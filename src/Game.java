import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.*;

public class Game extends JPanel implements Runnable {
    private Player player;
    private List<Zombie> zombies;
    private List<Bullet> bullets;
    private GameEventNotifier notifier;
    private WordLoader wordLoader;
    private int wave;
    private int maxDifficulty;
    private String selectedMusic;
    private String selectedPlayerSkin;
    private String selectedGunSkin;
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
    private Timer zombieSpawnTimer;
    private int zombiesToSpawn;
    private int zombiesSpawned;
    private Clip shootingSoundClip;
    private Clip boomSoundClip;
    private int lifesavers = 3;
    private double lifesaverBarProgress = 0.0;

    public Game(String selectedMusic, String selectedPlayerSkin, String selectedGunSkin, String difficulty) {
        this.selectedMusic = selectedMusic;
        this.selectedPlayerSkin = selectedPlayerSkin;
        this.selectedGunSkin = selectedGunSkin;
        this.difficulty = difficulty;
    
        initializeGame();
    }

    private void initializeGame() {
        System.out.println(this.difficulty);
        player = new Player(375, 520, selectedPlayerSkin, selectedGunSkin);
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
        lifesavers = 3;
        lifesaverBarProgress = 0.0;
    
        loadResources();
        setupKeyListener();
        setupComponentListener();
    
        startNewWave();
    }

