import java.awt.Color;
import java.awt.Graphics;

public class Bullet {
    private int x, y;
    private double angle;
    private int speed = 10;
    private Zombie targetZombie;

    public Bullet(int x, int y, double angle, Zombie targetZombie) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.targetZombie = targetZombie;
    }

    public void update() {
        if (targetZombie != null) {
            adjustAngleTowardsTarget();
        }
        x += (int) (speed * Math.cos(Math.toRadians(angle)));
        y += (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    private void adjustAngleTowardsTarget() {
        double deltaX = targetZombie.getX() - x;
        double deltaY = targetZombie.getY() - y;
        angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    public boolean isOutOfBounds() {
        return x < 0 || x > 800 || y < 0 || y > 600;
    }

    public boolean collidesWith(Zombie zombie) {
        int zombieX = zombie.getX();
        int zombieY = zombie.getY();
        int zombieWidth = 50;
        int zombieHeight = 50;

        return x >= zombieX && x <= zombieX + zombieWidth && y >= zombieY && y <= zombieY + zombieHeight;
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