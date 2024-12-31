import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JPanel {
    private JFrame frame;
    private String selectedMusic;
    private String selectedPlayerSkin;
    private String selectedEnemySkin;
    private String difficulty;
    private int score;
    private HighScoreManager highScoreManager;

    public EndScreen(JFrame frame, String selectedMusic, String selectedPlayerSkin, String selectedEnemySkin, String difficulty, int score) {
        this.frame = frame;
        this.selectedMusic = selectedMusic;
        this.selectedPlayerSkin = selectedPlayerSkin;
        this.selectedEnemySkin = selectedEnemySkin;
        this.difficulty = difficulty;
        this.score = score;
        this.highScoreManager = new HighScoreManager();
        setLayout(new GridLayout(4, 1));

        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 36));
        add(gameOverLabel);

        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(scoreLabel);

        JPanel saveScorePanel = new JPanel(new BorderLayout());
        JTextField nameField = new JTextField();
        JButton saveButton = new JButton("Save Score");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (!name.isEmpty()) {
                    highScoreManager.addHighScore(name, score);
                    JOptionPane.showMessageDialog(frame, "Score saved!");
                }
            }
        });
        saveScorePanel.add(nameField, BorderLayout.CENTER);
        saveScorePanel.add(saveButton, BorderLayout.EAST);
        add(saveScorePanel);

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMainMenu();
            }
        });
        add(mainMenuButton);
    }

    private void backToMainMenu() {
        frame.getContentPane().removeAll();
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.revalidate();
        frame.repaint();
    }
}