import java.util.List;

public class GooThrowingMovement implements MovementStrategy {
    private static final int THROW_INTERVAL = 5000; // Time between throws in milliseconds
    private static final double GOO_SPEED = 0.25; // Slower speed of the goo projectiles
    private long lastThrowTime = 0;

    @Override
    public void move(Zombie zombie) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrowTime >= THROW_INTERVAL) {
            throwGoo(zombie);
            lastThrowTime = currentTime;
        }

        // Move the zombie towards the player at a slower speed
        zombie.moveTowards(zombie.getTargetX(), zombie.getTargetY(), zombie.getSpeed() / 2.0);
    }

    private void throwGoo(Zombie zombie) {
        List<GooProjectile> gooProjectiles = zombie.getGooProjectiles();
        String word = zombie.getWord();
        for (char letter : word.toCharArray()) {
            GooProjectile goo = new GooProjectile(zombie.getX(), zombie.getY(), letter, GOO_SPEED);
            goo.setTarget(zombie.getTargetX(), zombie.getTargetY());
            gooProjectiles.add(goo);
        }
    }
}