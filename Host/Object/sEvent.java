package Host.Object;
/*
 * Đây là class sEvent, dùng để lưu trữ các sự kiện từ Client
 */

public class sEvent {
    private String eventType;
    private int buttonCode;
    private int posx, posy;
    private String pasteText;

    public sEvent() {
    }

    public void setEvent(String eventType, int buttonCode, int posx, int posy) {
        this.eventType = eventType;
        this.buttonCode = buttonCode;
        this.posx = posx;
        this.posy = posy;
    }

    public void setEvent(String eventType, String pasteText) {
        this.eventType = eventType;
        this.pasteText = pasteText;
    }

    public String getEventType() {
        return eventType;
    }

    public int getButtonCode() {
        return buttonCode;
    }

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public String getPasteText() {
        return pasteText;
    }
}