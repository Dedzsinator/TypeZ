import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Iterator;

public class Game extends JPanel implements Runnable {
    private Player player;
    private List<Zombie> zombies;
    private List<GooProjectile> gooProjectiles = new ArrayList<>();
    private List<Bullet> bullets;
    private GameEventNotifier notifier;
    private WordLoader wordLoader;
    private int wave;
    private int maxDifficulty;
    private String selectedMusic;
    private String selectedPlayerSkin;
    private String selectedEnemySkin;
    private MusicPlayer musicPlayer;
    private JFrame frame;
    private Image backgroundImage;
    private int score;
    private double combo;
    private long lastKillTime;
    private String waveMessage;
    private float waveMessageAlpha;
    private Zombie focusedZombie;
    private boolean waveInProgress;
    private boolean gameOver;
    private List<Image> gunFireSprites;
    private int gunFireFrame;
    private String difficulty;

    public Game(String selectedMusic, String selectedPlayerSkin, String selectedEnemySkin, String difficulty) {
        this.selectedMusic = selectedMusic;
        this.selectedPlayerSkin = selectedPlayerSkin;
        this.selectedEnemySkin = selectedEnemySkin;
        this.difficulty = difficulty;

        initializeGame();
    }

    private void initializeGame() {
        System.out.println(this.difficulty); // Debug statement
        player = new Player(375, 520, selectedPlayerSkin); // Initial position will be updated
        zombies = new ArrayList<>();
        bullets = new ArrayList<>();
        notifier = new GameEventNotifier();
        notifier.addObserver(new ZombieObserver());
        wave = 1;
        maxDifficulty = 10;
        score = 0;
        combo = 1.0;
        lastKillTime = System.currentTimeMillis();
        waveMessage = "";
        waveMessageAlpha = 0.0f;
        focusedZombie = null;
        waveInProgress = false;
        gameOver = false;
        gunFireFrame = 0;

        try {
            wordLoader = new WordLoader("../res/words.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        musicPlayer = new MusicPlayer();
        playSelectedMusic();

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("../res/bg.png")); // Adjust the path as needed
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load gun fire sprites
        gunFireSprites = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            try {
                gunFireSprites.add(ImageIO.read(new File("../res/misc/gf" + i + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        setFocusable(true);
        requestFocusInWindow(); // Ensure the game panel has focus for key events
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!gameOver) {
                    char typedChar = e.getKeyChar();
                    checkZombieKilled(typedChar);
                    shootBullet();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePlayerPosition();
            }
        });

        startNewWave();
    }

    private void updatePlayerPosition() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        player.setX(panelWidth / 2 - 25); // Center horizontally
        player.setY(panelHeight - 80); // Position at the bottom
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private void playSelectedMusic() {
        String musicFilePath = "../res/" + selectedMusic + ".wav"; // Adjust the path as needed
        musicPlayer.play(musicFilePath);
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    private void startNewWave() {
        System.out.println("Starting new wave: " + wave); // Debug statement
        int numberOfZombies = (int) (5 * Math.log(wave + 1)); // Logarithmic increase in the number of zombies
        Random random = new Random();
        double[] probabilities = getZombieProbabilities();

        int simpleZombieCount = 0;
        int zigzagZombieCount = 0;
        int gooThrowingZombieCount = 0;

        for (int i = 0; i < numberOfZombies; i++) {
            int x, y;
            boolean overlap;
            do {
                x = random.nextInt(750); // Random x position (assuming screen width is 800)
                y = 0; // Start at the top
                overlap = false;
                for (Zombie zombie : zombies) {
                    if (Math.abs(zombie.getX() - x) < 50 && Math.abs(zombie.getY() - y) < 50) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);
            WordLoader.Word word = wordLoader.getRandomWord(maxDifficulty);
            MovementStrategy strategy;
            double rand = random.nextDouble();
            if (rand < probabilities[0]) {
                strategy = new StraightLineMovement();
                simpleZombieCount++;
            } else if (rand < probabilities[0] + probabilities[1]) {
                strategy = new ZigZagMovement();
                zigzagZombieCount++;
            } else {
                strategy = new GooThrowingMovement();
                gooThrowingZombieCount++;
            }
            Zombie zombie = new Zombie(x, y, word.getWord(), 1.5, selectedEnemySkin, strategy);
            zombie.setTarget(player.getX(), player.getY());
            zombies.add(zombie);
        }
        waveInProgress = true;

        // Notify observer about the current wave's zombie types
        notifier.notifyObservers("Wave " + wave + ": Simple Zombies: " + simpleZombieCount + ", ZigZag Zombies: " + zigzagZombieCount + ", GooThrowing Zombies: " + gooThrowingZombieCount);
    }

    private double[] getZombieProbabilities() {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return new double[]{0.7, 0.2, 0.1}; // Simple, ZigZag, GooThrowing
            case "medium":
                return new double[]{0.4, 0.3, 0.3};
            case "hard":
                return new double[]{0.2, 0.4, 0.4};
            default:
                return new double[]{0.4, 0.3, 0.3}; // Default to medium if difficulty is not recognized
        }
    }

    private void checkZombieKilled(char typedChar) {
        if (focusedZombie == null) {
            for (Zombie zombie : zombies) {
                if (zombie.getWord().startsWith(Character.toString(typedChar))) {
                    if (focusedZombie == null || zombie.getWord().length() < focusedZombie.getWord().length()) {
                        focusedZombie = zombie;
                    }
                }
            }
        }

        if (focusedZombie != null) {
            focusedZombie.addTypedLetter(typedChar);
            if (focusedZombie.isFullyTyped()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastKillTime <= 3000) {
                    combo += 0.1;
                } else {
                    combo = 1.0;
                }
                lastKillTime = currentTime;

                int longestCorrectSubsequence = focusedZombie.getTypedWord().length(); // Assuming the whole word is the longest correct subsequence
                score += (10 * longestCorrectSubsequence * combo);

                focusedZombie.die();
                notifier.notifyObservers("ZOMBIE_KILLED");
                focusedZombie = null;
            }
        }

        if (zombies.isEmpty() && waveInProgress) {
            waveInProgress = false;
            wave++;
            maxDifficulty += 10; // Increase difficulty for next wave
            showWaveMessage("Wave " + (wave - 1) + " beaten! Get ready for wave " + wave + "!");
        }
    }

    private void shootBullet() {
        if (focusedZombie != null) {
            player.aimAt(focusedZombie.getX(), focusedZombie.getY());
        }
        player.shoot();
        bullets.add(new Bullet(player.getX() + 25, player.getY() + 25, player.getGunAngle()));
        gunFireFrame = 0; // Reset gun fire animation frame
    }

    private void showWaveMessage(String message) {
        waveMessage = message;
        waveMessageAlpha = 1.0f;
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Show message for 1 second
                while (waveMessageAlpha > 0) {
                    waveMessageAlpha -= 0.01f;
                    waveMessageAlpha = Math.max(waveMessageAlpha, 0); // Ensure alpha does not go below 0
                    repaint();
                    Thread.sleep(50); // Ease out the message
                }
                Thread.sleep(2000); // Pause for 2 seconds after the message fades out
                startNewWave(); // Start the new wave after the pause
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        while (!gameOver) {
            update();
            repaint();
            try {
                Thread.sleep(16); // Roughly 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        player.update();
        updateBullets();
        updateZombies();
        updateGooProjectiles();

        // Check if all zombies are killed and wave is in progress
        if (zombies.isEmpty() && waveInProgress) {
            waveInProgress = false;
            wave++;
            maxDifficulty += 10; // Increase difficulty for next wave
            showWaveMessage("Wave " + (wave - 1) + " beaten! Get ready for wave " + wave + "!");
        }

        // Update gun fire animation frame
        if (gunFireFrame < gunFireSprites.size() - 1) {
            gunFireFrame++;
        }
    }

    private void updateBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.isOutOfBounds()) {
                bulletIterator.remove();
                continue;
            }
            for (Zombie zombie : zombies) {
                if (bullet.collidesWith(zombie)) {
                    bulletIterator.remove();
                    break;
                }
            }
            Iterator<GooProjectile> gooIterator = gooProjectiles.iterator();
            while (gooIterator.hasNext()) {
                GooProjectile goo = gooIterator.next();
                if (bullet.collidesWith(goo)) {
                    gooIterator.remove();
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }

    private void updateZombies() {
        Iterator<Zombie> iterator = zombies.iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            if (zombie.isFullyTyped() && zombie.getDeathAnimation().isFinished()) {
                if (!(zombie.getMovementStrategy() instanceof GooThrowingMovement)) {
                    iterator.remove();
                } else {
                    // Remove the zombie but keep its goo projectiles
                    List<GooProjectile> gooProjectiles = zombie.getGooProjectiles();
                    iterator.remove();
                    this.gooProjectiles.addAll(gooProjectiles);
                }
            } else {
                zombie.move();
                if (System.currentTimeMillis() - zombie.getSpawnTime() > 1000) { // 1 second delay before collision
                    if (!(zombie.getMovementStrategy() instanceof GooThrowingMovement)) {
                        preventZombieCollision(zombie, zombies.indexOf(zombie));
                    }
                    if (zombie.getY() >= player.getY()) {
                        notifier.notifyObservers("GAME_OVER");
                        gameOver = true;
                        stopMusic();
                        showEndScreen();
                        break;
                    }
                }
            }
        }
    }
    
    private void updateGooProjectiles() {
        Iterator<GooProjectile> iterator = gooProjectiles.iterator();
        while (iterator.hasNext()) {
            GooProjectile goo = iterator.next();
            goo.update();
            if (System.currentTimeMillis() - goo.getSpawnTime() > 1000) { // 1 second delay before collision
                if (goo.isOutOfBounds()) {
                    iterator.remove();
                } else if (goo.collidesWith(player)) {
                    // Handle collision with player
                    iterator.remove();
                }
            }
        }
    }

    private void preventZombieCollision(Zombie currentZombie, int currentIndex) {
        for (int i = 0; i < zombies.size(); i++) {
            if (i != currentIndex) {
                Zombie otherZombie = zombies.get(i);
                if (Math.abs(currentZombie.getX() - otherZombie.getX()) < 50 && Math.abs(currentZombie.getY() - otherZombie.getY()) < 50) {
                    // Move the current zombie away from the other zombie
                    int deltaX = currentZombie.getX() - otherZombie.getX();
                    int deltaY = currentZombie.getY() - otherZombie.getY();
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        currentZombie.setX(currentZombie.getX() + (deltaX > 0 ? 1 : -1));
                    } else {
                        currentZombie.setY(currentZombie.getY() + (deltaY > 0 ? 1 : -1));
                    }
                }
            }
        }
    }

    private void showEndScreen() {
        if (frame != null) {
            frame.getContentPane().removeAll();
            stopMusic(); // Ensure music is stopped when showing the end screen
            EndScreen endScreen = new EndScreen(frame, selectedMusic, selectedPlayerSkin, selectedEnemySkin, difficulty, score);
            frame.add(endScreen);
            frame.revalidate();
            frame.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        player.draw(g);
        for (Zombie zombie : zombies) {
            zombie.draw(g);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        drawGooProjectiles(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Wave: " + wave, 10, 30);
        g.drawString("Score: " + score, 10, 60);

        if (!waveMessage.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(new Color(1.0f, 1.0f, 1.0f, waveMessageAlpha));
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(waveMessage);
            g.drawString(waveMessage, (getWidth() - messageWidth) / 2, getHeight() / 2);
        }
    }

    private void drawGooProjectiles(Graphics g) {
        for (Zombie zombie : zombies) {
            for (GooProjectile goo : zombie.getGooProjectiles()) {
                goo.draw(g);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Game");
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);
    }
}