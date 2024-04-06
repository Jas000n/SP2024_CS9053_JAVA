package NYU.SPJAVA.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//paint the swing frame, screenshot actually
public class Painter {
    public static void saveComponentAsImage(Component component,String filename) throws AWTException, IOException {
        Rectangle rect = component.getBounds();

        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(new Rectangle(component.getLocationOnScreen(), component.getSize()));
        String timestampAsString = String.valueOf(System.currentTimeMillis());
        ImageIO.write(image, "PNG", new File(filename+"_"+timestampAsString+".png"));
    }
}