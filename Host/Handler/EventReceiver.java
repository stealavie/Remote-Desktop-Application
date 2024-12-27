package Host.Handler;
/*
 * Đây là class EventReceiverController để nhận và thực thi các sự kiện từ Client
 */

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import Host.Object.Screen;
import Host.Object.sEvent;

public class EventReceiver implements Runnable {
    private Socket imgSoc;
    private sEvent event = new sEvent();
    Robot robot;
    DataInputStream imgDis;
    Screen screen = new Screen();
    private static LinkedBlockingQueue<sEvent> eventQueue = new LinkedBlockingQueue<>();

    private static EventReceiver _instance;
    boolean isRunning = true;

    private EventReceiver(Socket imgSoc) {
        this.imgSoc = imgSoc;
    }

    public static EventReceiver getInstance(Socket imgSoc) {
        if (_instance == null) {
            _instance = new EventReceiver(imgSoc);
        }
        return _instance;
    }

    private void execute(sEvent event) {
        String eventType = event.getEventType();
        System.out.println(eventType);
        switch (eventType) {
            case "MOUSE_MOVE":
                double posx = event.getPosx() / screen.getScale();
                double posy = event.getPosy() / screen.getScale();
                robot.mouseMove((int) posx, (int) posy);
                break;
            case "MOUSE_PRESS":
                robot.mousePress(event.getButtonCode());
                break;
            case "MOUSE_RELEASE":
                robot.mouseRelease(event.getButtonCode());
                break;
            case "KEY_PRESS":
                robot.keyPress(event.getButtonCode());
                break;
            case "KEY_RELEASE":
                robot.keyRelease(event.getButtonCode());
                break;
            case "PASTE":
                String pasteText = event.getPasteText();
                StringSelection stringSelection = new StringSelection(pasteText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    System.out.println("Error in sleeping");
                }

                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                break;
            default:
                System.out.println("Khong doc duoc loai su kien!");
                break;
        }
    }

    @Override
    public synchronized void run() {
        try {
            robot = new Robot();
            imgDis = new DataInputStream(imgSoc.getInputStream());
        } catch (Exception e) {
            System.out.println("Error in creating robot/imgDis");
        }
        while (imgSoc.isConnected() && isRunning) {
            try {
                String eventType = imgDis.readUTF();
                switch (eventType) {
                    case "PASTE":
                        event.setEvent(eventType, imgDis.readUTF());
                        eventQueue.put(event);
                        break;
                    default:
                        event.setEvent(eventType, imgDis.readInt(), imgDis.readInt(), imgDis.readInt());
                        eventQueue.put(event);
                        break;
                }

                execute(eventQueue.take());

            } catch (Exception e) {
                System.out.println("Error in reading event");
            }
        }
    }

    public void disconnect() {
        try {
            imgDis.close();
            imgSoc.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            System.out.println("Error in disconnecting");
        }
    }
}
