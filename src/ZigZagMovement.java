public class ZigZagMovement implements MovementStrategy {
    private boolean movingRight = true;
    private static final int ZIGZAG_SPEED = 5; // Speed of horizontal movement
    private static final int ZIGZAG_DISTANCE = 800; // Width of the screen

    @Override
    public void move(Zombie zombie) {
        int currentX = zombie.getX();
        int targetX = movingRight ? currentX + ZIGZAG_SPEED : currentX - ZIGZAG_SPEED;

        if (targetX >= ZIGZAG_DISTANCE || targetX <= 0) {
            movingRight = !movingRight;
        }

        zombie.setX(targetX);

        // Move forward towards the target
        zombie.moveTowards(zombie.getTargetX(), zombie.getTargetY());
    }
}