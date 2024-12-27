package Host.Object;
/*
 * Đây là class Screen, dùng để lấy thông tin màn hình Host
 */

import java.awt.geom.AffineTransform;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.GraphicsConfiguration;

public class Screen {
    private double width;
    private double height;
    private double scale;

    public Screen() {
        this.width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        AffineTransform at = gc.getDefaultTransform();
        this.scale = at.getScaleX();
    }

    public double getWidth() {
        return width * scale;
    }

    public double getHeight() {
        return height * scale;
    }

    public double getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "Resolution: " + width + "x" + height;
    }
}