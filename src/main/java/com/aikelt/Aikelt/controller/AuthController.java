package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, Object> requestBody) {
        // Extract username and password from the request body
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");

        try {

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Set the authentication in the context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve the principal (user details) from the authentication object
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // You can now access the user details (such as username, authorities, etc.)
            String jwtToken = jwtService.generateToken(userDetails);

            // Return the token as the response
            return ResponseEntity.ok(Map.of("token", jwtToken));

        } catch (Exception e) {
            // Handle authentication failure
            return ResponseEntity.status(401).body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
}