    private void loadResources() {
        try {
            wordLoader = new WordLoader("../res/words.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        musicPlayer = new MusicPlayer();
        playSelectedMusic();

        backgroundImage = (Image) ResourceLoader.loadRes("../res/bg.png");

        gunFireSprites = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            gunFireSprites.add((Image) ResourceLoader.loadRes("../res/misc/gf" + i + ".png"));
        }

        shootingSoundClip = (Clip) ResourceLoader.loadRes("../res/misc/sh.wav");
        boomSoundClip = (Clip) ResourceLoader.loadRes("../res/misc/bo.wav");
    }

    private void setupKeyListener() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!gameOver) {
                    char typedChar = e.getKeyChar();
                    if (typedChar == ' ') {
                        activateLifesaver();
                    } else {
                        boolean correctTyping = checkZombieKilled(typedChar);
                        shootBullet();
                        updateLifesaverBar(correctTyping);
                    }
                }
            }
    
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver && e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    switchTarget();
                    repaint();
                }
            }
        });
    }

    private void setupComponentListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePlayerPosition();
            }
        });
    }

    private void switchTarget() {
        if (zombies.isEmpty()) {
            focusedZombie = null;
            return;
        }
    
        if (focusedZombie == null) {
            focusedZombie = zombies.get(0);
        } else {
            int currentIndex = zombies.indexOf(focusedZombie);
            int nextIndex = (currentIndex + 1) % zombies.size();
            focusedZombie = zombies.get(nextIndex);
        }
    }

    private void updateLifesaverBar(boolean correctTyping) {
        if (correctTyping) {
            lifesaverBarProgress += Math.log(1 + combo) / 10.0;
            if (lifesaverBarProgress >= 1.0) {
                lifesaverBarProgress = 0.0;
                lifesavers++;
            }
        }
    }
    
    private void activateLifesaver() {
        if (lifesavers > 0) {
            lifesavers--;
            List<Zombie> closestZombies = getClosestZombies(5);
            Iterator<Zombie> iterator = closestZombies.iterator();
            while (iterator.hasNext()) {
                Zombie zombie = iterator.next();
                if (zombie instanceof ShieldedZombie) {
                    ShieldedZombie shieldedZombie = (ShieldedZombie) zombie;
                    if (!shieldedZombie.isArmorDestroyed()) {
                        shieldedZombie.destroyArmor();
                    } else {
                        iterator.remove();
                        zombies.remove(zombie);
                    }
                } else {
                    iterator.remove();
                    zombies.remove(zombie);
                }
            }
            playBoomSound();
        }
    }
    
    private void playBoomSound() {
        if (boomSoundClip != null) {
            boomSoundClip.setFramePosition(0);
            boomSoundClip.start();
        }
    }
    
    private List<Zombie> getClosestZombies(int count) {
        List<Zombie> closestZombies = new ArrayList<>(zombies);
        closestZombies.sort(Comparator.comparingDouble(zombie -> calculateDistance(player.getX(), player.getY(), zombie.getX(), zombie.getY())));
        return closestZombies.subList(0, Math.min(count, closestZombies.size()));
    }

    private void updatePlayerPosition() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        player.setX(panelWidth / 2 - 25);
        player.setY(panelHeight - 80);
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private void playSelectedMusic() {
        String musicFilePath = "../res/" + selectedMusic + ".wav";
        musicPlayer.play(musicFilePath);
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    private void startNewWave() {
        System.out.println("Uj hullam: " + wave);
        int numberOfZombies = (int) (5 * Math.log(wave + 1));
        zombiesToSpawn = numberOfZombies;
        zombiesSpawned = 0;

        Random random = new Random();
        double[] probabilities = getZombieProbabilities();

        zombieSpawnTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (zombiesSpawned < zombiesToSpawn) {
                    spawnZombie(random, probabilities);
                    zombiesSpawned++;
                } else {
                    zombieSpawnTimer.stop();
                    waveInProgress = true;
                }
            }
        });
        zombieSpawnTimer.start();
    }

    private void spawnZombie(Random random, double[] probabilities) {
        int x, y;
        boolean overlap;
        do {
            x = random.nextInt(750);
            y = 0;
            overlap = false;
            for (Zombie zombie : zombies) {
                if (Math.abs(zombie.getX() - x) < 50 && Math.abs(zombie.getY() - y) < 50) {
                    overlap = true;
                    break;
                }
            }
        } while (overlap);
    
        WordLoader.Word word = wordLoader.getRandomWord(maxDifficulty);
        MovementStrategy strategy = getMovementStrategy(random, probabilities);
    
        Zombie zombie;
        if (strategy instanceof ShieldedMovement) {
            WordLoader.Word armorWord = wordLoader.getRandomWord(maxDifficulty);
            zombie = new ShieldedZombie(x, y, word.getWord(), armorWord.getWord(), 1.5, strategy);
        } else {
            zombie = new Zombie(x, y, word.getWord(), 1.5, strategy);
        }
        zombie.setTarget(player.getX(), player.getY());
        zombies.add(zombie);
    }

    private MovementStrategy getMovementStrategy(Random random, double[] probabilities) {
        double rand = random.nextDouble();
        if (rand < probabilities[0]) {
            return new StraightLineMovement();
        } else if (rand < probabilities[0] + probabilities[1]) {
            return new ZigZagMovement();
        } else {
            return new ShieldedMovement();
        }
    }
    
    private double[] getZombieProbabilities() {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return new double[]{0.6, 0.2, 0.2}; // Simple, ZigZag, Shielded
            case "medium":
                return new double[]{0.3, 0.3, 0.4};
            case "hard":
                return new double[]{0.2, 0.3, 0.5};
            default:
                return new double[]{0.3, 0.3, 0.4};
        }
    }

    private boolean checkZombieKilled(char typedChar) {
        boolean correctTyping = false;
        if (focusedZombie == null) {
            Zombie closestZombie = null;
            double shortestDistance = Double.MAX_VALUE;
            for (Zombie zombie : zombies) {
                if (zombie instanceof ShieldedZombie) {
                    ShieldedZombie shieldedZombie = (ShieldedZombie) zombie;
                    if (!shieldedZombie.isArmorDestroyed() && shieldedZombie.getArmorWord().startsWith(Character.toString(typedChar))) {
                        double distance = calculateDistance(player.getX(), player.getY(), shieldedZombie.getX(), shieldedZombie.getY());
                        if (distance < shortestDistance) {
                            shortestDistance = distance;
                            closestZombie = shieldedZombie;
                        }
                    } else if (shieldedZombie.isArmorDestroyed() && shieldedZombie.getWord().startsWith(Character.toString(typedChar))) {
                        double distance = calculateDistance(player.getX(), player.getY(), shieldedZombie.getX(), shieldedZombie.getY());
                        if (distance < shortestDistance) {
                            shortestDistance = distance;
                            closestZombie = shieldedZombie;
                        }
                    }
                } else if (zombie.getWord().startsWith(Character.toString(typedChar))) {
                    double distance = calculateDistance(player.getX(), player.getY(), zombie.getX(), zombie.getY());
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        closestZombie = zombie;
                    }
                }
            }
            focusedZombie = closestZombie;
        }
    
        if (focusedZombie != null) {
            if (focusedZombie instanceof ShieldedZombie) {
                ShieldedZombie shieldedZombie = (ShieldedZombie) focusedZombie;
                if (!shieldedZombie.isArmorDestroyed()) {
                    if (shieldedZombie.getArmorWord().charAt(shieldedZombie.getTypedArmorWord().length()) == typedChar) {
                        shieldedZombie.addTypedArmorLetter(typedChar);
                        correctTyping = true;
                        if (shieldedZombie.isArmorDestroyed()) {
                            focusedZombie = null;
                        }
                    } else {
                        lifesaverBarProgress = 0.0;
                    }
                } else {
                    if (shieldedZombie.getWord().charAt(shieldedZombie.getTypedWord().length()) == typedChar) {
                        shieldedZombie.addTypedLetter(typedChar);
                        correctTyping = true;
                        if (shieldedZombie.isFullyTyped()) {
                            killZombie(shieldedZombie);
                        }
                    } else {
                        lifesaverBarProgress = 0.0;
                    }
                }
            } else {
                if (focusedZombie.getWord().charAt(focusedZombie.getTypedWord().length()) == typedChar) {
                    focusedZombie.addTypedLetter(typedChar);
                    correctTyping = true;
                    if (focusedZombie.isFullyTyped()) {
                        killZombie(focusedZombie);
                    }
                } else {
                    lifesaverBarProgress = 0.0;
                }
            }
        }
    
        if (zombies.isEmpty() && waveInProgress) {
            waveInProgress = false;
            wave++;
            maxDifficulty += 10;
            showWaveMessage("Hullám " + (wave - 1) + " leütve! Készülj mert még jönnek!");
        }
    
        return correctTyping;
    }
    
    private void shootBullet() {
        Zombie closestZombie = null;
        double shortestDistance = Double.MAX_VALUE;
        int shortestWordLength = Integer.MAX_VALUE;
    
        for (Zombie zombie : zombies) {
            double distance = calculateDistance(player.getX(), player.getY(), zombie.getX(), zombie.getY());
            if (distance < shortestDistance || (distance == shortestDistance && zombie.getWord().length() < shortestWordLength)) {
                shortestDistance = distance;
                shortestWordLength = zombie.getWord().length();
                closestZombie = zombie;
            }
        }
    
        if (closestZombie != null) {
            player.aimAt(closestZombie.getX(), closestZombie.getY());
        }
        player.shoot();
        bullets.add(new Bullet(player.getX() + 25, player.getY() + 25, player.getGunAngle(), closestZombie));
        gunFireFrame = 0;
    
        if (shootingSoundClip != null) {
            Clip clip = (Clip) ResourceLoader.loadRes("../res/misc/sh.wav");
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }
    }
    
    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    private void killZombie(Zombie zombie) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKillTime <= 3000) {
            combo += 0.1;
        } else {
            combo = 1.0;
        }
        lastKillTime = currentTime;
    
        int longestCorrectSubsequence = zombie.getTypedWord().length();
        score += (10 * longestCorrectSubsequence * combo);
    
        zombie.die();
        notifier.notifyObservers("ZOMBIE_KILLED");
        focusedZombie = null;
    }

    private void showWaveMessage(String message) {
        waveMessage = message;
        waveMessageAlpha = 1.0f;
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                while (waveMessageAlpha > 0) {
                    waveMessageAlpha -= 0.01f;
                    waveMessageAlpha = Math.max(waveMessageAlpha, 0);
                    repaint();
                    Thread.sleep(50);
                }
                Thread.sleep(2000);
                startNewWave();
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
                Thread.sleep(16); //kb 60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        player.update();
        updateBullets();
        updateZombies();
    
        if (zombies.isEmpty() && waveInProgress) {
            waveInProgress = false;
            wave++;
            maxDifficulty += 10;
            bullets.clear();
            showWaveMessage("Hullám " + (wave - 1) + " leütve! Készülj mert még jönnek!");
        }
    
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
        }
    }
    
    private void updateZombies() {
        Iterator<Zombie> iterator = zombies.iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            if (zombie.isFullyTyped() && zombie.getDeathAnimation().isFinished()) {
                iterator.remove();
            } else {
                zombie.move();
                if (System.currentTimeMillis() - zombie.getSpawnTime() > 1000) {
                    if (!(zombie.getMovementStrategy() instanceof ShieldedMovement)) {
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
    
        if (zombies.isEmpty() && waveInProgress) {
            waveInProgress = false;
            wave++;
            maxDifficulty += 10;
            bullets.clear();
            showWaveMessage("Hullám " + (wave - 1) + " leütve! Készülj mert még jönnek!");
        }
    }

    private void preventZombieCollision(Zombie currentZombie, int currentIndex) {
        for (int i = 0; i < zombies.size(); i++) {
            if (i != currentIndex) {
                Zombie otherZombie = zombies.get(i);
                if (Math.abs(currentZombie.getX() - otherZombie.getX()) < 50 && Math.abs(currentZombie.getY() - otherZombie.getY()) < 50) {
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
            stopMusic();
            EndScreen endScreen = new EndScreen(frame, selectedMusic, selectedPlayerSkin, selectedGunSkin, difficulty, score, wave);
            frame.add(endScreen);
            frame.revalidate();
            frame.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        player.draw(g2d);
        for (Zombie zombie : zombies) {
            zombie.draw(g2d);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g2d);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Hullám: " + wave, 10, 30);
        g2d.drawString("Pontszám: " + score, 10, 60);

        drawCombo(g2d);

        if (!waveMessage.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.setColor(new Color(1.0f, 1.0f, 1.0f, waveMessageAlpha));
            FontMetrics fm = g2d.getFontMetrics();
            int messageWidth = fm.stringWidth(waveMessage);
            g2d.drawString(waveMessage, (getWidth() - messageWidth) / 2, getHeight() / 2);
        }

        drawLifesaverBar(g2d);
        drawTargetIndicator(g2d);

        g2d.dispose();
}

    private void drawTargetIndicator(Graphics2D g) {
        if (focusedZombie != null) {
            int x = focusedZombie.getX();
            int y = focusedZombie.getY();
            int width = focusedZombie.getWidth();
            int height = focusedZombie.getHeight();
    
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(1));
            g.drawRect(x, y, width + 10, height + 10);
        }
    }

    private void drawCombo(Graphics2D g) {
        int x = 10;
        int y = 90;
    
        if (combo > 5) {
            int shakeRange = 5;
            int shakeX = (int) (Math.random() * shakeRange - shakeRange / 2);
            int shakeY = (int) (Math.random() * shakeRange - shakeRange / 2);
            x += shakeX;
            y += shakeY;
            g.setColor(new Color(255, 69, 0));
        } else {
            g.setColor(Color.WHITE);
        }
    
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Kombó: " + String.format("%.1f", combo), x, y);
    }

    private void drawLifesaverBar(Graphics g) {
        int barWidth = getWidth() - 20;
        int barHeight = 20;
        int barX = 10;
        int barY = getHeight() - 30;
    
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
    
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, (int) (barWidth * lifesaverBarProgress), barHeight);
    
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Mentők: " + lifesavers, barX + barWidth - 150, barY - 10);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TypeZ");
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}