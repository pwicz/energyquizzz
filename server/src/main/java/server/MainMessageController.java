package server;

import commons.*;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.api.ActivityController;
import server.database.ActivityRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private ActivityController activityController;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate, ActivityController activityController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.activityController = activityController;
    }

    @MessageMapping("/general")
    public void handleClientMessages(ClientMessage msg){
        ServerMessage result = null;

        switch(msg.type){
            case INIT_MULTIPLAYER:
                // do something
                break;
            case INIT_SINGLEPLAYER:
                // do something else
                break;
            case INIT_QUESTION:
                result = initMultiplayerGame(msg);
                break;
            case SUBMIT_ANSWER:
                result = new ServerMessage(ServerMessage.Type.DISPLAY_ANSWER);
                showLeaderboard(msg.playerID);
                showQuestions(msg.playerID);
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

    public void showLeaderboard(String playerID) {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simpMessagingTemplate.convertAndSend("/topic/client/" + playerID,
                        new ServerMessage(ServerMessage.Type.DISPLAY_INBETWEENSCORES));
            }
        };
        myThread.start();
    }

    public void showQuestions(String playerID) {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simpMessagingTemplate.convertAndSend("/topic/client/" + playerID,
                        new ServerMessage(ServerMessage.Type.LOAD_NEW_QUESTIONS));
            }
        };
        myThread.start();
    }

    private ServerMessage initMultiplayerGame(ClientMessage msg){

        // 1. Create a new game
        Game g = new Game(new ArrayList<>(), UUID.randomUUID().toString());
        g.addPlayer(new Player("Mike", msg.playerID));

        // 2. Return a message with the first Question, gameID and an initial score
        ServerMessage result = nextQuestion(0);
        g.setMultiplayer(true);

        // TODO: Question class should be generated by a proper method
        result.gameID = g.getID();

        return result;
    }


    private ServerMessage nextQuestion(int playerScore){
        ServerMessage result = new ServerMessage(ServerMessage.Type.LOAD_NEW_QUESTIONS);
        // TODO: Question class should be generated by a proper method
        List<Activity> selectedActivities =
                List.of(activityController.getRandom().getBody(),
                        activityController.getRandom().getBody(),
                        activityController.getRandom().getBody());
        result.question = new Question(selectedActivities, Question.Type.COMPARE);
        result.score = playerScore;
        result.timerFull = 10.0; // 10 seconds
        result.timerFraction = 1.0;

        return result;
    }

}
