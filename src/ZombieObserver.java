public class ZombieObserver implements Observer {
    @Override
    public void update(String event) {
        if (event.equals("ZOMBIE_KILLED")) {
            System.out.println("Halott zombi!");
        } else if (event.equals("GAME_OVER")) {
            System.out.println("Jatek vege, zombik nyertek.");
        } else {
            System.out.println(event);
        }
    }
}