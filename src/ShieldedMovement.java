public class ShieldedMovement implements MovementStrategy {
    @Override
    public void move(Zombie zombie) {
        zombie.moveTowards(zombie.getTargetX(), zombie.getTargetY());
    }
}