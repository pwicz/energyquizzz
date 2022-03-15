package commons;

import java.util.List;

public class Game {

    private List<Player> players;
    private int ID;

    public Game(List<Player> players, int ID) {
        this.players = players;
        this.ID = ID;
    }
}
