import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Zombie {
    private double x, y;
    private double speed;
    private String word;
    private MovementStrategy movementStrategy;
    private String typedWord = "";
    private Animation walkAnimation;
    private Animation deathAnimation;
    private boolean isDead = false;
    private int targetX, targetY;
    private long spawnTime;
    private int width;
    private int height;

    public Zombie(int x, int y, String word, double speed, MovementStrategy movementStrategy) {
        this.x = x;
        this.y = y;
        this.word = word;
        this.speed = speed;
        this.movementStrategy = movementStrategy;
        this.walkAnimation = new Animation(100);
        this.deathAnimation = new Animation(100);
        this.deathAnimation.setLoop(false);
        this.spawnTime = System.currentTimeMillis();
        this.width = 32;
        this.height = 32;

        List<Image> walkFrames = loadFramesFromDirectory(getWalkAnimationDirectory());
        for (Image frame : walkFrames) {
            walkAnimation.addFrame(frame);
        }

        List<Image> deathFrames = loadFramesFromDirectory("../res/dead_zombie");
        for (Image frame : deathFrames) {
            deathAnimation.addFrame(frame);
        }
    }

    private String getWalkAnimationDirectory() {
        if (movementStrategy instanceof ZigZagMovement) {
            return "../res/fast_zombie";
        } else {
            return "../res/slow_zombie";
        }
    }

    private List<Image> loadFramesFromDirectory(String directoryPath) {
        List<Image> frames = new ArrayList<>();
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((_, name) -> name.toLowerCase().endsWith(".png"));
        if (files != null) {
            for (File file : files) {
                try {
                    frames.add(ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return frames;
    }

    public void moveTowards(int targetX, int targetY) {
        moveTowards(targetX, targetY, speed);
    }

    public void moveTowards(int targetX, int targetY, double speed) {
        if (!isDead) {
            double deltaX = targetX - x;
            double deltaY = targetY - y;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            x += speed * (deltaX / distance);
            y += speed * (deltaY / distance);
        }
    }

    public void move() {
        movementStrategy.move(this);
    }

    public void update() {
        move();
    }

    public boolean collidesWith(Player player) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = 50;
        int playerHeight = 50;

        return getX() >= playerX && getX() <= playerX + playerWidth && getY() >= playerY && getY() <= playerY + playerHeight;
    }

    public void draw(Graphics g) {
        Image currentFrame;
        if (isDead) {
            deathAnimation.update();
            currentFrame = deathAnimation.getCurrentFrame();
        } else {
            walkAnimation.update();
            currentFrame = walkAnimation.getCurrentFrame();
        }
        if (currentFrame != null) {
            g.drawImage(currentFrame, (int) x, (int) y, null);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(word, (int) x + 10, (int) y + 30);
        g.setColor(Color.RED);
        g.drawString(typedWord, (int) x + 10, (int) y + 45);

    }

    public int getY() {
        return (int) y;
    }

    public String getWord() {
        return word;
    }

    public String getTypedWord() {
        return typedWord;
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

    public void die() {
        isDead = true;
        deathAnimation.reset();
    }

    public int getX() {
        return (int) x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTarget(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public Animation getDeathAnimation() {
        return deathAnimation;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}