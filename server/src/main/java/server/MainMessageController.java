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
import java.util.Objects;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private ActivityController activityController;
    private HashMap<String, Game> games = new HashMap<>();

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate, ActivityController activityController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.activityController = activityController;
    }

    @MessageMapping("/general")
    public void handleClientMessages(ClientMessage msg){
        ServerMessage result = null;
        switch(msg.type){

            case INIT_GAME:
                result = initGame(msg);
                break;
            case INIT_MULTIPLAYER:
                // do something
                break;
            case INIT_SINGLEPLAYER:
                // do something else
                break;
            case INIT_QUESTION:

                result = nextMultiQuestion(
                        games.get(msg.gameID).getPlayerWithID(msg.playerID).getScore(), games.get(msg.gameID));

//                result.questionCounter = games.get(msg.gameID).getQuestionCounter();
                break;
            case SUBMIT_ANSWER:
                Game g = games.get(msg.gameID);
                Player p = g.getPlayerWithID(msg.playerID);
                updatScore(msg, p, g);
                //make message
                result = displayAnswer(p, g, msg);
                //set time between each scene
                showLeaderboard(msg);
                showQuestions(msg);
                break;
            case TEST:
                // for testing purposes
                result = new ServerMessage(ServerMessage.Type.TEST);
                break;
            default:
                // unknown message
        }

        // if we created a valid message, we send it to a specific client at /client/{ID}
        if(result == null) return;

        try{
            simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, result);
        }
        catch(MessagingException ex){
            System.out.println("MessagingException on handleClientMessages: " + ex.getMessage());
        }
    }

    public void updatScore(ClientMessage msg, Player p, Game g){

        games.get(msg.gameID).incCounter();

        int scoreForQuestion = 0;
        if(Objects.equals(msg.chosenActivity, g.getCorrectAnswerID())){
            scoreForQuestion = 100 + (int)(100 * msg.time);
        }
        p.setScore(p.getScore() + scoreForQuestion);
    }

    //make game with dummy players for now
    public ServerMessage initGame(ClientMessage msg){
        Game g = new Game(new ArrayList<>(), UUID.randomUUID().toString());
        g.addPlayer(new Player("Alex", "222"));
        g.addPlayer(new Player("Mike", msg.playerID));
        g.addPlayer(new Player("awd", "222"));
        g.addPlayer(new Player("awhd", "200"));
        g.addPlayer(new Player("sdhgsge", "278"));
        g.addPlayer(new Player("awdafg", "256"));

        games.put(g.getID(), g);
        ServerMessage result = new ServerMessage(ServerMessage.Type.INIT_PLAYER);
        result.gameID = g.getID();

        return result;
    }

    public ServerMessage displayAnswer(Player p, Game g, ClientMessage msg){
        ServerMessage result = new ServerMessage(ServerMessage.Type.DISPLAY_ANSWER);
        result.topScores = getTopScores(games.get(msg.gameID));
        result.score = p.getScore();
        result.pickedID = msg.chosenActivity;
        result.correctID = g.getCorrectAnswerID();
        return result;
    }

    //shows inbetweenscore after 3 sceonds
    public void showLeaderboard(ClientMessage msg) {
        Thread myThread = new Thread() {
            @Override
            public void run() {
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
            }
        };
        myThread.start();
    }

    public List<String> getTopScores(Game game){
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

    //show questions again after 6 seconds
    public void showQuestions(ClientMessage msg) {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(games.get(msg.gameID).getQuestionCounter() <=20)
                     simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, nextMultiQuestion(
                             games.get(msg.gameID).getPlayerWithID(msg.playerID).getScore(), games.get(msg.gameID))
                     );
                else {
                    simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID,
                                                            new ServerMessage(ServerMessage.Type.END_GAME));
                    games.get(msg.gameID).setQuestionCounter(0);
                }
            }
        };
        myThread.start();
    }

    private ServerMessage nextMultiQuestion(int playerScore, Game forGame){

        ServerMessage result = new ServerMessage(ServerMessage.Type.LOAD_NEW_QUESTIONS);
        // TODO: Question class should be generated by a proper method
        List<Activity> selectedActivities =
                List.of(activityController.getRandom().getBody(),
                        activityController.getRandom().getBody(),
                        activityController.getRandom().getBody());
        result.question = new Question(selectedActivities, Question.Type.COMPARE);
        Activity max = selectedActivities.get(0);
        for(int i = 1; i < selectedActivities.size(); ++i){
            Activity activity = selectedActivities.get(i);
            if(activity.consumptionInWh > max.consumptionInWh) max = activity;
        }

        // save the correct answer in the Game object

        forGame.setCorrectAnswerID(max.id);
        result.score = playerScore;
        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;

        return result;
    }

}
