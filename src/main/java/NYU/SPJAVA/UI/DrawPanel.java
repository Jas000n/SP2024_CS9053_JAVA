package NYU.SPJAVA.UI;

import NYU.SPJAVA.DBEntity.Line;
import NYU.SPJAVA.utils.Painter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class DrawPanel extends JPanel {
    private String UID; // User ID, the user that is playing this game
    private String word;  // The word to be drawn
    private int time_limit; // time limit for the word
    private int line_count = 0;
    private int prevX = -1, prevY = -1; // Previous coordinates for drawing.
    private Color currentColor = Color.BLACK; // Current drawing color, initially black.
    private int penWidth = 1; // Initial pen width.
    private boolean eraserMode = false; // Eraser mode, initially off.
    private JPanel drawingArea;
    private JToolBar toolBar;
    private ArrayList<Line> lines;
    public DrawPanel(String UID, String word, int time_limit) {
        this.UID = UID;
        this.word = word;
        this.time_limit = time_limit;
        this.lines = new ArrayList<>();
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

        this.add(toolBar, BorderLayout.NORTH);
        this.add(drawingArea, BorderLayout.CENTER);
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
//            Line tmp = new Line()
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Drawing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new DrawPanel("234","SmartPhone",150));
        frame.setVisible(true);
    }
}
