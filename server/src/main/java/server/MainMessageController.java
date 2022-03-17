package server;

import commons.ClientMessage;
import commons.Game;
import commons.Player;
import commons.ServerMessage;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private HashMap<UUID, Game> games;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

        games = new HashMap<>();
    }

    @MessageMapping("/general")
    public void handleClientMessages(ClientMessage msg){
        ServerMessage result = null;

        switch(msg.type){
            case INIT_MULTIPLAYER:
                // do something
                break;
            case INIT_SINGLEPLAYER:

                // 0. Check if player name is correct
                if(msg.playerName == null || msg.playerName.isEmpty()){
                    // TODO: send error message
                    return;
                }

                // 1. Create a new game
                Game g = new Game(new ArrayList<>(), UUID.randomUUID().toString());
                g.addPlayer(new Player(msg.playerName, msg.playerID));

                // 2. Return a message with the first Question and an initial score
                result = new ServerMessage(ServerMessage.Type.NEW_SINGLEPLAYER_GAME);
                result.question = null; // TODO: Use proper Question class
                                        // TODO: Question class should be generated by a proper method
                result.score = 0;
                break;
            case TEST:
                // for testing purposes
                result = new ServerMessage(ServerMessage.Type.TEST);
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

}
