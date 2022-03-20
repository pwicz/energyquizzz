package commons;

import java.util.List;
import java.util.Objects;

public class Game {

    private List<Player> players;
    private String ID;
    private boolean isMultiplayer;
    private Long correctAnswerID;

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

    public Player getPlayerWithID(String playerID){
        for(var p : players){
            if(Objects.equals(p.getID(), playerID)) return p;
        }

        return null;
    }

    public Long getCorrectAnswerID() {
        return correctAnswerID;
    }

    public void setCorrectAnswerID(Long correctAnswerID) {
        this.correctAnswerID = correctAnswerID;
    }
}
