import java.awt.Color;
import java.awt.Graphics;

public class Zombie {
    private int x, y, speed;
    private String word;
    private String skin;
    private MovementStrategy movementStrategy;
    private String typedWord = "";

    public Zombie(int x, int y, String word, int speed, String skin) {
        this.x = x;
        this.y = y;
        this.word = word;
        this.speed = speed;
        this.skin = skin;
    }

    public void moveTowards(int targetX, int targetY) {
        int deltaX = targetX - x;
        int deltaY = targetY - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        x += (int) (speed * (deltaX / distance));
        y += (int) (speed * (deltaY / distance));
    }

    public void draw(Graphics g) {
        if (skin.equals("Skin1")) {
            g.setColor(Color.GREEN);
        } else if (skin.equals("Skin2")) {
            g.setColor(Color.YELLOW);
        } else if (skin.equals("Skin3")) {
            g.setColor(Color.ORANGE);
        }
        g.fillRect(x, y, 50, 50);
        g.setColor(Color.WHITE);
        g.drawString(word, x + 10, y + 30);
        g.setColor(Color.RED);
        g.drawString(typedWord, x + 10, y + 45);
    }

    public int getY() {
        return y;
    }

    public String getWord() {
        return word;
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public boolean isKilled(String typedWord) {
        return word.equals(typedWord);
    }

    public void addTypedLetter(char letter) {
        if (typedWord.length() < word.length() && word.charAt(typedWord.length()) == letter) {
            typedWord += letter;
        }
    }

    public boolean isFullyTyped() {
        return word.equals(typedWord);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public String getSkin() {
        return skin;
    }
}