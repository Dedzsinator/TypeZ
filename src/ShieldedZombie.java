import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ShieldedZombie extends Zombie {
    private String armorWord;
    private String typedArmorWord = "";
    private boolean armorDestroyed = false;
    private float armorTransparency = 1.0f;
    private Image helmetImage;

    public ShieldedZombie(int x, int y, String word, String armorWord, double speed, MovementStrategy movementStrategy) {
        super(x, y, word, speed, movementStrategy);
        this.armorWord = armorWord;

        try {
            helmetImage = ImageIO.read(new File("../res/misc/helm.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTypedArmorLetter(char letter) {
        if (typedArmorWord.length() < armorWord.length() && armorWord.charAt(typedArmorWord.length()) == letter) {
            typedArmorWord += letter;
        }
        if (typedArmorWord.equals(armorWord)) {
            armorDestroyed = true;
        }
    }

    public String getTypedArmorWord() {
        return typedArmorWord;
    }

    @Override
    public void addTypedLetter(char letter) {
        if (!armorDestroyed) {
            addTypedArmorLetter(letter);
        } else {
            super.addTypedLetter(letter);
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (!armorDestroyed && helmetImage != null) {
            g.drawImage(helmetImage, (int) getX() + 15, (int) getY() + 5, null);
        }
        if (!armorDestroyed) {
            g.setColor(new Color(0, 0, 255, (int) (armorTransparency * 255)));
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(armorWord, (int) getX() + 10, (int) getY() + 30);
            g.setColor(new Color(255, 0, 0, (int) (armorTransparency * 255)));
            g.drawString(typedArmorWord, (int) getX() + 10, (int) getY() + 45);
        } else {
            armorTransparency -= 0.01f;
            armorTransparency = Math.max(armorTransparency, 0);
        }
    }

    public boolean isArmorDestroyed() {
        return armorDestroyed;
    }

    public String getArmorWord() {
        return armorWord;
    }

    public void destroyArmor() {
        armorDestroyed = true;
    }
}