import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Guest.GUI.GuestGUI;
import Host.GUI.HostGUI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.SwingConstants;

public class RemoteDesktop extends JFrame {

    private JPanel contentPane;

    private JTextField tfMyID;
    private JTextField tfMyPassword;
    private JTextField tfOpponentPassword;
    private JTextField tfOpponentID;
    private JTextField tfMyName;
    private JButton btnConnect;
    private JButton btnChangeName;
    private JButton btnCopy;
    private JButton btnChangePassword;

    private userInfo user = new userInfo();

    private Socket soc;
    private DataInputStream dis;
    private DataOutputStream dos;

    static String ipAddress = "192.168.1.3";
    static int port = 4444;

    private HostGUI host;
    private GuestGUI client;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    RemoteDesktop frame = new RemoteDesktop();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public RemoteDesktop() {
        if (user.isExist() == false) {
            String name = null;
            while (true) {
                name = JOptionPane.showInputDialog(null,
                        "Nhập tên của bạn:",
                        "Chào mừng",
                        JOptionPane.PLAIN_MESSAGE);
                if (name == null || name.trim().isEmpty())
                    JOptionPane.showMessageDialog(null,
                            "Bạn chưa nhập tên. Chương trình sẽ thoát.",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                else
                    break;
            }
            user.createFile(name);
        }

        String[] info = user.getInfo();
        String myName = info[0];
        String myID = info[1];
        try {
            soc = new Socket(ipAddress, port);
            
            dis = new DataInputStream(soc.getInputStream());
            dos = new DataOutputStream(soc.getOutputStream());

            // Phan xu li voi server
            // -----------------------------------------------------------------------
            dos.writeUTF(myID);
            dos.writeUTF(myName);
            new Thread(() -> {
                try {
                    while (true) {
                        String req = dis.readUTF();
                        System.out.println(req);
                        switch (req) {
                            /*
                             * 1. refuse_00: Kiem tra ID khong ton tai
                             * 2. refuse_01: Kiem tra mat khau khong dung
                             * 3. refuse_02: Host dang ket noi voi nguoi khac
                             * 4. refuse_03: Host tu choi ket noi
                             * 5. accept: Host dong y ket noi
                             * 6. request: Yeu cau ket noi tu Remote User
                             */
                            case "request":
                                if (!dis.readUTF().equals(tfMyPassword.getText())) {
                                    dos.writeUTF("refuse_01");
                                    dos.flush();
                                    break;
                                }
                                String guestName = dis.readUTF();
                                int response = JOptionPane.showConfirmDialog(
                                        null,
                                        "[Client: " + guestName + " ] muốn kết nối tới. Bạn có đồng ý không?",
                                        "Xác nhận",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                                switch (response) {
                                    case JOptionPane.YES_OPTION:
                                        // Chay Host
                                        dos.writeUTF("accept");
                                        dos.flush();
                                        System.out.println("Run host");
                                        host = new HostGUI(dos, myName);
                                        break;
                                    case JOptionPane.NO_OPTION:
                                    default:
                                        dos.writeUTF("refuse_03");
                                        dos.flush();
                                        break;
                                }
                                break;
                            case "accept":
                                // Chay client
                                String ip = dis.readUTF();
                                System.out.println("Run client");
                                Thread.sleep(1000);
                                client = new GuestGUI(ip, dos, myName);
                                break;
                            case "refuse_00":
                                JOptionPane.showMessageDialog(null, "ID không tồn tại!");
                                break;
                            case "refuse_01":
                                JOptionPane.showMessageDialog(null, "Mật khẩu không đúng!");
                                break;
                            case "refuse_02":
                                JOptionPane.showMessageDialog(null, "Host đang kết nối với người khác!");
                                break;
                            case "refuse_03":
                                JOptionPane.showMessageDialog(null, "Host từ chối kết nối!");
                                break;
                            case "disconnect":
                                if (host != null) {
                                    host.disconnect();
                                    JOptionPane.showMessageDialog(null, "Guest đã ngắt kết nối!");
                                    host = null;
                                }
                                if (client != null) {
                                    client.disconnect();
                                    JOptionPane.showMessageDialog(null, "Host đã ngắt kết nối!");
                                    client = null;
                                }
                                break;
                            default:
                                System.out.println("Req khong hop le");
                                break;
                        }
                        setButton(true);
                    }
                } catch (Exception e) {
                }
            }).start();
            // -----------------------------------------------------------------------------------------------

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server đang bận!");
            System.exit(1);
        }

        setAutoRequestFocus(false);
        setTitle("Ứng dụng điều khiển máy tính từ xa");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 362);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lbOpponentID = new JLabel("ID đối tác");
        lbOpponentID.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbOpponentID.setBounds(445, 96, 104, 40);
        contentPane.add(lbOpponentID);

        JLabel lbOpponentPassword = new JLabel("Mật khẩu ");
        lbOpponentPassword.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbOpponentPassword.setBounds(445, 158, 104, 40);
        contentPane.add(lbOpponentPassword);

        tfOpponentPassword = new JTextField();
        tfOpponentPassword.setColumns(10);
        tfOpponentPassword.setBounds(559, 162, 190, 44);
        contentPane.add(tfOpponentPassword);

        tfOpponentID = new JTextField();
        tfOpponentID.setColumns(10);
        tfOpponentID.setBounds(560, 100, 190, 44);
        contentPane.add(tfOpponentID);

        btnConnect = new JButton("Kết nối");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tfOpponentID.getText().isEmpty() || tfOpponentPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                    return;
                }
                toggleConnection();
            }
        });
        btnConnect.setBackground(new Color(163, 216, 255));
        btnConnect.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConnect.setBounds(598, 216, 151, 66);
        contentPane.add(btnConnect);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(163, 216, 255));
        panel.setBounds(0, 0, 400, 325);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lbMyInfo = new JLabel("Thông tin của bạn");
        lbMyInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lbMyInfo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbMyInfo.setBounds(98, 10, 193, 39);
        panel.add(lbMyInfo);

        btnChangePassword = new JButton("Đổi mật khẩu");
        btnChangePassword.setBounds(201, 251, 143, 34);
        panel.add(btnChangePassword);
        btnChangePassword.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnCopy = new JButton("Sao chép");
        btnCopy.setBounds(79, 251, 112, 34);
        panel.add(btnCopy);
        btnCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyInfo();
            }
        });
        btnCopy.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tfMyPassword = new JTextField(randomPassword());
        tfMyPassword.setBounds(180, 197, 190, 44);
        panel.add(tfMyPassword);
        tfMyPassword.setEditable(false);
        tfMyPassword.setColumns(10);

        JLabel lbMyPassword = new JLabel("Mật khẩu ");
        lbMyPassword.setBounds(39, 197, 104, 40);
        panel.add(lbMyPassword);
        lbMyPassword.setFont(new Font("Segoe UI", Font.BOLD, 20));

        tfMyID = new JTextField(myID);
        tfMyID.setBounds(180, 143, 190, 44);
        panel.add(tfMyID);
        tfMyID.setEditable(false);
        tfMyID.setColumns(10);

        JLabel lbMyID = new JLabel("ID của tôi");
        lbMyID.setBounds(39, 143, 104, 40);
        panel.add(lbMyID);
        lbMyID.setFont(new Font("Segoe UI", Font.BOLD, 20));

        tfMyName = new JTextField(myName);
        tfMyName.setBounds(180, 89, 190, 44);
        panel.add(tfMyName);
        tfMyName.setColumns(10);

        JLabel lbMyName = new JLabel("Tên của tôi");
        lbMyName.setBounds(39, 93, 130, 40);
        panel.add(lbMyName);
        lbMyName.setFont(new Font("Segoe UI", Font.BOLD, 20));

        btnChangeName = new JButton("Đổi tên");
        btnChangeName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeName();
            }
        });
        btnChangeName.setBounds(285, 66, 85, 21);
        panel.add(btnChangeName);

        JLabel lbOpponentInfo = new JLabel("Thông tin đối tác");
        lbOpponentInfo.setBackground(new Color(255, 255, 255));
        lbOpponentInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lbOpponentInfo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbOpponentInfo.setBounds(509, 10, 193, 39);
        contentPane.add(lbOpponentInfo);
        btnChangePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tfMyPassword.setText(randomPassword());
            }
        });
    }

    private void setButton(boolean state) {
        btnConnect.setEnabled(state);
        btnChangeName.setEnabled(state);
        btnCopy.setEnabled(state);
        btnChangePassword.setEnabled(state);
    }

    private String randomPassword() {
        Random rand = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6;
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    // Hàm kích hoạt kết nối
    private void toggleConnection() {
        setButton(false);
        try {
            if (tfOpponentID.getText().equals(tfMyID.getText())) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối với chính mình!");
                return;
            }
            dos.writeUTF("connect");
            dos.writeUTF(tfOpponentID.getText());
            dos.writeUTF(tfOpponentPassword.getText());
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hàm thay đổi tên
    private void changeName() {
        try {
            dos.writeUTF("changeName");
            user.changeName(tfMyName.getText());
            dos.writeUTF(tfMyName.getText());
            JOptionPane.showMessageDialog(this, "Change name successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Change name failed!");
            throw new RuntimeException(e);
        }
    }

    private void copyInfo() {
        String info = "ID: " + tfMyID.getText() + "\n" + "Password: " + tfMyPassword.getText();
        JOptionPane.showMessageDialog(this, "Đã sao chép ID và Password", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        // create clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new java.awt.datatransfer.StringSelection(info), null);
    }
}
