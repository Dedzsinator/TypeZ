import java.awt.Color;
import java.awt.Graphics;

public class Bullet {
    private int x, y;
    private double angle;
    private int speed = 10;

    public Bullet(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update() {
        x += (int) (speed * Math.cos(Math.toRadians(angle)));
        y += (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    public boolean isOutOfBounds() {
        return x < 0 || x > 800 || y < 0 || y > 600; // Assuming screen size is 800x600
    }

    public boolean collidesWith(Zombie zombie) {
        int zombieX = zombie.getX();
        int zombieY = zombie.getY();
        int zombieWidth = 50; // Assuming zombie width
        int zombieHeight = 50; // Assuming zombie height

        return x >= zombieX && x <= zombieX + zombieWidth && y >= zombieY && y <= zombieY + zombieHeight;
    }

    public boolean collidesWith(GooProjectile goo) {
        int gooX = goo.getX();
        int gooY = goo.getY();
        int gooWidth = 20; // Assuming goo width
        int gooHeight = 20; // Assuming goo height

        return x >= gooX && x <= gooX + gooWidth && y >= gooY && y <= gooY + gooHeight;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 5, 5);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}