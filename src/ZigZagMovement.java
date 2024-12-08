public class ZigZagMovement implements MovementStrategy {
    private boolean movingRight = true;

    @Override
    public void move(Zombie zombie) {
        zombie.move(); // Forward movement
        if (movingRight) {
            zombie.setX(zombie.getX() + 2); // Move right
        } else {
            zombie.setX(zombie.getX() - 2); // Move left
        }
        movingRight = !movingRight; // Change direction
    }
}