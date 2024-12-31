import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "../res/highscores.dat";
    private Set<HighScore> highScores;

    public HighScoreManager() {
        highScores = new HashSet<>();
        loadHighScores();
    }

    public void addHighScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        saveHighScores();
    }

    public Set<HighScore> getHighScores() {
        return highScores;
    }

    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScores = (Set<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // If the file doesn't exist or can't be read, start with an empty set
            highScores = new HashSet<>();
        }
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class HighScore implements Serializable {
        private String name;
        private int score;

        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int hashCode() {
            return name.hashCode() + score;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            HighScore that = (HighScore) obj;
            return score == that.score && name.equals(that.name);
        }
    }
}