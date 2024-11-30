package RemoteClient.View;
/*
 * Lớp này tạo ra cửa sổ chat giữa Remote Client và Host
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import RemoteClient.Controller.FileTransferController;
import RemoteClient.Controller.MessageController;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ChatWindow extends JFrame {
    public ChatWindow(Socket msgSoc, Socket fileSoc, String folderPath, String hostName) {
        setupFrame();
        initializeComponents(msgSoc, fileSoc, folderPath, hostName);
    }

    private void setupFrame() {
        // Ẩn đi thanh tiêu đề và không cho phép resize
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout(5, 5));
    }

    private void initializeComponents(Socket msgSoc, Socket fileSoc, String folderPath, String hostName) {

        // Cài đặt khu vực chat
        String[] cols = { "", "" };
        DefaultTableModel chatModel = new DefaultTableModel(cols, 0);
        JTable chatArea = new JTable(chatModel);
        chatArea.setEnabled(false);

        // Các instance của các controllers
        MessageController messageController = MessageController.getInstance(msgSoc, chatArea, chatModel, hostName);
        FileTransferController fileController = FileTransferController.getInstance(fileSoc, folderPath);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    messageController.sendMessage(message);
                    inputField.setText("");
                }
            }
        });

        JButton sendButton = new JButton("Gửi");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    messageController.sendMessage(message);
                    inputField.setText("");
                }
            }
        });

        JButton browseButton = new JButton("File");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(ChatWindow.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getName();
                    String filePath = selectedFile.getAbsolutePath();

                    messageController.sendMessage("Sent file: " + filePath);
                    fileController.sendFile(filePath, fileName);
                }
            }
        });

        // Panel chứa các button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(browseButton);

        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // khởi tạo luồng
        new Thread(messageController).start();
        new Thread(fileController).start();
    }
}
