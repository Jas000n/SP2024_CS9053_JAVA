package NYU.SPJAVA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleDrawing extends JFrame {
    private int line_count = 0;
    private int prevX = -1, prevY = -1; // Previous coordinates for drawing.
    private Color currentColor = Color.BLACK; // Current drawing color, initially black.
    private int penWidth = 1; // Initial pen width.
    private boolean eraserMode = false; // Eraser mode, initially off.

    public SimpleDrawing() {
        super("Simple Drawing Application"); // Window title.
        setSize(800, 600); // Window size.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation.
        setLocationRelativeTo(null); // Center window on screen.

        // Create the drawing area.
        JPanel drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Default painting behavior.
            }
        };
        drawingArea.setBackground(Color.WHITE); // Set background color of the drawing area.

        // Mouse motion listener for drawing lines.
        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g = drawingArea.getGraphics();
                // Set the drawing color: currentColor if in drawing mode, white if in eraser mode.
                g.setColor(eraserMode ? drawingArea.getBackground() : currentColor);
                ((Graphics2D) g).setStroke(new BasicStroke(penWidth)); // Set pen width.
                int x = e.getX();
                int y = e.getY();
                if (prevX != -1 && prevY != -1) {
                    g.drawLine(prevX, prevY, x, y);
                    line_count+=1;
                    System.out.println(String.format("line count:%d, prevX:%d,prevY:%d,X:%d,Y:%d,color:%s,width:%d",line_count,prevX,prevY,x,y,currentColor,penWidth ));
                }
                prevX = x;
                prevY = y;
                g.dispose();
            }
        });

        // Reset the previous point on mouse release.
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                prevX = -1;
                prevY = -1;
            }
        });

        // Create a toolbar for changing color, pen width, and toggling the eraser.
        JToolBar toolBar = new JToolBar();
        JButton changeColorButton = new JButton("Change Color");
        changeColorButton.addActionListener(e -> {
            if (!eraserMode) { // Only allow changing colors if eraser mode is not active
                Color newColor = JColorChooser.showDialog(SimpleDrawing.this, "Choose a color", currentColor);
                if (newColor != null) {
                    currentColor = newColor;
                }
            }
        });
        toolBar.add(changeColorButton);

        // Add a combo box for selecting pen width.
        String[] penWidths = {"1", "2", "4", "8"};
        JComboBox<String> penWidthBox = new JComboBox<>(penWidths);
        penWidthBox.addActionListener(e -> {
            penWidth = Integer.parseInt((String) penWidthBox.getSelectedItem());
        });
        toolBar.add(penWidthBox);

        // Add a button to toggle eraser mode.
        JToggleButton eraserButton = new JToggleButton("Eraser");
        
        eraserButton.addActionListener(e -> {
           eraserMode = eraserButton.isSelected(); // Toggle eraser mode.
            if (eraserMode) {
                changeColorButton.setEnabled(false); // Disable color change while erasing.
            } else {
                changeColorButton.setEnabled(true); // Enable color change when not erasing.
            }
        });
        toolBar.add(eraserButton);

        // Layout management.
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.NORTH);
        this.add(drawingArea, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleDrawing().setVisible(true);
        });
    }
}
