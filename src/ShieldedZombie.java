import java.awt.Color;
import java.awt.Graphics;

public class ShieldedZombie extends ZombieDecorator {
    public ShieldedZombie(Zombie zombie) {
        super(zombie);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.RED);
        g.drawRect(zombie.getX(), zombie.getY(), 50, 50); // Draw shield
    }
}