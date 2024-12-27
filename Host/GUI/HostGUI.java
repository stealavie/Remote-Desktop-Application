package Host.GUI;
/*
 * Đây là cửa sổ chính của Host
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Host.Handler.EventReceiver;
import Host.Handler.ScreenSender;
import Host.Object.Screen;

public class HostGUI extends JFrame {
    boolean checkButton = false;

    private final static int port = 4445;
    private Socket imgSoc, msgSoc, fileSoc;
    private ServerSocket server;
    private ScreenSender screenSenderInstance;
    private EventReceiver eventInstance;
    private DataOutputStream server_dos;
    chatInterface interface1;
    boolean ischatBoxVisible = false;

    Thread screenSenderThread;
    Thread eventThread;

    String folderPath = "D:\\RemoteDesktop";

    public HostGUI(DataOutputStream server_dos, String clientName) {
        try {
            // Khởi tạo server
            server = new ServerSocket(port);
            imgSoc = server.accept();
            msgSoc = server.accept();
            fileSoc = server.accept();
            this.server_dos = server_dos;

            if (imgSoc.isConnected() && msgSoc.isConnected() && fileSoc.isConnected()) {
                Screen screen = new Screen();
                DataOutputStream dos = new DataOutputStream(imgSoc.getOutputStream());

                // Gửi resolution của Host
                dos.writeDouble(screen.getWidth());
                dos.writeDouble(screen.getHeight());
                dos.flush();
            }

            screenSenderInstance = ScreenSender.getInstance(imgSoc);
            eventInstance = EventReceiver.getInstance(imgSoc);

            interface1 = new chatInterface(msgSoc, fileSoc, folderPath, clientName);

            screenSenderThread = new Thread(screenSenderInstance);
            screenSenderThread.start();

            eventThread = new Thread(eventInstance);
            eventThread.start();
        } catch (Exception e) {
            System.out.println("loi");
        }

        int frameWidth = 300;
        int frameHeight = 100;

        setLayout(new GridLayout(2, 1));
        setBounds(0, 0, frameWidth, frameHeight);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel("Đang kết nối!!");
        infoPanel.add(label);
        add(infoPanel);

        JPanel iPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton chatBt = new JButton("Trò chuyện");
        chatBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ischatBoxVisible = !ischatBoxVisible;
                interface1.setVisible(ischatBoxVisible);
            }
        });

        JButton disBt = new JButton("Ngắt kết nối");
        disBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }

        });
        JButton miniBt = new JButton("_");
        miniBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minimize();
            }

        });

        iPanel.add(chatBt);
        iPanel.add(disBt);
        iPanel.add(miniBt);
        add(iPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Disconnecting...");
                disconnect();
            }
        });

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public void minimize() {
        this.setState(JFrame.ICONIFIED);
    }

    public void disconnect() {
        try {
            eventInstance.disconnect();
            screenSenderInstance.disconnect();
            interface1.disconnect();
            interface1.dispose();
            dispose();
        } catch (Exception e) {
            System.out.println("loi");
        }

        try {
            server_dos.writeUTF("disconnect");
            server_dos.flush();
            server.close();
        } catch (Exception e) {
            System.out.println("loi");
        }

    }
}
