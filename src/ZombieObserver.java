public class ZombieObserver implements Observer {
    @Override
    public void update(String event) {
        if (event.equals("ZOMBIE_KILLED")) {
            System.out.println("A zombie was killed!");
        } else if (event.equals("GAME_OVER")) {
            System.out.println("Game over! Zombies win.");
        } else {
            System.out.println(event); // Display the current wave's zombie types
        }
    }
}