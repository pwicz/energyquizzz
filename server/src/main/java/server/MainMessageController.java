package server;

import commons.Activity;
import commons.ClientMessage;
import commons.Game;
import commons.Player;
import commons.Question;
import commons.ServerMessage;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.api.ActivityController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import java.util.Comparator;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private ActivityController activityController;
    private HashMap<String, Game> games;
    private Game waitingRoom;
    private boolean sentToAll = false;

    // game options
    private final int questionsPerGame = 4;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate, ActivityController activityController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.activityController = activityController;

        games = new HashMap<>();
    }

    //CHECKSTYLE:OFF
    @MessageMapping("/general")
    public void handleClientMessages(ClientMessage msg) {
        ServerMessage result = null;
        try {
            Game g = null;
            Player p = null;
            if(games.get(msg.gameID) != null) {
                g = games.get(msg.gameID);
                if (g.getPlayerWithID(msg.playerID) != null)
                    p = g.getPlayerWithID(msg.playerID);
            }

            switch (msg.type) {

                case INIT_SINGLEPLAYER:
                    // first init the game
                    var initMsg = initSingleplayerGame(msg);
                    if (initMsg != null) {
                        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, initMsg);
                        // then send a first question
                        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                                nextQuestion(initMsg.score, games.get(initMsg.gameID)));
                    }
                    break;
                case INIT_MULTIPLAYER:
                    // if name is already taken
                    if(waitingRoom != null
                            && waitingRoom.getPlayers()
                            .stream().anyMatch(players ->
                                    (players.getName()).equalsIgnoreCase(msg.playerName)))
                        return;

                    initMultiGame(msg);
                    break;
                case START_GAME:
                    waitingRoom = new Game(new ArrayList<>(), UUID.randomUUID().toString());
                    games.put(waitingRoom.getID(), waitingRoom);
                    //no break on purpose
                case INIT_QUESTION:
                    System.out.println("[msg] init question");
                    multiplayerSendNewQuestions(g);
                    break;
                case SUBMIT_ANSWER:
                    updateScore(msg, p, g);

                    System.out.println("[msg] submit answer");
                    if(g.getPlayersAnswered() == g.getPlayers().size()){
                        // all players have answered
                        g.questionEndAction.cancel();
                        multiplayerShowAnswersProcedure(g);
                        System.out.println("[msg] all players submitted!");
                    }
                    break;
                case TEST:
                    // for testing purposes
                    result = new ServerMessage(ServerMessage.Type.TEST);
                    System.out.println("[test] message received");
                    break;
                case SUBMIT_SINGLEPLAYER:
                    if (msg.gameID == null || msg.playerID == null) return;
                    // check if specified game and player exist
                    if (g == null) return;
                    if (p == null || p.hasAnswered()) return;

                    p.setHasAnswered(true);
                    submitSingleplayer(msg, g, p);
                    break;
                case QUIT:
                    playerGameCorrectnessCheck(msg.gameID, msg.playerID);

                    // remove player from the game
                    Game game = games.get(msg.gameID);
                    game.getPlayers().remove(game.getPlayerWithID(msg.playerID));
                    // end game if there are no more players
                    if (game.getPlayers().size() == 0) endGame(game);
                    break;
                case QUIT_WAITING_ROOM:
                    if(waitingRoom == null) return;

                    // remove player from the waiting room
                    Player playerInWaitingRoom = waitingRoom.getPlayerWithID(msg.playerID);
                    waitingRoom.getPlayers().remove(playerInWaitingRoom);

                    // send update message to all other players

                    ServerMessage temp = new ServerMessage(ServerMessage.Type.EXTRA_PLAYER);
                    temp.playersWaiting = getWaitingListOfPlayers(waitingRoom);
                    sendMessageToAllPlayers(temp, waitingRoom);

                    break;
                default:
                    // unknown message
            }

            if(result == null) return;

            if(sentToAll){
                for (Player player: games.get(msg.gameID).getPlayers()) {
                    simpMessagingTemplate.convertAndSend("/topic/client/" + player.getID(), result);
                    sentToAll = false;
                }
            }else {
                simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, result);
            }
        } catch (MessagingException ex) {
            System.out.println("MessagingException on handleClientMessages: " + ex.getMessage());
        }
    }
    //CHECKSTYLE:ON
    
    private boolean playerGameCorrectnessCheck(String gameID, String playerID) {
        if (gameID == null || playerID == null) return false;

        // check if specified game and player exist

        if (!games.containsKey(gameID)) return false;
        Player p = games.get(gameID).getPlayerWithID(playerID);
        return p != null && !p.hasAnswered();
    }


    public void updateScore(ClientMessage msg, Player p, Game g) {
        g.setPlayersAnswered(g.getPlayersAnswered() + 1);

        int scoreForQuestion = 0;
        if (Objects.equals(msg.chosenActivity, g.getCorrectAnswerID())) {
            double answerTime = 10.0 - (System.currentTimeMillis() - g.getQuestionStartTime()) / 1000.0;
            scoreForQuestion = 100 + (int) (10 * answerTime);
        }
        p.setScore(p.getScore() + scoreForQuestion);
        p.setHasAnswered(true);
        p.setAnswer(msg.chosenActivity);
    }

    private ServerMessage initSingleplayerGame(ClientMessage msg) {
        // 0. Check if player name is correct
        if (msg.playerName == null || msg.playerName.isEmpty()) {
            // TODO: send error message
            return null;
        }

        // 1. Create a new game
        Game g = new Game(new ArrayList<>(), UUID.randomUUID().toString());
        g.addPlayer(new Player(msg.playerName, msg.playerID));
        games.put(g.getID(), g);

        // 2. Return a message with the gameID and an initial score
        ServerMessage result = new ServerMessage(ServerMessage.Type.NEW_SINGLEPLAYER_GAME);
        result.score = 0;
        result.gameID = g.getID();

        return result;
    }

    private ServerMessage nextQuestion(int playerScore, Game forGame) {
        ServerMessage result = new ServerMessage(ServerMessage.Type.NEXT_QUESTION);
        // TODO: Question class should be generated by a proper method
        List<Activity> selectedActivities =
                List.of(activityController.getRandom().getBody(),
                        activityController.getRandom().getBody(),
                        activityController.getRandom().getBody());
        result.question = new Question(selectedActivities, Question.Type.COMPARE);
        // TEMPORARY SOLUTION
        Activity max = selectedActivities.get(0);
        for (int i = 1; i < selectedActivities.size(); ++i) {
            Activity activity = selectedActivities.get(i);
            if (activity.consumptionInWh > max.consumptionInWh) max = activity;
        }

        // save the correct answer in the Game object

        forGame.setCorrectAnswerID(max.id);
        forGame.setQuestionStartTime(System.currentTimeMillis());

        result.score = playerScore;
        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;
        result.round = forGame.getRound();

        forGame.questionEndAction = new Timer();
        forGame.questionEndAction.schedule(new TimerTask() {
            @Override
            public void run() {
                // get the only player as it's singleplayer
                Player p = forGame.getPlayers().get(0);

                ClientMessage dummy = new ClientMessage(ClientMessage.Type.SUBMIT_SINGLEPLAYER);
                dummy.chosenActivity = -1L;
                dummy.playerID = p.getID();

                submitSingleplayer(dummy, forGame, p);
            }
        }, 10000);

        return result;
    }

    private void submitSingleplayer(ClientMessage msg, Game g, Player p) {
        // cancel 10s timeout if player has answered
        if(msg.chosenActivity != -1L) g.questionEndAction.cancel();

        // update player's score
        updateScore(msg, p, g);
        // send score msg
        // send the correct answer id and the picked answer id
        ServerMessage m = new ServerMessage(ServerMessage.Type.RESULT);
        m.correctAnswerID = g.getCorrectAnswerID();
        m.pickedAnswerID = msg.chosenActivity;
        m.score = p.getScore();
        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, m);

        if (g.getRound() < questionsPerGame) {
            // send next question after 3 seconds
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (g.hasEnded()) return;

                    g.setRound(g.getRound() + 1);
                    simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                            nextQuestion(p.getScore(), g));
                    p.setHasAnswered(false);
                }
            }, 3000);
        } else {
            // end game
            endGame(g);
            // send player to the leaderboard
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                            new ServerMessage(ServerMessage.Type.END));
                }
            }, 3000);
        }

    }

    private void endGame(Game g) {
        g.setHasEnded(true);
        games.remove(g.getID());
    }

    //make game with dummy players for now
    public void initMultiGame(ClientMessage msg) {
        if(waitingRoom == null) {
            waitingRoom = new Game(new ArrayList<>(), UUID.randomUUID().toString());
            games.put(waitingRoom.getID(), waitingRoom);
            waitingRoom.setMultiplayer(true);
        }

        // add player to waiting room and init game for him
        waitingRoom.addPlayer(new Player(msg.playerName, msg.playerID));
        ServerMessage result = new ServerMessage(ServerMessage.Type.INIT_PLAYER);
        result.gameID = waitingRoom.getID();
        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, result);

        // update waiting room players list
        ServerMessage temp = new ServerMessage(ServerMessage.Type.EXTRA_PLAYER);
        temp.playersWaiting = getWaitingListOfPlayers(waitingRoom);

        sendMessageToAllPlayers(temp, waitingRoom);
    }

    public List<String> getWaitingListOfPlayers(Game game){
        List<String> result = new ArrayList<>();

        int counter = 1;
        for (Player p : game.getPlayers()) {
            result.add("#" + counter++ + " " + p.getName());
        }

        return result;
    }

    public void sendMessageToAllPlayers(ServerMessage msg, Game g){
        for (Player p : g.getPlayers()) {
            simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), msg);
        }
    }

    public ServerMessage displayAnswer(Player p, Game g) {
        ServerMessage result = new ServerMessage(ServerMessage.Type.DISPLAY_ANSWER);
        result.topScores = getTopScores(g);
        result.score = p.getScore();
        result.pickedID = p.getAnswer();
        result.correctID = g.getCorrectAnswerID();
        return result;
    }

    public List<String> getTopScores(Game game) {
        List<Player> playerList = game.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getScore).thenComparing(Player::getID).reversed())
                .limit(5)
                .collect(Collectors.toList());
        List<String> topScores = new ArrayList<>();
        for (Player p : playerList) {
            topScores.add(p.getName() + ":" + p.getScore());
        }
        return topScores;
    }

    private void multiplayerShowAnswersProcedure(Game g){
        // first reveal answers
        System.out.println("[msg] revealing answers to all players");
        for(var p : g.getPlayers()){
            if(!p.hasAnswered()) p.setAnswer(-1L); // set incorrect ID so that 'you chose text' is not shown
            simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), displayAnswer(p, g));
        }

        // then, after 3 seconds, render the in-between-scores
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for(var p : g.getPlayers()){
                    System.out.println("[msg] revealing in-between-scores to all players");
                    ServerMessage result = new ServerMessage(ServerMessage.Type.DISPLAY_INBETWEENSCORES);
                    result.topScores = getTopScores(g);
                    result.questionCounter = g.getQuestionCounter();
                    result.totalQuestions = questionsPerGame;
                    simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), result);
                }
            }
        }, 3000);

        // then, after another 3 seconds (6 in total):
        if(g.getQuestionCounter() < questionsPerGame){
            // send new question if the game is still on
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("[msg] sending new questions to all players (end of procedure)");
                    multiplayerSendNewQuestions(g);
                }
            }, 6000);
        }
        else{
            // stop the game
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("[msg] ending game");
                    for(var p : g.getPlayers()){
                        ServerMessage result = new ServerMessage(ServerMessage.Type.END_GAME);
                        simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), result);
                    }
                }
            }, 6000);
        }

    }

    private void multiplayerSendNewQuestions(Game g){
        g.incQuestionCounter();

        ServerMessage result = new ServerMessage(ServerMessage.Type.LOAD_NEW_QUESTIONS);
        // TODO: Question class should be generated by a proper method
        List<Activity> selectedActivities =
                List.of(activityController.getRandom().getBody(),
                        activityController.getRandom().getBody(),
                        activityController.getRandom().getBody());
        result.question = new Question(selectedActivities, Question.Type.COMPARE);
        Activity max = selectedActivities.get(0);
        for (int i = 1; i < selectedActivities.size(); ++i) {
            Activity activity = selectedActivities.get(i);
            if (activity.consumptionInWh > max.consumptionInWh) max = activity;
        }

        g.setCorrectAnswerID(max.id);
        g.setQuestionStartTime(System.currentTimeMillis());
        g.setPlayersAnswered(0);

        g.questionEndAction = new Timer();
        g.questionEndAction.schedule(new TimerTask() {
            @Override
            public void run() {
                multiplayerShowAnswersProcedure(g);
            }
        }, 10000);

        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;

        for(var p : g.getPlayers()){
            p.setHasAnswered(false);
            result.score = p.getScore();
            simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), result);
        }
    }
}
