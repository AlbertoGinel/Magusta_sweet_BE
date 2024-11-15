package com.aikelt.Aikelt.service;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.repository.JdbcUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcUserRepository userRepository;

    public CustomUserDetailsService(JdbcUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public Optional<UUID> findIDByUsername(String username) {
        return userRepository.findIDByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the repository
        Optional<User> optionalUser = userRepository.findUserByUsername(username);

        // Handle case where user is not found
        User user = optionalUser.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        // Return a Spring Security UserDetails object with the user's data and authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()  // Directly passing authorities here
        );
    }
}