package server;

import commons.Activity;
import commons.ClientMessage;
import commons.Game;
import commons.Player;
import commons.Question;
import commons.Score;
import commons.ServerMessage;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.api.ActivityController;
import server.api.ScoreController;

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
    private ScoreController scoreController;
    private HashMap<String, Game> games;
    private Game waitingRoom;
    private boolean sentToAll = false;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate,
                                 ActivityController activityController, ScoreController scoreController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.activityController = activityController;
        this.scoreController = scoreController;

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
                    result = nextMultiQuestion(
                            games.get(msg.gameID).getPlayerWithID(msg.playerID).getScore(), games.get(msg.gameID));
                    result.questionCounter = games.get(msg.gameID).getQuestionCounter();
                    System.out.println("[msg] init question");
                    sentToAll = true;
                    break;
                case SUBMIT_ANSWER:
                    updatScore(msg, p, g);
                    //make message
                    result = displayAnswer(p, g, msg);
                    //set time between each scene
                    showLeaderboard(msg);
                    showQuestions(msg);
                    System.out.println("[msg] submit answer");

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

                    Game game = games.get(msg.gameID);
                    if(game.isMultiplayer()){
                        // remove player from the game
                        game.getPlayers().remove(game.getPlayerWithID(msg.playerID));
                    }
                    // end game if there are no more players
                    if (game.getPlayers().size() == 0 || !game.isMultiplayer()) endGame(game);
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


    public void updatScore(ClientMessage msg, Player p, Game g) {

        games.get(msg.gameID).incCounter();

        int scoreForQuestion = 0;
        if (Objects.equals(msg.chosenActivity, g.getCorrectAnswerID())) {
            scoreForQuestion = 100 + (int) (100 * msg.time);
            p.setAnswerStatus(true);
        }
        p.setScore(p.getScore() + scoreForQuestion);

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

        // 2. Return a message with the first Question, gameID and an initial score
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

        result.score = playerScore;
        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;
        result.round = forGame.getRound();

        return result;
    }

    private void submitSingleplayer(ClientMessage msg, Game g, Player p) {
        // update player's score
        int scoreForQuestion = 0;
        if (Objects.equals(msg.chosenActivity, g.getCorrectAnswerID())) {
            scoreForQuestion = 100 + (int) (100 * msg.time);
        }
        p.setScore(p.getScore() + scoreForQuestion);
        // send score msg
        // send the correct answer id and the picked answer id
        ServerMessage m = new ServerMessage(ServerMessage.Type.RESULT);
        m.correctAnswerID = g.getCorrectAnswerID();
        m.pickedAnswerID = msg.chosenActivity;
        m.score = p.getScore();
        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, m);

        if (g.getRound() < 5) {
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

        if(!g.isMultiplayer() && g.getPlayers().size() > 0){
            Player p = g.getPlayers().get(0);
            scoreController.addScore(new Score(p.getName(), p.getScore()));
        }
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

    public ServerMessage displayAnswer(Player p, Game g, ClientMessage msg) {
        ServerMessage result = new ServerMessage(ServerMessage.Type.DISPLAY_ANSWER);
        result.topScores = getTopScores(games.get(msg.gameID));
        result.score = p.getScore();
        result.pickedID = msg.chosenActivity;
        result.correctID = g.getCorrectAnswerID();
        result.correctlyAnswered = correctAnswer(g);
        result.incorrectlyAnswered = incorrectAnswer(g);

        return result;
    }

    //shows inbetweenscore after 3 sceonds
    public void showLeaderboard(ClientMessage msg) {
        Thread myThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ServerMessage result = new ServerMessage(ServerMessage.Type.DISPLAY_INBETWEENSCORES);
//                result.topScores = getTopScores(games.get(msg.gameID));

            result.questionCounter = games.get(msg.gameID).getQuestionCounter();

            simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                    result);
        });
        myThread.start();
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

    /**
     * It returns the names of the people who answered correctly as a list of strings.
     * @param game the current game
     * @return a list of the names of people who answered correctly
     */
    public List<String> correctAnswer(Game game){
        List<Player> playerList = game.getPlayers().stream()
                .filter(p -> p.getAnswerStatus() == true)
                .collect(Collectors.toList());
        List<String> correctAnswers = new ArrayList<>();
        for (Player p : playerList) {
            correctAnswers.add(p.getName());
        }
        return correctAnswers;
    }

    /**
     * It returns the names of the people who answered incorrectly as a list of strings.
     * @param game the current game
     * @return a list of the names of people who answered incorrectly
     */
    public List<String> incorrectAnswer(Game game){
        List<Player> playerList = game.getPlayers().stream()
                .filter(p -> p.getAnswerStatus() == false)
                .collect(Collectors.toList());
        List<String> incorrectAnswers = new ArrayList<>();
        for (Player p : playerList) {
          incorrectAnswers.add(p.getName());
        }
        return incorrectAnswers;
    }

    //show questions again after 6 seconds
    public void showQuestions(ClientMessage msg) {
        Thread myThread = new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(Player p: games.get(msg.gameID).getPlayers()){
                p.setAnswerStatus(false);
            }

            if (games.get(msg.gameID).getQuestionCounter() < 20)
                simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, nextMultiQuestion(
                        games.get(msg.gameID).getPlayerWithID(msg.playerID).getScore(), games.get(msg.gameID))
                );
            else {
                simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                        new ServerMessage(ServerMessage.Type.END_GAME));
                games.get(msg.gameID).setQuestionCounter(0);
            }
        });
        myThread.start();
    }

    private ServerMessage nextMultiQuestion(int playerScore, Game forGame) {

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

        // save the correct answer in the Game object

        forGame.setCorrectAnswerID(max.id);
        result.score = playerScore;
        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;

        return result;
    }

}
