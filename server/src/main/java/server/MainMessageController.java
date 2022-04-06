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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
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

    // game options
    private final int questionsPerGame = 4;
    private final double timeToAnswer = 10.0;
    private final int scoreBase = 100;          // points for correct answer
    private final int scoreBonusPerSecond = 10; // extra points for every second left

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate,
                                 ActivityController activityController, ScoreController scoreController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.activityController = activityController;
        this.scoreController = scoreController;

        games = new HashMap<>();

        waitingRoom = new Game(new ArrayList<>(), UUID.randomUUID().toString());
        waitingRoom.setMultiplayer(true);
    }

    //CHECKSTYLE:OFF
    @MessageMapping("/general")
    public void handleClientMessages(ClientMessage msg) {
        ServerMessage result = null;
        try {
            Game game = games.get(msg.gameID);
            Player player = null;
            if(game != null) {
                player = game.getPlayerWithID(msg.playerID);
            }

            switch (msg.type) {

                case INIT_SINGLEPLAYER:
                    // first init the game
                    var initMsg = initSingleplayerGame(msg);
                    if (initMsg != null) {
                        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, initMsg);
                        // then send a first question
                        simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                                singleplayerSendNewQuestion(initMsg.score, games.get(initMsg.gameID)));
                    }
                    break;
                case INIT_MULTIPLAYER:

                    if(msg.serverName == null){
                        //show server screen
                    }
                    // if name is already taken
                    if(waitingRoom != null
                            && waitingRoom.getPlayers()
                            .stream().anyMatch(players ->
                                    (players.getName()).equalsIgnoreCase(msg.playerName)))
                        return;

                    joinWaitingRoom(msg);
                    break;
                case START_MULTIPLAYER:
                    game = waitingRoom;

                    games.put(waitingRoom.getID(), waitingRoom);
                    waitingRoom = new Game(new ArrayList<>(), UUID.randomUUID().toString());

                    System.out.println("[msg] init question");
                    multiplayerSendNewQuestions(game);
                    break;
                case SUBMIT_ANSWER:
                    if (game == null || player == null) return;

                    processAnswer(msg, player, game);

                    System.out.println("[msg] submit answer");
                    if(game.getPlayersAnswered() == game.getPlayers().size()){
                        // all players have answered
                        game.questionEndAction.cancel();
                        multiplayerShowAnswersProcedure(game);
                        System.out.println("[msg] all players submitted!");
                    }
                    break;
                case TEST:
                    // for testing purposes
                    result = new ServerMessage(ServerMessage.Type.TEST);
                    System.out.println("[test] message received");
                    simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, result);
                    break;
                case SUBMIT_SINGLEPLAYER:
                    if (game == null || player == null) return;
                    // check if specified game and player exist
                    if (player.hasAnswered() || game.isMultiplayer()) return;

                    player.setHasAnswered(true);
                    submitSingleplayer(msg, game, player);
                    break;
                case QUIT:
                    if (game == null || player == null) return;

                    // remove player from the game
                    game.getPlayers().remove(player);
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
                case PING:
                    if(msg.playerID == null) return;
                    var pingResponse = new ServerMessage(ServerMessage.Type.PING);
                    simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, pingResponse);
                    break;
                case SHOW_EMOJI:
                    ServerMessage serverMessage = new ServerMessage(ServerMessage.Type.SHOW_EMOJI);
                    serverMessage.imgName = msg.imgName;
                    serverMessage.namePLayerEmoji = games.get(msg.gameID).getPlayerWithID(msg.playerID).getName();
                    sendMessageToAllPlayers(serverMessage, games.get(msg.gameID));
                    break;
                default:
                    // unknown message
            }
        } catch (MessagingException ex) {
            System.out.println("MessagingException on handleClientMessages: " + ex.getMessage());
        }
    }
    //CHECKSTYLE:ON

    public void processAnswer(ClientMessage msg, Player p, Game g) {
        g.setPlayersAnswered(g.getPlayersAnswered() + 1);

        int scoreForQuestion = 0;
        double timeLeft = timeToAnswer - (System.currentTimeMillis() - g.getQuestionStartTime()) / 1000.0;

        if(g.getType() == Question.Type.ESTIMATION){
            // chosenActivity & correctAnswerID are here consumptions
            double answerRatio = msg.chosenActivity / (double) g.getCorrectAnswerID();

            // give points to players who answer in the range 0.5 - 1.5
            if(answerRatio >= 0.5 && answerRatio <= 1.5)
            {
                int points = 100;
                // subtract at most 99 points
                points -= Math.min(Math.abs(1.0 - answerRatio) * 2 * 100, 99);

                // add at most 5*points time bonus
                scoreForQuestion = points + (int) (points * (timeLeft / 2));
                System.out.println("Points received: " + points + " and time bonus: " + (scoreForQuestion - points));
                p.setAnswerStatus(true);
            }

        } else if (Objects.equals(msg.chosenActivity, g.getCorrectAnswerID())) {
            scoreForQuestion = scoreBase + (int) (scoreBonusPerSecond * timeLeft);
            p.setAnswerStatus(true);
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

    /**
     * Generates a question randomly out of the available types
     * @return new randomly generated question
     */
    private Question generateQuestion(){
        //Chose number for random question type
        Random rand = new Random();
        int randomType = rand.nextInt(Question.Type.values().length);

        // TEST ONLY TODO: remove after testing
        randomType = 3;

        //Type = Compare
        if(randomType == 0){
            //Get three activities from database
             List<Activity> selectedActivities =
                    List.of(Objects.requireNonNull(activityController.getRandom().getBody()),
                        activityController.getRandom().getBody(),
                        activityController.getRandom().getBody());
            return new Question(selectedActivities, Question.Type.COMPARE);
            //Type = Guess
        } else if(randomType == 1){
            //Get random activity from database
            List<Activity> selectedActivity =  List.of(
                    Objects.requireNonNull(activityController.getRandom().getBody()));

            //Get three possible options
            List<Long> options = new ArrayList<>();
            long correct = selectedActivity.get(0).consumptionInWh;    //Get correct answer
            //Add options to list
            options.add(correct);
            options.addAll(getOptions(correct));

            return new Question(selectedActivity, Question.Type.GUESS, options);
            //Type = How many times
        } else if(randomType == 2) {
            List<Activity> selectedActivities = new ArrayList<>();
            //Get 2 random activities
            selectedActivities.add(activityController.getRandom().getBody());
            selectedActivities.add(activityController.getRandom().getBody());
            //Check if activities are equal
            while(selectedActivities.get(1).consumptionInWh <= selectedActivities.get(0).consumptionInWh){
                selectedActivities.set(1, activityController.getRandom().getBody());
            }
            //Get three options
            List<Long> options = new ArrayList<>();
            long correct = selectedActivities.get(1).consumptionInWh / selectedActivities.get(0).consumptionInWh;
            options.add(correct);
            options.addAll(getOptions(correct));

            return new Question(selectedActivities, Question.Type.HOW_MANY_TIMES, options);

        }
        //Type = Estimation
        List<Activity> selectedActivity = List.of(
                Objects.requireNonNull(activityController.getRandom().getBody()));
        System.out.println(selectedActivity.get(0).consumptionInWh);
        return new Question(selectedActivity, Question.Type.ESTIMATION);

    }

    /**
     * Generates a random number in the appropriate bounds of a correct answer
     * @param correct answer which determines bounds
     * @return option given a correct answer
     */
    private long getRandomAnswer(long correct){
        Random rand = new Random();
        return (long) ((correct/2) + Math.random() * ((2 * correct) - (correct/2)));
    }

    /**
     * gets unique list of options given a correct answer
     * @param correct answer for which to create options
     * @return list with two possible options for a correct answer
     */
    private List<Long> getOptions(long correct){
        List<Long> res = new ArrayList<>();
        long randomAnswer01 = getRandomAnswer(correct);
        long randomAnswer02 = getRandomAnswer(correct);
        //Check if selected options are equal
        while(randomAnswer01 == randomAnswer02 || randomAnswer01 == correct || randomAnswer02 == correct){
            if(randomAnswer01 == correct) randomAnswer01 = getRandomAnswer(correct);
            if(randomAnswer02 == correct || randomAnswer02 == randomAnswer01){
                randomAnswer02 = getRandomAnswer(correct);
            }
        }
        res.add(randomAnswer01);
        res.add(randomAnswer02);
        return res;
    }

    private ServerMessage singleplayerSendNewQuestion(int playerScore, Game forGame) {
        ServerMessage result = new ServerMessage(ServerMessage.Type.NEXT_QUESTION);
        result.question = generateQuestion();
        forGame.setType(result.question.type);

        forGame.setCorrectAnswerID(result.question.getCorrect());
        forGame.setQuestionStartTime(System.currentTimeMillis());

        result.score = playerScore;
        result.timerFull = timeToAnswer; // 10 seconds
        result.timerFraction = 1.0;
        result.round = forGame.getRound();

        forGame.questionEndAction = new Timer();
        forGame.questionEndAction.schedule(new TimerTask() {
            @Override
            public void run() {
                // get the only player as it's singleplayer
                if(forGame.getPlayers().size() == 0) return;
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
        processAnswer(msg, p, g);
        // send score msg
        // send the correct answer id and the picked answer id
        ServerMessage m = new ServerMessage(ServerMessage.Type.RESULT);
        m.typeQ = g.getType();
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
                            singleplayerSendNewQuestion(p.getScore(), g));
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

    public void joinWaitingRoom(ClientMessage msg) {
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
        result.correctlyAnswered = correctAnswer(g);
        result.incorrectlyAnswered = incorrectAnswer(g);
        result.typeQ = g.getType();
        result.answeredCorrect = p.getAnswerStatus();

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

    private void multiplayerShowAnswersProcedure(Game g){
        // first reveal answers
        System.out.println("[msg] revealing answers to all players");
        for(var p : g.getPlayers()){
            if(!p.hasAnswered()) p.setAnswer(-1L); // set incorrect ID so that 'you chose text' is not shown
            simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), displayAnswer(p, g));
        }

        if(g.hasEnded()) return;

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

        result.question = generateQuestion();
        g.setCorrectAnswerID(result.question.getCorrect());
        g.setType(result.question.type);
        g.setQuestionStartTime(System.currentTimeMillis());
        g.setPlayersAnswered(0);

        g.questionEndAction = new Timer();
        g.questionEndAction.schedule(new TimerTask() {
            @Override
            public void run() {
                multiplayerShowAnswersProcedure(g);
            }
        }, 10000);

        result.timerFull = timeToAnswer; // 10 seconds
        result.timerFraction = 1.0;

        for(var p : g.getPlayers()){
            p.setHasAnswered(false);
            p.setAnswerStatus(false);
            result.score = p.getScore();
            simpMessagingTemplate.convertAndSend("/topic/client/" + p.getID(), result);
        }
    }
}
