package NYU.SPJAVA.UI;

import NYU.SPJAVA.Connector.GameDBConnector;
import NYU.SPJAVA.Connector.PlayerDBConnector;
import NYU.SPJAVA.Connector.RedisConnector;
import NYU.SPJAVA.Connector.WordDBConnector;
import NYU.SPJAVA.DBEntity.*;
import NYU.SPJAVA.utils.Response;
import com.google.common.hash.Hashing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel cardPanel = new JPanel(cardLayout);
    boolean isLogin = false;
    Player player;
    RedisConnector redisConnector;
    WordDBConnector wordDBConnector;
    GameDBConnector gameDBConnector;

    public Main() throws Exception {
        setTitle("Picasso");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        redisConnector = new RedisConnector();
        wordDBConnector = new WordDBConnector();
        gameDBConnector = new GameDBConnector();
        initializeUI();
        add(cardPanel);
        setVisible(true);
    }

    private void retrieveOnlinePlayer(JScrollPane onlinePlayer) {
        int delay = 1000; // refresh every second
        ArrayList<PlayerVO> currentPlayers = new ArrayList<>();
        AtomicReference<String> lastInvitedMsg = new AtomicReference<>();

        new Timer(delay, (e) -> {
            ArrayList<PlayerVO> allOnlinePlayers = redisConnector.getAllOnlinePlayers();
            if (!allOnlinePlayers.equals(currentPlayers)) {
                currentPlayers.clear();
                currentPlayers.addAll(allOnlinePlayers);
                DefaultListModel<String> model = new DefaultListModel<>();
                for (PlayerVO player : allOnlinePlayers) {
                    model.addElement(player.getPlayerID() + ":" + player.getUname() + " (" + player.getStatus() + ")");
                }

                SwingUtilities.invokeLater(() -> {
                    JList<String> list = new JList<>(model);
                    onlinePlayer.setViewportView(list);
                    list.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent evt) {
                            invitePlayerMouseClick(evt, list, model);
                        }
                    });
                    onlinePlayer.revalidate();
                });
            }

            if (player != null && player.getPlayerID() != null) {
                DoubleGameRoom doubleGameRoom = redisConnector.retrieveRoomByInvitedID(player.getPlayerID());
                if (doubleGameRoom != null) {
                    String msg = "You are invited in a double game with player " + doubleGameRoom.getHostPlayerID() + " with word: " + doubleGameRoom.getWord();
                    if (!msg.equals(lastInvitedMsg.get())) {
                        int result = JOptionPane.showConfirmDialog(Main.this, msg, "Invitation", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            acceptInvitation(doubleGameRoom);
                        } else if (result == JOptionPane.OK_CANCEL_OPTION) {
                            System.out.println("player rejected the invitation!");
                            redisConnector.removeGameRoom(doubleGameRoom);

                        }
                        lastInvitedMsg.set(msg);
                    }
                }
            }
        }).start();
    }

    private void acceptInvitation(DoubleGameRoom doubleGameRoom) {
        try {
            doubleGameRoom.setStatus("accepted");
            redisConnector.updateGameRoom(doubleGameRoom);
            Word word = wordDBConnector.getWordByString(doubleGameRoom.getWord());
            Game doubleGame = new Game(word, player);
            Response gameCreateRes = gameDBConnector.createGame(doubleGame);
            doubleGame = (Game) gameCreateRes.data;
            DrawPanel singleGamePanel = new DrawPanel(doubleGame);
            redisConnector.updatePlayerStatus(new PlayerVO(String.valueOf(player.getPlayerID()), player.getUname(), "In-Game"));
            cardPanel.add(singleGamePanel, "SingleGame");
            cardLayout.show(cardPanel, "SingleGame");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void invitePlayerMouseClick(MouseEvent evt, JList<String> list, DefaultListModel<String> model) {
        if (evt.getClickCount() == 1) {
            int index = list.locationToIndex(evt.getPoint());
            if (index >= 0) {
                String item = model.getElementAt(index);
                int hostID = Main.this.player.getPlayerID();
                int invitedID = Integer.parseInt(item.split(":")[0]);
                if (hostID != invitedID) {
                    System.out.print(hostID + " invited " + invitedID + " in a double game ");
                    String word = ((ArrayList<Word>) (wordDBConnector.getWord(1).data)).get(0).word;
                    boolean result = redisConnector.hostInvitePlayer(hostID, invitedID, word);
                    if (result) {
                        System.out.println("and succeeded!");
                        int delay = 1000;
                        Timer checkingAcceptionTimer = null;
                        Timer finalCheckingAcceptionTimer = checkingAcceptionTimer;
                        checkingAcceptionTimer = new Timer(delay, (e) -> {
                            //check every second, whether the invited player accepted
                            DoubleGameRoom doubleGameRoom = redisConnector.retrieveRoomByHostID(hostID);
                            if (doubleGameRoom != null && Objects.equals(doubleGameRoom.getStatus(), "accepted")) {
                                //the other player accepted that invitation
                                try {
                                    Word word1 = wordDBConnector.getWordByString(doubleGameRoom.getWord());
                                    Game doubleGame = new Game(word1,Main.this.player);
                                    Response gameCreateRes = gameDBConnector.createGame(doubleGame);
                                    doubleGame= (Game) gameCreateRes.data;
                                    DrawPanel singleGamePanel = null;
                                    try {
                                        singleGamePanel = new DrawPanel(doubleGame);
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    redisConnector.updatePlayerStatus(new PlayerVO(String.valueOf(player.getPlayerID()), player.getUname(), "In-Game"));
                                    cardPanel.add(singleGamePanel, "SingleGame");
                                    cardLayout.show(cardPanel, "SingleGame");
                                    //stop timer here
                                    finalCheckingAcceptionTimer.stop();
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });

                        checkingAcceptionTimer.start();
                    } else {
                        System.out.println("and failed");
                    }
                }
            }
        }
    }

    private void initializeUI() throws Exception {
        PlayerDBConnector playerDBConnector = new PlayerDBConnector();
        WordDBConnector wordDBConnector = new WordDBConnector();
        GameDBConnector gameDBConnector = new GameDBConnector();

        JPanel menuPanel = new JPanel(new FlowLayout());
        JButton singleGameButton = new JButton("Single Game");
        JButton reviewButton = new JButton("Review");

        // Single game button event listener
        singleGameButton.addActionListener(e -> {
            Word wordToDraw = ((ArrayList<Word>) wordDBConnector.getWord(1).data).get(0);
            Game singleGame = new Game(wordToDraw, player);
            Response gameCreateRes = gameDBConnector.createGame(singleGame);
            singleGame = (Game) gameCreateRes.data;
            DrawPanel singleGamePanel = null;
            try {
                singleGamePanel = new DrawPanel(singleGame);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            redisConnector.updatePlayerStatus(new PlayerVO(String.valueOf(player.getPlayerID()), player.getUname(), "In-Game"));
            cardPanel.add(singleGamePanel, "SingleGame");
            cardLayout.show(cardPanel, "SingleGame");
        });

        reviewButton.addActionListener(e -> {
            ActionListener backListener = j -> cardLayout.show(cardPanel, "MainMenu");
            ReviewPanel reviewPanel = null;
            try {
                reviewPanel = new ReviewPanel(player, backListener);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            cardPanel.add(reviewPanel, "Review");
            redisConnector.updatePlayerStatus(new PlayerVO(String.valueOf(player.getPlayerID()), player.getUname(), "Reviewing"));
            cardLayout.show(cardPanel, "Review");
        });

        singleGameButton.setEnabled(isLogin);
        reviewButton.setEnabled(isLogin);
        menuPanel.add(singleGameButton);
        menuPanel.add(reviewButton);

        // Text fields for username and password
        JLabel uname = new JLabel("User Name:");
        menuPanel.add(uname);
        JTextField unameText = new JTextField(10);
        unameText.setBounds(100, 20, 165, 25);
        menuPanel.add(unameText);
        JLabel password = new JLabel("Password:");
        menuPanel.add(password);
        JPasswordField passwordText = new JPasswordField(10);
        passwordText.setBounds(100, 50, 165, 25);
        menuPanel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        menuPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(180, 80, 100, 25);
        menuPanel.add(registerButton);

        // Online players
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> gameModesList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(gameModesList);
        listScrollPane.setPreferredSize(new Dimension(200, 100));
        menuPanel.add(listScrollPane, BorderLayout.SOUTH);
        this.retrieveOnlinePlayer(listScrollPane);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = unameText.getText();
                char[] password = passwordText.getPassword();
                String hashedPassword = Hashing.sha256().hashString(new String(password), StandardCharsets.UTF_8).toString();
                Arrays.fill(password, '0');
                Player tmp = new Player(username, hashedPassword);
                Response response = playerDBConnector.login(tmp);
                if (response.code == Response.ResponseCode.SUCCESS) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    singleGameButton.setEnabled(true);
                    reviewButton.setEnabled(true);
                    isLogin = true;
                    unameText.setEditable(false);
                    passwordText.setText("");
                    passwordText.setEditable(false);
                    Main.this.player = (Player) response.data;
                    redisConnector.updatePlayerStatus(new PlayerVO(String.valueOf(Main.this.player.getPlayerID()), Main.this.player.getUname(), "Online"));
                } else {
                    JOptionPane.showMessageDialog(null, response.msg);
                    passwordText.setText("");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = unameText.getText();
                char[] password = passwordText.getPassword();
                Response response = playerDBConnector.register(new Player(username, Hashing.sha256().hashString(new String(password), StandardCharsets.UTF_8).toString()));
                Arrays.fill(password, '0');
                String msg = response.msg;
                JOptionPane.showMessageDialog(null, msg);
            }
        });

        cardPanel.add(menuPanel, "MainMenu");

        cardLayout.show(cardPanel, "MainMenu");  // Show the main menu first

        // Set default close operation to do nothing
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // Log out player on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Main.this.player != null && Main.this.player.getPlayerID() != null) {
                    //System.out.println(Main.this.player);
                    redisConnector.playerOffline(new PlayerVO(String.valueOf(Main.this.player.getPlayerID()), Main.this.player.getUname(), "Offline"));
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}
