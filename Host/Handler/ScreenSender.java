package Host.Handler;
/*
 * Đây là class ScreenSenderController để gửi màn hình cho client
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ScreenSender implements Runnable {
    private Socket imgSoc;
    private static ScreenSender _instance;
    Robot robot;
    BufferedImage img;
    Rectangle screen;
    ByteArrayOutputStream baos;
    DataOutputStream imgDos;
    byte[] data;
    boolean isRunning = true;

    private ScreenSender(Socket imgSoc) {
        this.imgSoc = imgSoc;
        try {
            imgDos = new DataOutputStream(imgSoc.getOutputStream());
            robot = new Robot();
            screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            baos = new ByteArrayOutputStream();
        } catch (Exception e) {
            System.out.println("Error in creating screen sender");
        }
    }

    public static ScreenSender getInstance(Socket imgSoc) {
        if (_instance == null) {
            _instance = new ScreenSender(imgSoc);
        }
        return _instance;
    }

    @Override
    public synchronized void run() {
        while (isRunning && imgSoc.isConnected()) {
            try {
                img = robot.createScreenCapture(screen);

                ImageIO.write(img, "jpeg", baos);
                data = baos.toByteArray();

                imgDos.writeInt(data.length);
                imgDos.write(data);
                imgDos.flush();

                baos.reset();
                Thread.sleep(30);
            } catch (Exception e) {
                System.out.println("Error in sending screen");

            }
        }
    }

    public void disconnect() {
        try {
            imgDos.close();
            baos.close();
            imgSoc.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            System.out.println("loi");
        }
    }
}
