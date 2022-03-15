package server;

import commons.ClientMessage;
import commons.ServerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@RequestMapping("/")
public class MainMessageController {

    private SimpMessagingTemplate simpMessagingTemplate;

    public MainMessageController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
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
            default:
                // unknown message
        }

        // if we created a valid message, we send it to a specific client at /client/{ID}
        if(result != null) this.simpMessagingTemplate.convertAndSend("/topic/client/" + msg.playerID, result);
    }
}
