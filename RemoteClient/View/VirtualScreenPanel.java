package RemoteClient.View;

/*
 * Lớp này tạo ra một JPanel để hiển thị hình ảnh nhận về từ máy Host
 */

import java.awt.Graphics;
import java.awt.Image;

import java.net.Socket;

import javax.swing.JPanel;

import RemoteClient.Controller.EventSenderController;
import RemoteClient.Controller.ScreenReceiverController;

class VirtualScreenPanel extends JPanel {
    EventSenderController esc;
    ScreenReceiverController src;

    public VirtualScreenPanel(double hostScreenWidth, double hostScreenHeight, Socket imgSoc) {
        setLayout(null);
        setSize(1280, 720);
        esc = EventSenderController.getInstance((int) getWidth(), (int) getHeight(), (int) hostScreenWidth,
                (int) hostScreenHeight, imgSoc);
        addMouseListener(esc);
        addMouseMotionListener(esc);
        addKeyListener(esc);

        src = ScreenReceiverController.getInstance(imgSoc);
        new Thread(() -> {
            while (true) {
                try {
                    Image img = src.getImage();
                    if (img == null) {
                        continue;
                    }
                    Graphics g = getGraphics();
                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
                } catch (Exception e) {
                    System.out.println("Error in drawing image");
                }
            }
        }).start();
    }

    // Hàm ngắt kết nối các luồng nhận màn hình và gửi sự kiện
    public void disconnect() {
        try {
            esc.disconnect();
            src.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
