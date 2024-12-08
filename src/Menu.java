import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    private JComboBox<String> musicComboBox;
    private JComboBox<String> playerSkinComboBox;
    private JComboBox<String> enemySkinComboBox;
    private JButton startButton;
    private JFrame frame;

    public Menu(JFrame frame) {
        this.frame = frame;
        setTitle("Zombie Game Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        // Music selection
        JLabel musicLabel = new JLabel("Select Music:");
        musicComboBox = new JComboBox<>(new String[]{"tart", "viad", "yest"});
        add(musicLabel);
        add(musicComboBox);

        // Player skin selection
        JLabel playerSkinLabel = new JLabel("Select Player Skin:");
        playerSkinComboBox = new JComboBox<>(new String[]{"Skin1", "Skin2", "Skin3"});
        add(playerSkinLabel);
        add(playerSkinComboBox);

        // Enemy skin selection
        JLabel enemySkinLabel = new JLabel("Select Enemy Skin:");
        enemySkinComboBox = new JComboBox<>(new String[]{"Skin1", "Skin2", "Skin3"});
        add(enemySkinLabel);
        add(enemySkinComboBox);

        // Start button
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(new JLabel()); // Empty cell
        add(startButton);

        setVisible(true);
    }

    private void startGame() {
        String selectedMusic = (String) musicComboBox.getSelectedItem();
        String selectedPlayerSkin = (String) playerSkinComboBox.getSelectedItem();
        String selectedEnemySkin = (String) enemySkinComboBox.getSelectedItem();

        // Pass the selected options to the game
        frame.getContentPane().removeAll();
        Game game = new Game(selectedMusic, selectedPlayerSkin, selectedEnemySkin);
        game.setFrame(frame);
        frame.add(game);
        frame.revalidate();
        frame.repaint();

        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Game");
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}