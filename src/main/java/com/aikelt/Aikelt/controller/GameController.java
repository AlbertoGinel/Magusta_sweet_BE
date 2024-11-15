package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.Game;
import com.aikelt.Aikelt.service.CustomUserDetailsService;
import com.aikelt.Aikelt.service.GameService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public GameController(GameService gameService, CustomUserDetailsService customUserDetailsService) {
        this.gameService = gameService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/getgame")
    public Game getGame() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        UUID id = customUserDetailsService.findIDByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        return gameService.getGame(id);
    }


    @PostMapping("/creategame")
    public Game createGame(@RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        String username = authentication.getName();
        UUID id = customUserDetailsService.findIDByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found for username: " + username));

        String typeString = (String) requestBody.get("type");
        if (typeString == null || !Arrays.stream(Game.GameType.values()).anyMatch(e -> e.name().equalsIgnoreCase(typeString))) {
            throw new IllegalArgumentException("Invalid game type: " + typeString);
        }
        Game.GameType type = Game.GameType.valueOf(typeString.toUpperCase());

        int length = (Integer) requestBody.get("length");

        // You might also want to validate the length
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length: must be greater than 0");
        }

        this.gameService.createGame(type, length, id);

        Game game = this.gameService.getGame(id);
        if (game == null) {
            throw new NoSuchElementException("Game not found after creation");
        }

        return game;
    }




    @PostMapping("/updateGameEEWords")
    public Game updateGameEEWords(@RequestBody Map<String, Object> requestBody) {
        UUID user = UUID.fromString((String) requestBody.get("user"));

        this.gameService.updateGameEEWords(user);

        return this.gameService.getGame(user);

    }


    @PostMapping("/gameResponse")
    public Game gameResponse(@RequestBody Map<String, Object> requestBody) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        UUID id = customUserDetailsService.findIDByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));


        String humanResponse = (String) requestBody.get("humanResponse");

        this.gameService.scoreResponse(id,humanResponse);

        return this.gameService.getGame(id);
    }

    @PostMapping("/finishGame")
    public void finishGame(@RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        UUID id = customUserDetailsService.findIDByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        JSONArray wordsList = new JSONArray((List<?>) requestBody.get("wordsList"));

        // Call the service with the extracted userID and wordsList
        gameService.finishGame(wordsList, id);

    }
}



