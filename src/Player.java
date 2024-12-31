import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int x, y;
    //private String skin;
    private Animation playerAnimation;
    private Animation gunFireAnimation;
    private Image gunImage;
    private double gunAngle;
    private int gunRecoil;

    public Player(int x, int y, String skin) {
        this.x = x;
        this.y = y;
        //this.skin = skin;
        this.gunAngle = 0;
        this.gunRecoil = 0;

        // Load player animation frames
        playerAnimation = new Animation(100); // Example frame duration
        List<Image> playerFrames = loadFramesFromDirectory("../res/player/" + skin + "/");
        for (Image frame : playerFrames) {
            playerAnimation.addFrame(frame);
        }

        // Load gun fire animation frames
        gunFireAnimation = new Animation(50); // Example frame duration
        List<Image> gunFireFrames = loadFramesFromDirectory("../res/misc/");
        for (Image frame : gunFireFrames) {
            gunFireAnimation.addFrame(frame);
        }
        gunFireAnimation.setLoop(false); // Gun fire animation should not loop

        // Load gun image
        try {
            gunImage = ImageIO.read(new File("../res/guns/g1.png")); // Adjust the path as needed
        } catch (IOException e) {
            e.printStackTrace();
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

    public void draw(Graphics g) {
        playerAnimation.update();
        Image currentFrame = playerAnimation.getCurrentFrame();
        if (currentFrame != null) {
            g.drawImage(currentFrame, x, y, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, 50, 50);
        }

        if (gunImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(Math.toRadians(gunAngle), x + 25, y + 25);
            g2d.drawImage(gunImage, x + 25 - gunImage.getWidth(null) / 2 + gunRecoil, y + 25 - gunImage.getHeight(null) / 2, null);
            if (gunRecoil < 0) {
                gunFireAnimation.update();
                Image gunFireFrame = gunFireAnimation.getCurrentFrame();
                if (gunFireFrame != null) {
                    // Calculate the tip of the gun
                    int gunTipX = (int) (x + 25 + Math.cos(Math.toRadians(gunAngle)) * 32);
                    int gunTipY = (int) (y + 25 + Math.sin(Math.toRadians(gunAngle)) * 32);
                    // Adjust the position of the gun fire animation here
                    g2d.drawImage(gunFireFrame, gunTipX - gunFireFrame.getWidth(null) / 2, gunTipY - gunFireFrame.getHeight(null) / 2, null);
                }
            }
            g2d.dispose();
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x + 25 + gunRecoil, y + 20, 50, 10);
        }
    }

    public void shoot() {
        gunRecoil = -10; // Recoil effect
        gunFireAnimation.reset(); // Reset gun fire animation
    }

    public void update() {
        if (gunRecoil < 0) {
            gunRecoil++;
        }
    }

    public void setGunAngle(double angle) {
        this.gunAngle = angle;
    }

    public double getGunAngle() {
        return gunAngle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void aimAt(int targetX, int targetY) {
        double deltaX = targetX - (x + 25);
        double deltaY = targetY - (y + 25);
        gunAngle = Math.toDegrees(Math.atan2(deltaY, deltaX));
    }
}