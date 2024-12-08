import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JPanel {
    private JFrame frame;

    public EndScreen(JFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(3, 1));

        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 36));
        add(gameOverLabel);

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);

        JButton mainMenuButton = new JButton("Main Menu");
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
        Game game = new Game("tart", "Skin1", "Skin1");
        game.setFrame(frame);
        frame.add(game);
        frame.revalidate();
        frame.repaint();
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