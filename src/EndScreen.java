import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JPanel {
    private JFrame frame;
    private String selectedMusic;
    private String selectedPlayerSkin;
    private String selectedGunSkin;
    private String difficulty;
    private HighScoreManager highScoreManager;

    public EndScreen(JFrame frame, String selectedMusic, String selectedPlayerSkin, String selectedGunSkin, String difficulty, int score, int maxWave) {
        this.frame = frame;
        this.selectedMusic = selectedMusic;
        this.selectedPlayerSkin = selectedPlayerSkin;
        this.selectedGunSkin = selectedGunSkin;
        this.difficulty = difficulty;
        this.highScoreManager = new HighScoreManager();
        setLayout(new GridLayout(5, 1));

        JLabel gameOverLabel = new JLabel("Meghaltál!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 36));
        add(gameOverLabel);

        JLabel scoreLabel = new JLabel("Pontszámod: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(scoreLabel);

        JLabel waveLabel = new JLabel("Legnagyobb tisztított hullám: " + maxWave, SwingConstants.CENTER);
        waveLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(waveLabel);

        JPanel saveScorePanel = new JPanel(new BorderLayout());
        JTextField nameField = new JTextField();
        JButton saveButton = new JButton("Tudasd mindenkivel, hogy mennyire kemény vagy!");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (!name.isEmpty()) {
                    highScoreManager.addHighScore(name, score, maxWave);
                    JOptionPane.showMessageDialog(frame, "Pontszám elmentve!");
                }
            }
        });
        saveScorePanel.add(nameField, BorderLayout.CENTER);
        saveScorePanel.add(saveButton, BorderLayout.EAST);
        add(saveScorePanel);

        JButton retryButton = new JButton("Sose adom fel!");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(retryButton);

        JButton mainMenuButton = new JButton("Vissza a főmenübe");
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMainMenu();
            }
        });
        add(mainMenuButton);
    }

    private void restartGame() {
        frame.getContentPane().removeAll();
        Game game = new Game(selectedMusic, selectedPlayerSkin, selectedGunSkin, difficulty);
        game.setFrame(frame);
        frame.add(game);
        frame.revalidate();
        frame.repaint();

        game.requestFocusInWindow();

        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private void backToMainMenu() {
        frame.getContentPane().removeAll();
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.revalidate();
        frame.repaint();
    }
}