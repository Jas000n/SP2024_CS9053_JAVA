package NYU.SPJAVA.UI;

import NYU.SPJAVA.Connector.GameDBConnector;
import NYU.SPJAVA.Connector.PlayerDBConnector;
import NYU.SPJAVA.Connector.RedisConnector;
import NYU.SPJAVA.Connector.WordDBConnector;
import NYU.SPJAVA.DBEntity.Game;
import NYU.SPJAVA.DBEntity.Player;
import NYU.SPJAVA.DBEntity.PlayerVO;
import NYU.SPJAVA.DBEntity.Word;
import NYU.SPJAVA.utils.Response;
import com.google.common.hash.Hashing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel cardPanel = new JPanel(cardLayout);
    boolean isLogin = false;
    Player player;
    RedisConnector redisConnector;

    public Main() throws Exception {
        setTitle("Picasso");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);  // Center the frame
        redisConnector = new RedisConnector();
        initializeUI();
        add(cardPanel);
        setVisible(true);
    }
//    private void retrieveOnlinePlayer(JScrollPane onlinePlayer) {
//        int delay = 8000; //  refresh every 2 seconds
//        new javax.swing.Timer(delay, (e) -> {
//            ArrayList<PlayerVO> allOnlinePlayers = redisConnector.getAllOnlinePlayer();
//
//            DefaultListModel<String> model = new DefaultListModel<>();
//            for (PlayerVO player : allOnlinePlayers) {
//                model.addElement(player.getUname());
//            }
//            JList<String> list = new JList<>(model);
//            onlinePlayer.setViewportView(list);
//
//            SwingUtilities.invokeLater(() -> onlinePlayer.revalidate());
//
//            //jump to single game
//            WordDBConnector wordDBConnector = null;
//            GameDBConnector gameDBConnector = null;
//            try {
//                gameDBConnector = new GameDBConnector();
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//            try {
//                wordDBConnector = new WordDBConnector();
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//            System.out.println("clicked single game");
//            Word wordToDraw = ((ArrayList<Word>) wordDBConnector.getWord(1).data).get(0);
//            Game singleGame = new Game(wordToDraw, player);
//            Response gameCreateRes =  gameDBConnector.createGame(singleGame);
//            singleGame = (Game)gameCreateRes.data;
//            DrawPanel singleGamePanel = null;
//            try {
//                singleGamePanel = new DrawPanel(singleGame);
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//            cardPanel.add(singleGamePanel, "SingleGame");
//            cardLayout.show(cardPanel, "SingleGame");
//
//        }).start();
//    }


    private void initializeUI() throws Exception {
        PlayerDBConnector playerDBConnector = new PlayerDBConnector();
        WordDBConnector wordDBConnector = new WordDBConnector();
        GameDBConnector gameDBConnector = new GameDBConnector();

        JPanel menuPanel = new JPanel(new FlowLayout());
        JButton singleGameButton = new JButton("Single Game");
        JButton reviewButton = new JButton("Review");

        //single game button event listener
        singleGameButton.addActionListener(e -> {
            System.out.println("clicked single game");
            Word wordToDraw = ((ArrayList<Word>) wordDBConnector.getWord(1).data).get(0);
            Game singleGame = new Game(wordToDraw, player);
            Response gameCreateRes =  gameDBConnector.createGame(singleGame);
            singleGame = (Game)gameCreateRes.data;
            DrawPanel singleGamePanel = null;
            try {
                singleGamePanel = new DrawPanel(singleGame);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            cardPanel.add(singleGamePanel, "SingleGame");
            cardLayout.show(cardPanel, "SingleGame");
        });
        reviewButton.addActionListener(e -> cardLayout.show(cardPanel, "Review"));
        singleGameButton.setEnabled(isLogin);
        reviewButton.setEnabled(isLogin);
        menuPanel.add(singleGameButton);
        menuPanel.add(reviewButton);

        //text field for uname and password
        JTextField unameText = new JTextField(20);
        unameText.setBounds(100, 20, 165, 25);
        menuPanel.add(unameText);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        menuPanel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        menuPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(180, 80, 100, 25);
        menuPanel.add(registerButton);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> gameModesList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(gameModesList);
        listScrollPane.setPreferredSize(new Dimension(200, 100));
        menuPanel.add(listScrollPane,BorderLayout.SOUTH);
//        this.retrieveOnlinePlayer(listScrollPane);

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
                    Main.this.player = (Player)response.data;

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
                Response response= playerDBConnector.register(new Player(username,Hashing.sha256().hashString(new String(password), StandardCharsets.UTF_8).toString()));
                Arrays.fill(password, '0');
                String msg = response.msg;
                JOptionPane.showMessageDialog(null, msg);
            }
        });

        ActionListener backListener = e -> cardLayout.show(cardPanel, "MainMenu");


        ReviewPanel reviewPanel = new ReviewPanel(backListener);

        cardPanel.add(menuPanel, "MainMenu");

        cardPanel.add(reviewPanel, "Review");

        cardLayout.show(cardPanel, "MainMenu");  // Show the main menu first

    }


    public static void main(String[] args) throws Exception {
        new Main();
    }
}