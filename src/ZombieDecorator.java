import java.awt.Graphics;

public abstract class ZombieDecorator extends Zombie {
    protected Zombie zombie;

    public ZombieDecorator(Zombie zombie) {
        super(zombie.getX(), zombie.getY(), zombie.getWord(), zombie.getSpeed(), zombie.getSkin());
        this.zombie = zombie;
    }

    @Override
    public void draw(Graphics g) {
        zombie.draw(g);
    }

    @Override
    public void move() {
        zombie.move();
    }

    @Override
    public int getX() {
        return zombie.getX();
    }

    @Override
    public int getY() {
        return zombie.getY();
    }

    @Override
    public String getWord() {
        return zombie.getWord();
    }

    @Override
    public int getSpeed() {
        return zombie.getSpeed();
    }

    @Override
    public String getSkin() {
        return zombie.getSkin();
    }

    @Override
    public void setX(int x) {
        zombie.setX(x);
    }

    @Override
    public void setY(int y) {
        zombie.setY(y);
    }
}