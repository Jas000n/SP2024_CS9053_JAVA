package NYU.SPJAVA.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReviewPanel extends JPanel {
    public ReviewPanel(ActionListener backListener) {
        setLayout(new FlowLayout());
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(backListener);
        add(backButton);
        // Additional initialization with parameters
    }
}
