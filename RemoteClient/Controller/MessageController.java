package RemoteClient.Controller;
/*
 * Đây là lớp nhận và gửi tin nhắn
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MessageController implements Runnable {
    private static MessageController _instance;
    private Socket msgSoc;
    private JTable chatArea;
    private DefaultTableModel chatModel;
    private DataInputStream msgDis;
    private DataOutputStream msgDos;
    private String hostName;
    boolean isRunning = true;

    private MessageController(Socket msgSoc, JTable chatArea, DefaultTableModel chatModel, String hostName) {
        this.msgSoc = msgSoc;
        try {
            this.msgDis = new DataInputStream(msgSoc.getInputStream());
            this.msgDos = new DataOutputStream(msgSoc.getOutputStream());
            this.hostName = hostName;
        } catch (Exception e) {

        }

        this.chatArea = chatArea;
        this.chatModel = chatModel;
    }

    public static MessageController getInstance(Socket msgSoc, JTable chatArea, DefaultTableModel chatModel, String hostName) {
        if (_instance == null) {
            _instance = new MessageController(msgSoc, chatArea, chatModel, hostName);
        }
        return _instance;
    }

    public void sendMessage(String message) {
        try {
            msgDos.writeUTF(message);
            msgDos.flush();
            message = "you: "+message;

            // Thêm tin nhắn vào chat
            chatModel.addRow(new Object[] { "", message });
            chatArea.scrollRectToVisible(chatArea.getCellRect(chatArea.getRowCount() - 1, 0, true));
        } catch (Exception e) {
            System.out.println("Error sending message");
        }
    }

    private void receiveMessage() {
        try {
            String message = msgDis.readUTF();
            message = hostName + ": " + message;

            chatModel.addRow(new Object[] { message, "" });
            chatArea.scrollRectToVisible(chatArea.getCellRect(chatArea.getRowCount() - 1, 0, true));
        } catch (Exception e) {
            System.out.println("Error receiving message");
        }
    }

    @Override
    public synchronized void run() {
        while (msgSoc.isConnected() && isRunning) {
            try {
                receiveMessage();
            } catch (Exception e) {
                System.out.println("Error receiving type");
            }
        }
    }

    public void disconnect() {
        try {
            msgDis.close();
            msgDos.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            System.out.println("Error in disconnecting message");
        }
    }
}