package Host.Handler;
/*
 * Đây là class FileTransferController để gửi và nhận file
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.net.Socket;

public class FileTransfer implements Runnable {
    private static FileTransfer _instance;
    private Socket fileSoc;
    private String folderPath;
    private DataOutputStream dos;
    private DataInputStream dis;
    BufferedInputStream bis;
    BufferedOutputStream bos;
    boolean isRunning = true;

    private FileTransfer(Socket fileSoc, String folderPath) {
        this.fileSoc = fileSoc;
        this.folderPath = folderPath;
        try {
            this.dis = new DataInputStream(fileSoc.getInputStream());
            this.dos = new DataOutputStream(fileSoc.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error in creating file transfer controller");
        }
    }

    public static FileTransfer getInstance(Socket fileSoc, String folderPath) {
        if (_instance == null) {
            _instance = new FileTransfer(fileSoc, folderPath);
        }
        return _instance;
    }

    public void sendFile(String filePath, String fileName) {
        try {
            bis = new BufferedInputStream(new FileInputStream(filePath));

            dos.writeUTF("file");
            dos.writeUTF(fileName);
            dos.flush();

            int c;
            do {
                c = bis.read();
                dos.writeInt(c);
                dos.flush();
            } while (c != -1);
            bis.close();
        } catch (Exception e) {
            System.out.println("Error in sending file");
        }
    }

    public void receiveFile() {
        try {
            bis = new BufferedInputStream(dis);
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String name = dis.readUTF();
            String filePath = folderPath + "\\" + name;
            bos = new BufferedOutputStream(new FileOutputStream(filePath));

            int c;
            do{
                c = dis.readInt();
                bos.write(c);
            } while (c != -1);
            bos.close();
        } catch (Exception e) {
            System.out.println("Error in recieving file");
        }
    }

    @Override
    public void run() {
        while (fileSoc.isConnected() && isRunning) {
            try {
                if (dis.readUTF().equals("file"))
                    receiveFile();
            } catch (Exception e) {
                System.out.println("Error in recieving host name");
            }
        }
    }

    public void disconnect() {
        try {
            dos.close();
            dis.close();
            fileSoc.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            System.out.println("loi");
        }
    }
}
