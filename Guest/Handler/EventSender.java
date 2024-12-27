package Guest.Handler;
/*
 * Đây là lớp gửi sự kiện chuột và bàn phím
 */

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;

import java.io.DataOutputStream;
import java.net.Socket;

import java.util.concurrent.LinkedBlockingQueue;

import Guest.Handler.EventSender;
import Guest.Object.cEvent;

public class EventSender implements MouseListener, MouseMotionListener, KeyListener {
    private static EventSender _instance;
    private Socket imgSoc;
    private int Width, Height, pWidth, pHeight;
    private static LinkedBlockingQueue<cEvent> eventQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<cEvent> previouse_eventQueue = new LinkedBlockingQueue<>();
    private DataOutputStream imgDos;
    boolean isRunning = true;

    private EventSender(Socket imgSoc, int pWidth, int pHeight, int Width, int Height) {
        this.imgSoc = imgSoc;
        try {
            this.imgDos = new DataOutputStream(imgSoc.getOutputStream());
        } catch (Exception e) {
            System.out.println("error in Event");
        }
        this.pWidth = pWidth;
        this.pHeight = pHeight;
        this.Width = Width;
        this.Height = Height;
    }

    public static EventSender getInstance(int pWidth, int pHeight, int width, int height, Socket imgSoc) {
        if (_instance == null) {
            _instance = new EventSender(imgSoc, pWidth, pHeight, width, height);
        }
        new Thread(() -> {
            try {
                while (_instance.imgSoc.isConnected() && _instance.isRunning) {
                    if (eventQueue.isEmpty()) {
                        continue;
                    }
                    cEvent current_event = new cEvent();
                    current_event = eventQueue.take();
                    if (current_event.getEventType().equals("KEY_PRESS")) {
                        if (current_event.getButtonCode() == KeyEvent.VK_CONTROL && previouse_eventQueue.isEmpty()) {
                            previouse_eventQueue.put(current_event);
                            continue;
                        }
                        if (!previouse_eventQueue.isEmpty() && current_event.getButtonCode() == KeyEvent.VK_V) {
                            String pasteText = _instance.getCLipboard();
                            if (pasteText.isEmpty()) {
                                continue;
                            }
                            current_event.setEvent("PASTE", pasteText);
                            _instance.sendClipboard(current_event);
                            previouse_eventQueue.clear();
                            continue;
                        }
                    }
                    _instance.sendEvent(current_event);
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        }).start();
        return _instance;
    }

    private void sendEvent(cEvent event) {
        try {
            imgDos.writeUTF(event.getEventType());
            imgDos.writeInt(event.getButtonCode());
            imgDos.writeInt(event.getPosx());
            imgDos.writeInt(event.getPosy());
            imgDos.flush();
        } catch (Exception e) {
            System.out.println("Error in sending event");
        }
    }

    private String getCLipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
            return pasteText;
        } catch (Exception e) {
            System.out.println("Error in getting clipboard");
            return "";
        }
    }

    private void sendClipboard(cEvent event) {
        try {
            imgDos.writeUTF(event.getEventType());
            imgDos.writeUTF(event.getPasteText());
            imgDos.flush();

        } catch (Exception e) {
            System.out.println("Error in sending clipboard");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        try {
            int button = InputEvent.getMaskForButton(e.getButton());
            cEvent current_event = new cEvent();
            current_event.setEvent("MOUSE_PRESS", button, 0, 0);
            eventQueue.put(current_event);
            System.out.println("Mouse Pressed");
        } catch (Exception e1) {
            System.out.println("Error in handling mouse pressed event");
        }

    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        try {
            int button = InputEvent.getMaskForButton(e.getButton());
            cEvent current_event = new cEvent();
            current_event.setEvent("MOUSE_RELEASE", button, 0, 0);
            eventQueue.put(current_event);
            System.out.println("Mouse Released");
        } catch (Exception e1) {
            System.out.println("Error in handling mouse released event");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        try {
            int x = e.getX();
            int y = e.getY();

            int xScale = x * Width / pWidth;
            int yScale = y * Height / pHeight;

            cEvent current_event = new cEvent();
            current_event.setEvent("MOUSE_MOVE", 0, xScale, yScale);
            eventQueue.put(current_event);
        } catch (Exception e1) {
            System.out.println("Error in handling mouse dragged event");
        }
    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
        try {
            int x = e.getX();
            int y = e.getY();

            int xScale = x * Width / pWidth;
            int yScale = y * Height / pHeight;

            cEvent current_event = new cEvent();
            current_event.setEvent("MOUSE_MOVE", 0, xScale, yScale);
            eventQueue.put(current_event);
        } catch (Exception e1) {
            System.out.println("Error in handling mouse moved event");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        try {
            int keyCode = e.getKeyCode();
            cEvent current_event = new cEvent();
            current_event.setEvent("KEY_PRESS", keyCode, 0, 0);
            eventQueue.put(current_event);
            System.out.println("Key Pressed");
        } catch (Exception e1) {
            System.out.println("Error in handling key pressed event");
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        try {
            int keyCode = e.getKeyCode();
            cEvent current_event = new cEvent();
            current_event.setEvent("KEY_RELEASE", keyCode, 0, 0);
            eventQueue.put(current_event);
            System.out.println("Key Released");
        } catch (Exception e1) {
            System.out.println("Error in handling key released event");
        }
    }

    public void disconnect() {
        try {
            imgDos.close();
            isRunning = false;
            _instance = null;
        } catch (Exception e) {
            System.out.println("Error in disconnecting event");
        }
    }
}
