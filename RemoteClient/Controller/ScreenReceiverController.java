package RemoteClient.Controller;
/*
 * Đây là lớp nhận ảnh từ máy host
 */

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ScreenReceiverController {

    private Socket imgSoc;
    private static ScreenReceiverController _instance;
    private DataInputStream imgDis;
    boolean isRunning = true;
    ByteArrayInputStream bais;
    BufferedImage image;

    private ScreenReceiverController(Socket imgSoc) {
        this.imgSoc = imgSoc;
        try {
            this.imgDis = new DataInputStream(imgSoc.getInputStream());
        } catch (Exception e) {
            System.out.println("Error in creating screen receiver");
        }
    }

    public static ScreenReceiverController getInstance(Socket imgSoc) {
        if (_instance == null) {
            _instance = new ScreenReceiverController(imgSoc);
        }
        return _instance;
    }

    public Image getImage() {
        while (imgSoc.isConnected() && isRunning) {
            try {
                int length = imgDis.readInt();
                byte[] data = new byte[length];
                int bytesRead = 0;
                while (bytesRead < length) {
                    int result = imgDis.read(data, bytesRead, length - bytesRead);
                    if (result == -1) {
                        throw new IOException("End of input stream reached before reading all image data");
                    }
                    bytesRead += result;
                }
                bais = new ByteArrayInputStream(data);
                image = ImageIO.read(bais);
                return image;
            } catch (Exception e) {
                System.out.println("Error receiving image");
                return null;
            }
        }
        return null;
    }

    public void disconnect() throws Exception {
        try {
            imgSoc.close();
            imgDis.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            throw new Exception("imgSoc");
        }
    }
}
