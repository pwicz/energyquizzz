package commons;

import java.util.List;

public class Game {

    private List<Player> players;
    private String ID;
    private boolean isMultiplayer;
    private int questionCounter = 0;

    public Game(List<Player> players, String ID) {
        this.players = players;
        this.ID = ID;
    }


    public void addPlayer(Player p){
        players.add(p);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public int getQuestionCounter() {
        return questionCounter;
    }

    public int incrementQuestionCounter() {
        return questionCounter++;
    }
}
