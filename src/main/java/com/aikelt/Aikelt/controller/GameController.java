package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.Game;
import com.aikelt.Aikelt.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/getgame")
    public Game getGame(@RequestBody Map<String, Object> requestBody) {

        UUID user = UUID.fromString((String) requestBody.get("user"));

        return gameService.getGame(user);
    }


    @PostMapping("/creategame")
    public Game createGame(@RequestBody Map<String, Object> requestBody) {
        // Extract values from requestBody map
        String typeString = (String) requestBody.get("type");
        int length = (Integer) requestBody.get("length");
        UUID user = UUID.fromString((String) requestBody.get("user"));

        // Convert the typeString to Game.GameType (if necessary)
        Game.GameType type = Game.GameType.valueOf(typeString.toUpperCase());

        // Call the service to create the game
        this.gameService.createGame(type, length, user);

        // Return the created game
        return this.gameService.getGame(user);
    }

    @PostMapping("/updateGameEEWords")
    public Game updateGameEEWords(@RequestBody Map<String, Object> requestBody) {
        UUID user = UUID.fromString((String) requestBody.get("user"));

        this.gameService.updateGameEEWords(user);

        return this.gameService.getGame(user);

    }


    @PostMapping("/gameResponse")
    public Game gameResponse(@RequestBody Map<String, Object> requestBody) {
        UUID user = UUID.fromString((String) requestBody.get("user"));
        String humanResponse = (String) requestBody.get("humanResponse");

        this.gameService.scoreResponse(user,humanResponse);

        return this.gameService.getGame(user);

    }
}



