import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private List<Image> frames;
    private int currentFrame;
    private long frameDuration;
    private long lastFrameTime;
    private boolean loop;

    public Animation(long frameDuration) {
        this.frames = new ArrayList<>();
        this.frameDuration = frameDuration;
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.loop = true;
    }

    public void addFrame(Image frame) {
        frames.add(frame);
    }

    public void update() {
        if (frames.isEmpty()) {
            return; // No frames to update
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDuration) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }
    }

    public Image getCurrentFrame() {
        if (frames.isEmpty()) {
            return null; // No frames to display
        }
        return frames.get(currentFrame);
    }

    public void reset() {
        currentFrame = 0;
        lastFrameTime = 0;
    }

    public boolean isFinished() {
        return !loop && currentFrame == frames.size() - 1;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}