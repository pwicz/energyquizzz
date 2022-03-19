package server;

import commons.*;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private final ActivityRepository repo;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate, ActivityRepository repo) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.repo = repo;
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

    private ServerMessage initMultiplayerGame(ClientMessage msg){

        // 1. Create a new game
        Game g = new Game(new ArrayList<>(), UUID.randomUUID().toString());
        g.addPlayer(new Player("Mike", msg.playerID));

        // 2. Return a message with the first Question, gameID and an initial score
        ServerMessage result = new ServerMessage(ServerMessage.Type.LOAD_NEW_QUESTIONS);
        result.question = new Question(new ArrayList<>(), Question.Type.COMPARE);
        g.setMultiplayer(true);

        // TODO: Question class should be generated by a proper method
        result.score = 0;
        result.gameID = g.getID();

        return result;
    }

}
