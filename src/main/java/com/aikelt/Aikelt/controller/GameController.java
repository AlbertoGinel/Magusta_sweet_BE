package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.Game;
import com.aikelt.Aikelt.service.GameService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PreAuthorize("hasRole('PLAYER') or hasRole('ADMIN')")
    @PostMapping("/getgame")
    public Game getGame(@RequestBody Map<String, Object> requestBody, Principal principal) {
        UUID requestedUserId = UUID.fromString((String) requestBody.get("user"));

        // Retrieve the currently authenticated user's ID from Principal
        UUID authenticatedUserId = UUID.fromString(principal.getName());

        // If the user is a PLAYER, they should only be allowed to retrieve their own game data
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PLAYER"))) {
            if (!authenticatedUserId.equals(requestedUserId)) {
                throw new AccessDeniedException("Players can only access their own game data.");
            }
        }

        // For admins, no additional check is necessary if they are allowed to retrieve any game data
        return gameService.getGame(requestedUserId);
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

    @PostMapping("/finishGame")
    public void finishGame(@RequestBody Map<String, Object> requestBody) {
        // Extract userID from requestBody
        UUID userID = UUID.fromString((String) requestBody.get("userID"));

        // Extract the words array
        JSONArray wordsList = new JSONArray((List<?>) requestBody.get("wordsList"));

        // Call the service with the extracted userID and wordsList
        gameService.finishGame(wordsList, userID);
    }






}



