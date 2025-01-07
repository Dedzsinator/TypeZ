import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import java.awt.*;
import java.util.Set;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.io.IOException;

public class Menu extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JFrame frame;
    /* Defaults */
    private String selectedPlayerSkin = "p1";
    private String selectedMusic = "metal";
    private String selectedDifficulty = "Normal";
    private String selectedGunSkin = "g1";

    private HighScoreManager highScoreManager;

    public Menu(JFrame frame) {
        this.frame = frame;
        this.highScoreManager = new HighScoreManager();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        addPanel("Főmenü", createMainMenu());
        addPanel("Nehézségek", createDifficultySelector());
        addPanel("Skinek", createSkinSelector());
        addPanel("Zenék", createMusicSelector());
        addPanel("Pontok", createHighScoresScreen());

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addPanel(String name, JPanel panel) {
        mainPanel.add(panel, name);
    }

    private JPanel createMainMenu() {
        String[] buttonLabels = {"Játék", "Kinézetek", "Zenék", "Legjobbak", "Kilépés"};
        ActionListener[] actions = new ActionListener[]{
                _ -> cardLayout.show(mainPanel, "Nehézségek"),
                _ -> cardLayout.show(mainPanel, "Skinek"),
                _ -> cardLayout.show(mainPanel, "Zenék"),
                _ -> cardLayout.show(mainPanel, "Pontok"),
                _ -> System.exit(0)
        };
    
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    
        JPanel buttonPanel = new JPanel(new GridLayout(buttonLabels.length, 1, 10, 10));
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.addActionListener(actions[i]);
            buttonPanel.add(button);
        }
    
        panel.add(buttonPanel, BorderLayout.CENTER);
    
        JButton helpCreditsButton = new JButton("Infók és segítség");
        helpCreditsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        helpCreditsButton.addActionListener(_ -> showHelpCreditsPopup());
    
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(helpCreditsButton, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
    
        return panel;
    }

    private void showHelpCreditsPopup() {
        String helpCreditsMessage = "<html>Üdv a TypeZ játékban!<br><br>" +
                "Hogyan játsz:<br>" +
                "- A billentyűzet segítségével írd be a zombikon megjelenő szavakat, hogy a karakter lelője őket.<br>" +
                "- Nyomd a szóköz-t ha pácban vagy.<br>" +
                "- Nyomd meg a Shift-et (baloldalit) ha nem tetszik akit éppen lősz.<br>" +
                "- Próbálj túlélni minél több hullámot! (úgyse fog sikerülni...)<br><br>" +
                "Infók:<br>" +
                "Fejlesztő:<br>" +
                "Dégi Nándor<br>" +
                "Designer:<br>" +
                "Szabadságon<br>" +
                "Github repó: <a href='https://github.com/Dedzsinator/TypeZ'>https://github.com/Dedzsinator/TypeZ</a><br><br>" +    
                "Köszi, hogy kipróbáltad!<br><br>";

        JEditorPane editorPane = new JEditorPane("text/html", helpCreditsMessage);
        editorPane.setOpaque(false);
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JOptionPane.showMessageDialog(frame, editorPane, "Infók és segítség", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createDifficultySelector() {
        return createPanelWithBackButton("Válassz nehézséget", new String[]{"Könnyű", "Közepes", "Nehéz"},
                new ActionListener[]{
                        _ -> { selectedDifficulty = "Easy"; startGame(); },
                        _ -> { selectedDifficulty = "Medium"; startGame(); },
                        _ -> { selectedDifficulty = "Hard"; startGame(); }
                });
    }

    private JPanel createSkinSelector() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    
        JLabel label = new JLabel("Select Skins", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);
    
        JPanel comboBoxPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        comboBoxPanel.add(createLabel("Játékos kinézetek:"));
        comboBoxPanel.add(createComboBox(new String[]{"p1", "p2", "p3"}, e -> selectedPlayerSkin = (String) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        comboBoxPanel.add(createLabel("Fegyver kinézetek:"));
        comboBoxPanel.add(createComboBox(new String[]{"g1", "g2", "g3"}, e -> selectedGunSkin = (String) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        comboBoxPanel.add(createBackButton());
    
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
        comboBoxPanel.add(createLabel("Zenék:"));
        comboBoxPanel.add(createComboBox(new String[]{"metal", "metal2", "chill", "folk"}, e -> selectedMusic = (String) ((JComboBox<?>) e.getSource()).getSelectedItem()));
        comboBoxPanel.add(createBackButton());

        panel.add(comboBoxPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHighScoresScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Legjobb írók", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JTextArea highScoresText = new JTextArea();
        highScoresText.setFont(new Font("Arial", Font.PLAIN, 18));
        highScoresText.setEditable(false);
        highScoresText.setBackground(panel.getBackground());

        StringBuilder highScoresBuilder = new StringBuilder();
        Set<HighScoreManager.HighScore> highScores = highScoreManager.getHighScores();
        for (HighScoreManager.HighScore highScore : highScores) {
            highScoresBuilder.append(highScore.getName()).append(": ").append(highScore.getScore()).append(" (Wave ").append(highScore.getMaxWave()).append(")\n");
        }
        highScoresText.setText(highScoresBuilder.toString());

        panel.add(new JScrollPane(highScoresText), BorderLayout.CENTER);
        panel.add(createBackButton(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPanelWithBackButton(String title, String[] buttonLabels, ActionListener[] actions) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(buttonLabels.length + 1, 1, 10, 10));
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.addActionListener(actions[i]);
            buttonsPanel.add(button);
        }

        buttonsPanel.add(createBackButton());
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        return label;
    }

    private JComboBox<String> createComboBox(String[] items, ActionListener actionListener) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        if (actionListener != null) {
            comboBox.addActionListener(actionListener);
        }
        return comboBox;
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("Vissza");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "Főmenü"));
        return backButton;
    }

    private void startGame() {
        frame.getContentPane().removeAll();
        Game game = new Game(selectedMusic, selectedPlayerSkin, selectedGunSkin, selectedDifficulty);
        game.setFrame(frame);
        frame.add(game);
        frame.revalidate();
        frame.repaint();
    
        game.requestFocusInWindow();
    
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TypeZ");
        Menu menu = new Menu(frame);
        frame.add(menu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}