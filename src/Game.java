import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends JPanel implements Runnable {
    private Player player;
    private List<Zombie> zombies;
    private GameEventNotifier notifier;
    private WordLoader wordLoader;
    private int wave;
    private int maxDifficulty;
    private String selectedMusic;
    private String selectedPlayerSkin;
    private String selectedEnemySkin;
    private MusicPlayer musicPlayer;
    private JFrame frame;

    public Game(String selectedMusic, String selectedPlayerSkin, String selectedEnemySkin) {
        this.selectedMusic = selectedMusic;
        this.selectedPlayerSkin = selectedPlayerSkin;
        this.selectedEnemySkin = selectedEnemySkin;

        player = new Player(375, 550, selectedPlayerSkin); // Assuming screen width is 800
        zombies = new ArrayList<>();
        notifier = new GameEventNotifier();
        notifier.addObserver(new ZombieObserver());
        wave = 1;
        maxDifficulty = 10;

        try {
            wordLoader = new WordLoader("../res/words.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        musicPlayer = new MusicPlayer();
        playSelectedMusic();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char typedChar = e.getKeyChar();
                checkZombieKilled(typedChar);
            }
        });

        startNewWave();
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private void playSelectedMusic() {
        String musicFilePath = "../res/" + selectedMusic + ".wav"; // Adjust the path as needed
        musicPlayer.play(musicFilePath);
    }

    private void startNewWave() {
        int numberOfZombies = wave * 5; // Example formula for number of zombies
        Random random = new Random();
        for (int i = 0; i < numberOfZombies; i++) {
            int x = random.nextInt(750); // Random x position (assuming screen width is 800)
            WordLoader.Word word = wordLoader.getRandomWord(maxDifficulty);
            zombies.add(new Zombie(x, 0, word.getWord(), 2, selectedEnemySkin));
        }
    }

    private void checkZombieKilled(char typedChar) {
        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);
            zombie.addTypedLetter(typedChar);
            if (zombie.isFullyTyped()) {
                zombies.remove(i);
                notifier.notifyObservers("ZOMBIE_KILLED");
                break;
            }
        }
        if (zombies.isEmpty()) {
            wave++;
            maxDifficulty += 10; // Increase difficulty for next wave
            startNewWave();
        }
    }

    @Override
    public void run() {
        while (true) {
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
        for (Zombie zombie : zombies) {
            zombie.moveTowards(player.getX(), player.getY());
            if (zombie.getY() >= player.getY()) {
                notifier.notifyObservers("GAME_OVER");
                showEndScreen();
                break;
            }
        }
    }

    private void showEndScreen() {
        if (frame != null) {
            frame.getContentPane().removeAll();
            EndScreen endScreen = new EndScreen(frame);
            frame.add(endScreen);
            frame.revalidate();
            frame.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Zombie zombie : zombies) {
            zombie.draw(g);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Game");
        Game game = new Game("tart", "Skin1", "Skin1");
        game.setFrame(frame);
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread gameThread = new Thread(game);
        gameThread.start();
    }
}