package Guest.GUI;
/*
 * Lớp này tạo ra cửa sổ giao diện chính của Remote Client 
 */

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

public class GuestGUI extends JFrame {
    private final static int port = 4445;
    Socket imgSoc, msgSoc, fileSoc;
    double hostScreenWidth, hostScreenHeight;
    String folderPath = "C:\\RemoteDesktop";
    private JFrame mainFrame;
    private VirtualScreenPanel screenPanel;
    private JButton chatButton;
    private ChatWindow chatWindow;
    private boolean isChatOpen = false;
    private DataOutputStream GUI_dos;
    private String hostName;

    public GuestGUI(String ip, DataOutputStream GUI_dos, String hostName) {
        try {
            System.out.println(ip);
            imgSoc = new Socket(ip, port);
            msgSoc = new Socket(ip, port);
            fileSoc = new Socket(ip, port);
            this.GUI_dos = GUI_dos;
            this.hostName = hostName;
        } catch (Exception e) {
            System.out.println("Loi chay client");
            e.printStackTrace();
        }
        try {
            if (imgSoc.isConnected() && msgSoc.isConnected() && fileSoc.isConnected()) {
                DataInputStream dis = new DataInputStream(imgSoc.getInputStream());
                hostScreenWidth = dis.readDouble();
                hostScreenHeight = dis.readDouble();
            } else {
                System.out.println("Loi ket noi");
            }

            // Cửa sổ chính của Remote Client
            mainFrame = new JFrame("Remote Desktop Viewer");
            mainFrame.setLayout(new BorderLayout());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1280, 770);
            mainFrame.setPreferredSize(mainFrame.getSize());

            // Panel chứa lớp hiển thị màn hình VirtualScreenPanel
            screenPanel = new VirtualScreenPanel(hostScreenWidth, hostScreenHeight, imgSoc);
            screenPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    screenPanel.requestFocusInWindow();
                }
            });
            mainFrame.add(screenPanel, BorderLayout.CENTER);

            // Tạo một toolbar chứa các nút chức năng
            JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);

            chatButton = new JButton("Trò chuyện");
            chatButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openChatWindow(msgSoc, fileSoc, folderPath);
                }
            });
            toolbar.add(chatButton);

            JButton disconnectButton = new JButton("Ngắt kết nối");
            disconnectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Disconnecting...");
                    disconnect();
                }
            });
            toolbar.add(disconnectButton);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Disconnecting...");
                    disconnect();
                }
            });

            mainFrame.add(toolbar, BorderLayout.NORTH);
            mainFrame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm ngắt kết nối các luồng và gửi sự kiện ngắt đến nối đến server để xử lý
    public void disconnect() {
        try {
            screenPanel.disconnect();
            mainFrame.dispose();
            chatWindow.disconnect();
            chatWindow.dispose();
            dispose();
        } catch (Exception e) {
            System.out.println("Loi dong ket noi");
        }

        try {
            GUI_dos.writeUTF("disconnect");
            GUI_dos.flush();
        } catch (IOException e) {
            System.out.println("Loi gui thong tin");
        }
    }

    // Hàm mở cửa sổ chat
    private void openChatWindow(Socket msgSoc, Socket fileSoc, String folderPath) {
        // Chỉ mở một cửa sổ chat
        if (chatWindow == null) {
            chatWindow = new ChatWindow(msgSoc, fileSoc, folderPath, hostName);

            // Cửa sổ chat sẽ hiển thị ở dưới thanh toolbar
            Point mainFrameLocation = mainFrame.getLocation();
            chatWindow.setLocation(mainFrameLocation.x, mainFrameLocation.y + 50);
        }
        isChatOpen = !isChatOpen;
        chatWindow.setVisible(isChatOpen);
    }
}
