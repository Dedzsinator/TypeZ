public class ZigZagMovement implements MovementStrategy {
    private boolean movingRight = true;
    private static final int ZIGZAG_SPEED = 5;
    private static final int ZIGZAG_DISTANCE = 800;

    @Override
    public void move(Zombie zombie) {
        int currentX = zombie.getX();
        int targetX = movingRight ? currentX + ZIGZAG_SPEED : currentX - ZIGZAG_SPEED;

        if (targetX >= ZIGZAG_DISTANCE || targetX <= 0) {
            movingRight = !movingRight;
        }

        zombie.setX(targetX);

        zombie.moveTowards(zombie.getTargetX(), zombie.getTargetY());
    }
}