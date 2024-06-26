package NYU.SPJAVA.UI;

import NYU.SPJAVA.Connector.*;
import NYU.SPJAVA.DBEntity.*;
import NYU.SPJAVA.NetworkEntity.ChatGPTResponse;
import NYU.SPJAVA.utils.Painter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

public class DrawPanel extends JPanel {
//    private String UID; // User ID, the user that is playing this game
//    private String word;  // The word to be drawn
//    private int time_limit; // time limit for the word
    private Game game;
    private Picture picture;
    private int line_count = 0;
    private int prevX = -1, prevY = -1; // Previous coordinates for drawing.
    private Color currentColor = Color.BLACK; // Current drawing color, initially black.
    private int penWidth = 1; // Initial pen width.
    private boolean eraserMode = false; // Eraser mode, initially off.
    private JPanel drawingArea;
    private JToolBar toolBar;
    private LineDBConnector lineDBConnector;
    private ArrayList<Line> lines;
    private GameDBConnector gameDBConnector;
    private PictureDBConnector picDBConnector;
    private RedisConnector redisConnector;
    public DrawPanel(Game singleGame) throws Exception {
        this.game = singleGame;
        this.picDBConnector = new PictureDBConnector();
        this.picture = (Picture) picDBConnector.createPicture(new Picture(singleGame,singleGame.getCreator())).data;
        this.lines = new ArrayList<>();
        this.lineDBConnector = new LineDBConnector();
        this.gameDBConnector = new GameDBConnector();
        this.redisConnector = new RedisConnector();
        initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());

        // Create the drawing area.
        drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        drawingArea.setBackground(Color.WHITE); // Set background color
        addMouseMotionListeners();
        addMouseListener();

        // Create toolbar with drawing controls
        setupToolBar();

        // Add the toolbar to the top (North)
        this.add(toolBar, BorderLayout.NORTH);
        // Add the drawing area to the center
        this.add(drawingArea, BorderLayout.CENTER);

        // Create a label for words and set the font
        JLabel wordLabel = new JLabel(game.getWord().word);
        wordLabel.setFont(new Font("Serif", Font.BOLD, 24)); // Set font name, style, and size

        // Create a countdown timer label
        JLabel countdownLabel = new JLabel("30", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 30));
        countdownLabel.setForeground(Color.RED); // Set the font color to red
        countdownLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding around the label

        // Setup timer to update the countdown label
        Timer timer = new Timer(1000, new ActionListener() {
            int timeLeft = game.getTimeLimit();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    countdownLabel.setText("Time Left:"+String.valueOf(--timeLeft));
                } else {
                    ((Timer) e.getSource()).stop();
                    countdownLabel.setText("Time's up!");
                }
            }
        });
        timer.start();

        // Create a submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set button font
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] scoreAndComment = submitPicture();
                    DoubleGameRoom doubleGameRoom = redisConnector.retrieveRoomByInvitedID(game.getCreator().getPlayerID());
                    if (doubleGameRoom != null) {
                        // This player is the invited player
                        doubleGameRoom = redisConnector.retrieveRoomByInvitedID(game.getCreator().getPlayerID());
                        doubleGameRoom.setInvitedScore(Integer.parseInt(scoreAndComment[0]));
                        redisConnector.updateGameRoom(doubleGameRoom);
                        Timer checkHostScore = null;
                        Timer finalCheckHostScore = checkHostScore;
                        checkHostScore = new Timer(1000, e1 -> {
                            DoubleGameRoom tmp = redisConnector.retrieveRoomByInvitedID(game.getCreator().getPlayerID());
                            if (tmp.getHostScore() != 0) {
                                System.out.println("Host player got score:" + tmp.getHostScore());
                                String msg = generateCompetitionResult("invited", tmp);
                                System.out.println(msg);
                                JOptionPane.showMessageDialog(null, msg);
                                finalCheckHostScore.stop();
                            }
                        });
                        checkHostScore.start();
                    }else{
                        //this player is the host
                        doubleGameRoom = redisConnector.retrieveRoomByHostID(game.getCreator().getPlayerID());
                        doubleGameRoom.setHostScore(Integer.parseInt(scoreAndComment[0]));
                        redisConnector.updateGameRoom(doubleGameRoom);
                        Timer checkInvitedScore = null;
                        Timer finalCheckInvitedScore = checkInvitedScore;
                        checkInvitedScore = new Timer(1000, e1 -> {
                            DoubleGameRoom tmp = redisConnector.retrieveRoomByHostID(game.getCreator().getPlayerID());
                            if(tmp.getInvitedScore()!=0){
                                System.out.println("Invited player got score:"+tmp.getInvitedScore());
                                String msg = generateCompetitionResult("host",tmp);
                                System.out.println(msg);
                                JOptionPane.showMessageDialog(null, msg);
                                finalCheckInvitedScore.stop();
                            }

                        });
                        checkInvitedScore.start();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }
                timer.stop();
