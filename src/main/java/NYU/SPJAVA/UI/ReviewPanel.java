package NYU.SPJAVA.UI;

import NYU.SPJAVA.Connector.LineDBConnector;
import NYU.SPJAVA.Connector.PictureDBConnector;
import NYU.SPJAVA.DBEntity.Line;
import NYU.SPJAVA.DBEntity.Picture;
import NYU.SPJAVA.utils.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReviewPanel extends JPanel {
    private int line_count = 0;
    private int prevX = -1, prevY = -1; // Previous coordinates for drawing.
    private Color currentColor = Color.BLACK; // Current drawing color, initially black.
    private int penWidth = 1; // Initial pen width.
    private boolean eraserMode = false; // Eraser mode, initially off.
    private List<Line> lines = new ArrayList<>(); // List to store lines
    private LineDBConnector lineDBConnector;
    private int picture_ID;

    public ReviewPanel(ActionListener backListener) throws Exception {
        setLayout(new BorderLayout());
        lineDBConnector = new LineDBConnector();
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(backListener);
        controlPanel.add(backButton);

        JTextField textField = new JTextField(10);
        controlPanel.add(textField);

        JButton submitButton = new JButton("Submit");
        JPanel drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Default painting behavior.
            }
        };
        drawingArea.setBackground(Color.WHITE); // Set background color of the drawing area.

        add(drawingArea, BorderLayout.CENTER);


        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                picture_ID = Integer.parseInt(textField.getText());
                ArrayList<Line> drawnLine = getDrawnLine();
                paintLines(drawingArea.getGraphics(),drawnLine);

            }
        });
        controlPanel.add(submitButton);

        add(controlPanel, BorderLayout.NORTH);


    }

    private void paintLines(Graphics g, ArrayList<Line> lines) {
        // 先按时间戳对线条进行排序
        lines.sort(Comparator.comparingLong(Line::getTime));

        long prevTime = 0;
        for (Line line : lines) {
            long currentTime = line.getTime();
            if (prevTime != 0) {
                // 计算前一个线条和当前线条之间的时间差(以毫秒为单位)
                long timeDiff = currentTime - prevTime;
                try {
                    // 休眠相应的时间
                    Thread.sleep(timeDiff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Color tmp = new Color(line.getColor_r(), line.getColor_g(), line.getColor_b());
            g.setColor(eraserMode ? getBackground() : tmp);
            ((Graphics2D) g).setStroke(new BasicStroke(line.getPen_size()));
            g.drawLine(line.getPre_x(), line.getPre_y(), line.getX(), line.getY());

            prevTime = currentTime;
        }
    }
    public  ArrayList<Line> getDrawnLine() {
        LineDBConnector dbConnector = this.lineDBConnector;
        Picture tmp = new Picture();
        tmp.setPictureID(this.picture_ID);
        ArrayList<Line> lines = (ArrayList<Line>) dbConnector.getLines(tmp).data;
//        for(Line line : lines) {
//            System.out.println(line);
//        }
        return lines;
    }
}

