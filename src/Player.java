import java.awt.Graphics;
import java.awt.Color;

public class Player {
    private int x, y;
    private String skin;

    public Player(int x, int y, String skin) {
        this.x = x;
        this.y = y;
        this.skin = skin;
    }

    public void draw(Graphics g) {
        if (skin.equals("Skin 1")) {
            g.setColor(Color.BLUE);
        } else if (skin.equals("Skin 2")) {
            g.setColor(Color.CYAN);
        } else if (skin.equals("Skin 3")) {
            g.setColor(Color.MAGENTA);
        }
        g.fillRect(x, y, 50, 50);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}