//                JOptionPane.showMessageDialog(null, "Picture saved!");

            }
        });

        // Create a panel to hold both labels and the button, and add it to the south
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(wordLabel, BorderLayout.WEST);
        bottomPanel.add(submitButton, BorderLayout.CENTER);
        bottomPanel.add(countdownLabel, BorderLayout.EAST);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }


    private void addMouseMotionListeners() {
        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawLine(e);
            }
        });
    }

    private void addMouseListener() {
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                resetDrawing(e);
            }
        });
    }

    private void drawLine(MouseEvent e) {
        Graphics g = drawingArea.getGraphics();
        g.setColor(eraserMode ? drawingArea.getBackground() : currentColor);
        ((Graphics2D) g).setStroke(new BasicStroke(penWidth));
        int x = e.getX();
        int y = e.getY();
        if (prevX != -1 && prevY != -1) {
            g.drawLine(prevX, prevY, x, y);
            line_count++;
            System.out.println(String.format("line count:%d, prevX:%d,prevY:%d,X:%d,Y:%d,color:%s,width:%d",line_count,prevX,prevY,x,y,currentColor,penWidth ));
            Line tmp = new Line(picture.getPictureID(),penWidth,currentColor.getRed(),currentColor.getGreen(),currentColor.getBlue(),System.currentTimeMillis(),
                    eraserMode,prevX,prevY,x,y);
            lines.add(tmp);
        }
        prevX = x;
        prevY = y;
        g.dispose();
    }

    private void resetDrawing(MouseEvent e) {
        prevX = -1;
        prevY = -1;
    }

    private void setupToolBar() {
        toolBar = new JToolBar();
        JButton changeColorButton = new JButton("Change Color");
        changeColorButton.addActionListener(this::changeColor);
        toolBar.add(changeColorButton);

        JLabel pensizeLabel = new JLabel("Pen Size:");
        toolBar.add(pensizeLabel);
        JComboBox<String> penWidthBox = new JComboBox<>(new String[]{"1", "2", "4", "8"});
        penWidthBox.addActionListener(e -> penWidth = Integer.parseInt((String) penWidthBox.getSelectedItem()));
        toolBar.add(penWidthBox);

        JToggleButton eraserButton = new JToggleButton("Eraser");
        eraserButton.addActionListener(this::toggleEraser);
        toolBar.add(eraserButton);
    }

    private void changeColor(ActionEvent e) {
        if (!eraserMode) {
            Color newColor = JColorChooser.showDialog(this, "Choose a color", currentColor);
            if (newColor != null) {
                currentColor = newColor;
            }
        }
    }

    private void toggleEraser(ActionEvent e) {
        eraserMode = !eraserMode;
        ((JButton)toolBar.getComponent(0)).setEnabled(!eraserMode);  // Enable/Disable color button based on eraser mode
    }
    private String[] submitPicture() throws IOException, AWTException {
        // save all lines in DB
        lineDBConnector.saveLines(lines);
        // save screenshot of the picture
        Painter.saveComponentAsImage(drawingArea,"src/main/resources/Pics/"+this.picture.getPictureID());
                System.out.println("Saved Pic "+this.picture.getPictureID()+"!");
        // send picture to ChatGPT connector and retrieve result
        ChatGPTResponse chatGPTResponse = ChatGPTConnector.sendPostRequest(("src/main/resources/Pics/" + this.picture.getPictureID())+".png", this.game.getWord().word);
        String score = chatGPTResponse.getScore();
        String comment = chatGPTResponse.getComment();
        //update picture in DB
        this.picture.setRemark(comment);
        this.picture.setScore(Integer.parseInt(score));
        this.picDBConnector.updatePicture(this.picture);
        // show score and comment from ChatGPT to user
        JOptionPane.showMessageDialog(null, "Score: "+score+" Comment: "+comment);
        return new String[]{score, comment};
    }
    //generate result msg according to the scores of both player, if both player checked Score, delete that record from redis
    private String generateCompetitionResult(String player_identity, DoubleGameRoom doubleGameRoom){
        int hostScore = doubleGameRoom.getHostScore();
        int invitedScore = doubleGameRoom.getInvitedScore();
        String word = doubleGameRoom.getWord();
        String msg = "";

        if(player_identity.equals("host")){
            if(hostScore > invitedScore){
                msg = "You win the game on word: " + word + ". You got: " + hostScore + " points, your opponent player " + doubleGameRoom.getInvitedPlayerID() + " got " + invitedScore + " points.";
            } else if (hostScore < invitedScore) {
                msg = "You lose the game on word: " + word + ". You got: " + hostScore + " points, your opponent player " + doubleGameRoom.getInvitedPlayerID() + " got " + invitedScore + " points.";
            } else {
                msg = "The game on word: " + word + " ended in a draw. You and invited player:"+doubleGameRoom.getInvitedPlayerID()+" scored " + hostScore + " points.";
            }
            doubleGameRoom.numOfCheckPlusPlus();
            redisConnector.updateGameRoom(doubleGameRoom);
            if(doubleGameRoom.getNumOfCheck()==2){
                System.out.println("Both player checked score!");
                redisConnector.removeGameRoom(doubleGameRoom);
            }

        }else {
            if(invitedScore > hostScore){
                msg = "You win the game on word: " + word + ". You got: " + invitedScore + " points, your opponent player " + doubleGameRoom.getHostPlayerID() + " got " + hostScore + " points.";
            } else if (invitedScore < hostScore) {
                msg = "You lose the game on word: " + word + ". You got: " + invitedScore + " points, your opponent player " + doubleGameRoom.getHostPlayerID() + " got " + hostScore + " points.";
            } else {
                msg = "The game on word: " + word + " ended in a draw. You and host player:" + doubleGameRoom.getHostPlayerID() + " scored " + invitedScore + " points.";
            }
            doubleGameRoom.numOfCheckPlusPlus();
            redisConnector.updateGameRoom(doubleGameRoom);
            if(doubleGameRoom.getNumOfCheck()==2){
                System.out.println("Both players checked the score!");
                redisConnector.removeGameRoom(doubleGameRoom);
            }
        }

        return msg;
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Drawing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        Word test_word = new Word(1,"smart phone");
        Player test_player = new Player(1,"admin","whatever");
        Game singleGame = new Game(test_word,test_player);
        frame.add(new DrawPanel(singleGame));
        frame.setVisible(true);
    }
}
