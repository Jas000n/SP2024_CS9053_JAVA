package NYU.SPJAVA.UI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel cardPanel = new JPanel(cardLayout);

    public Main() {
        setTitle("Main Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);  // Center the frame

        initializeUI();
        add(cardPanel);
        setVisible(true);
    }

    private void initializeUI() {
        JPanel menuPanel = new JPanel(new FlowLayout());
        JButton singleGameButton = new JButton("Single Game");
        JButton reviewButton = new JButton("Review");

        singleGameButton.addActionListener(e -> cardLayout.show(cardPanel, "SingleGame"));
        reviewButton.addActionListener(e -> cardLayout.show(cardPanel, "Review"));

        menuPanel.add(singleGameButton);
        menuPanel.add(reviewButton);

        ActionListener backListener = e -> cardLayout.show(cardPanel, "MainMenu");

        DrawPanel singleGamePanel = new DrawPanel("234","SmartPhone",150);
        ReviewPanel reviewPanel = new ReviewPanel(backListener);

        cardPanel.add(menuPanel, "MainMenu");
        cardPanel.add(singleGamePanel, "SingleGame");
        cardPanel.add(reviewPanel, "Review");

        cardLayout.show(cardPanel, "MainMenu");  // Show the main menu first
    }

    public static void main(String[] args) {
        new Main();
    }
}