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

    public void addHighScore(String name, int score, int maxWave) {
        highScores.add(new HighScore(name, score, maxWave));
        saveHighScores();
    }

    public Set<HighScore> getHighScores() {
        return highScores;
    }

    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScores = (Set<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
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
        private int maxWave;

        public HighScore(String name, int score, int maxWave) {
            this.name = name;
            this.score = score;
            this.maxWave = maxWave;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public int getMaxWave() {
            return maxWave;
        }

        @Override
        public int hashCode() {
            return name.hashCode() + score + maxWave;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            HighScore that = (HighScore) obj;
            return score == that.score && maxWave == that.maxWave && name.equals(that.name);
        }
    }
}