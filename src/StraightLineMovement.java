public class StraightLineMovement implements MovementStrategy {
    @Override
    public void move(Zombie zombie) {
        zombie.move(); // Default straight-line movement
    }
}