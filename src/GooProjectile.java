import java.awt.Color;
import java.awt.Graphics;

public class GooProjectile {
    private double x, y;
    private char letter;
    private double speed;
    private double targetX, targetY;
    private long spawnTime;

    public GooProjectile(int x, int y, char letter, double speed) {
        this.x = x;
        this.y = y;
        this.letter = letter;
        this.speed = speed;
        this.spawnTime = System.currentTimeMillis();
    }

    public void setTarget(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void update() {
        double deltaX = targetX - x;
        double deltaY = targetY - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        x += speed * (deltaX / distance);
        y += speed * (deltaY / distance);
    }

    public boolean isOutOfBounds() {
        return x < 0 || x > 800 || y < 0 || y > 600; // Assuming screen size is 800x600
    }

    public boolean collidesWith(Player player) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = 50; // Assuming player width
        int playerHeight = 50; // Assuming player height

        return x >= playerX && x <= playerX + playerWidth && y >= playerY && y <= playerY + playerHeight;
    }

    public boolean collidesWith(Bullet bullet) {
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        int bulletWidth = 5; // Assuming bullet width
        int bulletHeight = 5; // Assuming bullet height

        return x >= bulletX && x <= bulletX + bulletWidth && y >= bulletY && y <= bulletY + bulletHeight;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval((int) x, (int) y, 20, 20); // Increased size for better visibility
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(letter), (int) x + 7, (int) y + 15); // Adjusted position for better visibility
    }

    public char getLetter() {
        return letter;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
}