import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private static final Map<String, Object> cache = new HashMap<>();

    public static Object loadRes(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        Object resource = null;
        try {
            if (path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".gif")) {
                resource = ImageIO.read(new File(path));
            } else if (path.endsWith(".wav") || path.endsWith(".mp3")) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                resource = clip;
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("Error loading resource: " + path);
            e.printStackTrace();
        }

        if (resource != null) {
            cache.put(path, resource);
        }

        return resource;
    }
}