package Guest.GUI;

/*
 * Lớp này tạo ra một JPanel để hiển thị hình ảnh nhận về từ máy Host
 */

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.Socket;

import javax.swing.JPanel;

import Guest.Handler.EventSender;
import Guest.Handler.ScreenReceiver;

class VirtualScreenPanel extends JPanel {
    EventSender esInstance;
    ScreenReceiver srInstance;

    public VirtualScreenPanel(double hostScreenWidth, double hostScreenHeight, Socket imgSoc) {
        setLayout(null);
        setSize(1280, 720);
        esInstance = EventSender.getInstance((int) getWidth(), (int) getHeight(), (int) hostScreenWidth,
                (int) hostScreenHeight, imgSoc);
        addMouseListener(esInstance);
        addMouseMotionListener(esInstance);
        addKeyListener(esInstance);

        srInstance = ScreenReceiver.getInstance(imgSoc);
        new Thread(() -> {
            while (true) {
                try {
                    Image img = srInstance.getImage();
                    if (img == null) {
                        continue;
                    }
                    Graphics g = getGraphics();
//                    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
                } catch (Exception e) {
                    System.out.println("Error in drawing image");
                }
            }
        }).start();
    }

    // Hàm ngắt kết nối các luồng nhận màn hình và gửi sự kiện
    public void disconnect() {
        try {
            esInstance.disconnect();
            srInstance.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
