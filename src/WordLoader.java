import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordLoader {
    private List<Word> words;

    public WordLoader(String filePath) throws IOException {
        words = new ArrayList<>();
        loadWords(filePath);
    }

    private void loadWords(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String word = parts[0];
                int difficulty = Integer.parseInt(parts[1]);
                words.add(new Word(word, difficulty));
            }
        }
        reader.close();
    }

    public Word getRandomWord(int maxDifficulty) {
        Random random = new Random();
        List<Word> filteredWords = new ArrayList<>();
        for (Word word : words) {
            if (word.getDifficulty() <= maxDifficulty) {
                filteredWords.add(word);
            }
        }
        if (filteredWords.isEmpty()) {
            return new Word("default", 1);
        }
        return filteredWords.get(random.nextInt(filteredWords.size()));
    }

    public static class Word {
        private String word;
        private int difficulty;

        public Word(String word, int difficulty) {
            this.word = word;
            this.difficulty = difficulty;
        }

        public String getWord() {
            return word;
        }

        public int getDifficulty() {
            return difficulty;
        }
    }
}