package NYU.SPJAVA.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

//convert a picture to Base64 encode, so that we can send that to chatGPT
public class ImageToBase64 {


    public static String convertImageToBase64(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "png", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static void main(String[] args) {
        String imagePath = "src/main/resources/Pics/Test.png";

        try {
            String base64String = convertImageToBase64(imagePath);
            System.out.println("Base64 String: ");
            System.out.println(base64String);
        } catch (IOException e) {
            System.err.println("Error converting image to Base64: " + e.getMessage());
        }
    }
}
