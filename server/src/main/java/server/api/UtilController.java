package server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/util")
public class UtilController {

    /**
     * Creates a unique ID for a player
     * @return the newly created player ID
     */
    @GetMapping("/getPlayerID")
    public String getPlayerID() {
        return UUID.randomUUID().toString();
    }
}
