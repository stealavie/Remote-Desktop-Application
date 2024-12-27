package Host.GUI;
/*
 * Đây là cửa sổ chat giữa Host và Client
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Host.Handler.FileTransfer;
import Host.Handler.Message;

public class chatInterface extends JFrame {
    private Message msgInstance;
    private FileTransfer sFileTransferInstance;

    private Thread msgThread;
    private Thread fileThread;

    public chatInterface(Socket msgSoc, Socket fileSoc, String folderPath, String clientName) {
        setSize(300, 400);
        setLayout(new BorderLayout());

        // căn giữa cửa sổ chat
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - 325;
        int y = screenSize.height / 2 - 200;
        setLocation(x, y);

        // Tạo chỗ hiển thị chat
        String[] cols = { "", "" };
        DefaultTableModel chatModel = new DefaultTableModel(cols, 0);
        JTable chatArea = new JTable(chatModel);
        chatArea.setEnabled(false);

        // Khởi tạo các Instances của MessageController và FileTransferController
        msgInstance = Message.getInstance(msgSoc, chatArea, chatModel, clientName);
        sFileTransferInstance = FileTransfer.getInstance(fileSoc, folderPath);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                msgInstance.sendMessage(message);
                inputField.setText("");
            }
        });

        JButton sendButton = new JButton("Gửi");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    msgInstance.sendMessage(message);
                    inputField.setText("");
                }
            }
        });

        JButton browseButton = new JButton("File");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(chatInterface.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getName();
                    String filePath = selectedFile.getAbsolutePath();

                    msgInstance.sendMessage("Sent file!");
                    sFileTransferInstance.sendFile(filePath, fileName);
                }
            }
        });

        // Panel chứa các button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(browseButton);

        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Khởi chạy luồng nhận tin nhắn và file
        msgThread = new Thread(msgInstance);
        fileThread = new Thread(sFileTransferInstance);

        msgThread.start();
        fileThread.start();
    }

    public void disconnect() {
        try {
            msgInstance.disconnect();
            sFileTransferInstance.disconnect();
        } catch (Exception e) {
            System.out.println("loi");
        }
    }
}