package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;

    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/getAllUsers")
    List<User> findAllDictionaryWords() {
        return customUserDetailsService.findAllUsers();
    }

    @PostMapping("/loadUserByUsername")
    UserDetails loadUserByUsername(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        return customUserDetailsService.loadUserByUsername(username);
    }
}