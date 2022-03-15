package commons;

import java.util.List;

public class Game {

    private List<Player> players;
    private long ID;

    public Game(List<Player> players, long ID) {
        this.players = players;
        this.ID = ID;
    }
}
