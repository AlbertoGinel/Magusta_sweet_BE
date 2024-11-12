package com.aikelt.Aikelt.controller;

import com.aikelt.Aikelt.model.DictionaryWord;
import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.service.CustomUserDetailsService;
import com.aikelt.Aikelt.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    public UserController(UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/getAllUsers")
    List<User> findAllDictionaryWords() {
        return userService.getAllUsers();
    }

    @PostMapping("/loadUserByUsername")
    UserDetails loadUserByUsername(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        return customUserDetailsService.loadUserByUsername(username);
    }
}