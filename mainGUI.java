import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import java.awt.event.ActionEvent;

import RemoteClient.View.ClientGUi;
import Host.View.HostGUI;

public class mainGUI extends JFrame {
    JTextField idField, ridField, passField, rpassField, nameField;
    JLabel errLabel;

    private final static int port = 4444;
    private final static String ipAddress = "localhost";
    Socket soc;
    DataInputStream dis;
    DataOutputStream dos;
    HostGUI host;
    ClientGUi client;
    Thread listenThread;

    String myName = "none"; // myName cho viec xu li trong thread

    public mainGUI() {
        String myID = "none";
        myID = randomID();
        myName = myID;
        try {
            soc = new Socket(ipAddress, port);
            dis = new DataInputStream(soc.getInputStream());
            dos = new DataOutputStream(soc.getOutputStream());

            // Phan xu li voi server
            // -----------------------------------------------------------------------
            dos.writeUTF(myID);
            listenThread = new Thread(() -> {
                try {
                    while (true) {
                        String req = dis.readUTF();
                        System.out.println(req);
                        switch (req) {
                            /*
                             * * 1. refuse_00: Kiem tra ID khong ton tai
                             * 2. refuse_01: Kiem tra mat khau khong dung
                             * 3. refuse_02: Host dang ket noi voi nguoi khac
                             * 4. refuse_03: Host tu choi ket noi
                             * 5. accept: Host dong y ket noi
                             * 6. request: Yeu cau ket noi tu Remote User
                             */
                            case "request":
                                if (!dis.readUTF().equals(passField.getText())) {
                                    dos.writeUTF("refuse_01");
                                    dos.flush();
                                    break;
                                }
                                String guestName = dis.readUTF();
                                int response = JOptionPane.showConfirmDialog(
                                        null,
                                        "[CLient: " + guestName + " ] muốn kết nối tới. Bạn có đồng ý không?",
                                        "Xác nhận",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                                switch (response) {
                                    case JOptionPane.YES_OPTION:
                                        // Chay Host
                                        dos.writeUTF("accept");
                                        dos.flush();
                                        System.out.println("Run host");
                                        host = new HostGUI(dos, guestName);
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
                                client = new ClientGUi(ip, dos, myName);
                                break;
                            case "refuse_00":
                                errLabel.setText("ID not found!");
                                break;
                            case "refuse_01":
                                errLabel.setText("Wrong Password!");
                                break;
                            case "refuse_02":
                                errLabel.setText("Unable to connect to the this client!");
                                break;
                            case "refuse_03":
                                errLabel.setText("Host rejected!");
                                break;
                            case "disconnect":
                                if (host != null) {
                                    errLabel.setText("disconnected!");
                                    host.disconnect();
                                    host = null;
                                }
                                if (client != null) {
                                    errLabel.setText("Host disconnected!");
                                    client.disconnect();
                                    client = null;
                                }
                                break;
                            default:
                                System.out.println("Wrong request");
                                break;
                        }
                    }
                } catch (Exception e) {
                }
            });
            listenThread.start();
            // -----------------------------------------------------------------------------------------------

        } catch (Exception e) {

        }
        System.out.println("Debug1");

        setTitle("Phần mềm điều khiến máy tính từ xa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 10, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel trái - Thông tin Host
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Cho phép điều khiển"));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Tên của bạn:"));
        nameField = new JTextField(15);
        nameField.setText(myID);
        namePanel.add(nameField);
        JButton changeNameButton = new JButton("Đổi tên");
        changeNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeName();
            }
        });
        namePanel.add(changeNameButton);
        leftPanel.add(namePanel);

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("ID của bạn:"));
        idField = new JTextField(15);
        idField.setText(myID);
        idField.setEditable(false);
        idPanel.add(idField);
        leftPanel.add(idPanel);

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("Mật khẩu:"));
        passField = new JTextField(15);
        passField.setText(randomPassword());
        passField.setEditable(false);
        passPanel.add(passField);
        leftPanel.add(passPanel);

        JButton changePassButton = new JButton("Đổi mật khẩu");
        changePassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPass = randomPassword();
                passField.setText(newPass);
                JOptionPane.showMessageDialog(mainGUI.this, "Change password successfully!");
            }
        });
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(changePassButton);

        JButton copyPassButton = new JButton("Sao chép mật khẩu");
        copyPassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(passField.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(mainGUI.this, "Password copied to clipboard!");
            }
        });

        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(copyPassButton);

        // Panel phải - thông tin máy muốn điều khiển
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Điều khiển máy tính khác"));

        JPanel ridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ridPanel.add(new JLabel("ID của khách:"));
        ridField = new JTextField(15);
        ridPanel.add(ridField);
        rightPanel.add(ridPanel);

        JPanel rpassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rpassPanel.add(new JLabel("Mật khẩu:"));
        rpassField = new JTextField(15);
        rpassPanel.add(rpassField);
        rightPanel.add(rpassPanel);

        JButton connectButton = new JButton("Bắt đầu điều khiển");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleConnection();
            }
        });
        rightPanel.add(connectButton);

        errLabel = new JLabel();
        rightPanel.add(errLabel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Cài đặt thuộc tính của cửa sổ
        setSize(760, 240);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Hàm kích hoạt kết nối
    private void toggleConnection() {
        try {
            if (ridField.getText().equals(idField.getText())) {
                errLabel.setText("Không thể kết nối với chính mình!");
                return;
            }
            dos.writeUTF("connect");
            dos.writeUTF(ridField.getText());
            dos.writeUTF(rpassField.getText());
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hàm thay đổi tên
    private void changeName() {
        try {
            myName = nameField.getText();
            dos.writeUTF("changeName");
            dos.writeUTF(nameField.getText());
            JOptionPane.showMessageDialog(this, "Change name successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Change name failed!");
            throw new RuntimeException(e);
        }
    }

    // Hàm tạo mật khẩu ngẫu nhiên
    public String randomPassword() {
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

    // Hàm tạo ID ngẫu nhiên
    public String randomID() {
        Random rand = new Random();
        String characters = "0123456789";
        int length = 10;
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (i == 2 || i == 6) {
                randomString.append(" ");
                continue;
            }
            int index = rand.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    public static void main(String[] args) {
        new mainGUI().setVisible(true);
    }
}
