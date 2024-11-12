package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.security.JwtTokenProvider;
import com.aikelt.Aikelt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;



import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User is disabled");
        }

        try {
            // Yellow color for the authentication attempt message
            System.out.println("\u001B[33mAttempting to authenticate user: " + username + "\u001B[0m");

            // Authenticate the user using the username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Green color for successful authentication
            System.out.println("\u001B[32mAuthentication successful for user: " + username + "\u001B[0m");

            // Continue with token generation and response preparation
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String token = jwtTokenProvider.createToken(username, roles);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("roles", roles);
            response.put("tokensLeft", user.getTokensLeft());

            return response;

        } catch (Exception e) {
            // Red color for authentication failure messages
            System.err.println("\u001B[31mAuthentication failed for user: " + username + "\u001B[0m");
            System.err.println("\u001B[31mError: " + e.getMessage() + "\u001B[0m");
            throw new RuntimeException("Authentication failed", e);
        }
    }
}