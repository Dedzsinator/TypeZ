import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class Menu extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JFrame frame;
    private String selectedPlayerSkin = "p1";
    private String selectedMusic = "tart";
    private String selectedDifficulty = "Normal"; // Default difficulty
    private HighScoreManager highScoreManager;

    public Menu(JFrame frame) {
        this.frame = frame;
        this.highScoreManager = new HighScoreManager();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Main Menu
        JPanel mainMenu = createMainMenu();
        mainPanel.add(mainMenu, "Main Menu");

        // Difficulty Selector
        JPanel difficultySelector = createDifficultySelector();
        mainPanel.add(difficultySelector, "Difficulty Selector");

        // Skin Selector
        JPanel skinSelector = createSkinSelector();
        mainPanel.add(skinSelector, "Skin Selector");

        // Music Selector
        JPanel musicSelector = createMusicSelector();
        mainPanel.add(musicSelector, "Music Selector");

        // High Scores Screen
        JPanel highScoresScreen = createHighScoresScreen();
        mainPanel.add(highScoresScreen, "High Scores Screen");

        // Credits Screen
        JPanel creditsScreen = createCreditsScreen();
        mainPanel.add(creditsScreen, "Credits Screen");

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.addActionListener(_ -> cardLayout.show(mainPanel, "Difficulty Selector"));
        panel.add(playButton);

        JButton skinsButton = new JButton("Skins");
        skinsButton.setFont(new Font("Arial", Font.BOLD, 24));
        skinsButton.addActionListener(_ -> cardLayout.show(mainPanel, "Skin Selector"));
        panel.add(skinsButton);

        JButton musicsButton = new JButton("Musics");
        musicsButton.setFont(new Font("Arial", Font.BOLD, 24));
        musicsButton.addActionListener(_ -> cardLayout.show(mainPanel, "Music Selector"));
        panel.add(musicsButton);

        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.setFont(new Font("Arial", Font.BOLD, 24));
        highScoresButton.addActionListener(_ -> cardLayout.show(mainPanel, "High Scores Screen"));
        panel.add(highScoresButton);

        JButton creditsButton = new JButton("Credits");
        creditsButton.setFont(new Font("Arial", Font.BOLD, 24));
        creditsButton.addActionListener(_ -> cardLayout.show(mainPanel, "Credits Screen"));
        panel.add(creditsButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.addActionListener(_ -> System.exit(0));
        panel.add(exitButton);

        return panel;
    }

    private JPanel createDifficultySelector() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Select Difficulty", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton easyButton = new JButton("Easy");
        easyButton.setFont(new Font("Arial", Font.BOLD, 24));
        easyButton.addActionListener(_ -> {
            selectedDifficulty = "Easy";
            startGame();
        });
        buttonsPanel.add(easyButton);

        JButton mediumButton = new JButton("Medium");
        mediumButton.setFont(new Font("Arial", Font.BOLD, 24));
        mediumButton.addActionListener(_ -> {
            selectedDifficulty = "Medium";
            startGame();
        });
        buttonsPanel.add(mediumButton);

        JButton hardButton = new JButton("Hard");
        hardButton.setFont(new Font("Arial", Font.BOLD, 24));
        hardButton.addActionListener(_ -> {
            selectedDifficulty = "Hard";
            startGame();
        });
        buttonsPanel.add(hardButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Main Menu"));
        buttonsPanel.add(backButton);

        panel.add(buttonsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSkinSelector() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Select Skins", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel comboBoxPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel playerSkinLabel = new JLabel("Player Skin:");
        playerSkinLabel.setFont(new Font("Arial", Font.BOLD, 18));
        comboBoxPanel.add(playerSkinLabel);

        JComboBox<String> playerSkinComboBox = new JComboBox<>(new String[]{"p1", "p2", "p3"});
        playerSkinComboBox.addActionListener(_ -> selectedPlayerSkin = (String) playerSkinComboBox.getSelectedItem());
        comboBoxPanel.add(playerSkinComboBox);

        JLabel enemySkinLabel = new JLabel("Gun Skin:");
        enemySkinLabel.setFont(new Font("Arial", Font.BOLD, 18));
        comboBoxPanel.add(enemySkinLabel);

        JComboBox<String> enemySkinComboBox = new JComboBox<>(new String[]{"Skin1", "Skin2", "Skin3"});
        comboBoxPanel.add(enemySkinComboBox);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Main Menu"));
        comboBoxPanel.add(backButton);

        panel.add(comboBoxPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMusicSelector() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Select Music", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel comboBoxPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel musicLabel = new JLabel("Music:");
        musicLabel.setFont(new Font("Arial", Font.BOLD, 18));
        comboBoxPanel.add(musicLabel);

        JComboBox<String> musicComboBox = new JComboBox<>(new String[]{"tart", "viad", "yest"});
        musicComboBox.addActionListener(_ -> selectedMusic = (String) musicComboBox.getSelectedItem());
        comboBoxPanel.add(musicComboBox);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Main Menu"));
        comboBoxPanel.add(backButton);

        panel.add(comboBoxPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHighScoresScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("High Scores", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JTextArea highScoresText = new JTextArea();
        highScoresText.setFont(new Font("Arial", Font.PLAIN, 18));
        highScoresText.setEditable(false);
        highScoresText.setBackground(panel.getBackground());

        StringBuilder highScoresBuilder = new StringBuilder();
        Set<HighScoreManager.HighScore> highScores = highScoreManager.getHighScores();
        for (HighScoreManager.HighScore highScore : highScores) {
            highScoresBuilder.append(highScore.getName()).append(": ").append(highScore.getScore()).append("\n");
        }
        highScoresText.setText(highScoresBuilder.toString());

        panel.add(new JScrollPane(highScoresText), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Main Menu"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCreditsScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Credits", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JTextArea creditsText = new JTextArea(
                "Game developed by:\n" +
                " Dégi Nándor\n" +
                "Github: \n" +
                "\n" +
                "And all the players!"
        );
        creditsText.setFont(new Font("Arial", Font.PLAIN, 18));
        creditsText.setEditable(false);
        creditsText.setBackground(panel.getBackground());
        panel.add(creditsText, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Main Menu"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private void startGame() {
        // Pass the selected options to the game
        frame.getContentPane().removeAll();
        Game game = new Game(selectedMusic, selectedPlayerSkin, "p1", selectedDifficulty); // Use selected values
        game.setFrame(frame);
        frame.add(game);
        frame.revalidate();
        frame.repaint();

        game.requestFocusInWindow(); // Ensure the game panel has focus for key events

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