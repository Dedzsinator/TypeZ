public class StraightLineMovement implements MovementStrategy {
    @Override
    public void move(Zombie zombie) {
        zombie.moveTowards(zombie.getTargetX(), zombie.getTargetY()); // Move in a straight line towards the player
    }
}