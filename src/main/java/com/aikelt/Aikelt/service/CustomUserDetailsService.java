package com.aikelt.Aikelt.service;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.repository.JdbcUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcUserRepository userRepository;

    public CustomUserDetailsService(JdbcUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Query the user from the database as Optional
        Optional<User> optionalUser = userRepository.findUserByUsername(username);

        // If the user is not found, throw UsernameNotFoundException
        User user = optionalUser.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        // Convert Role to GrantedAuthority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // Return UserDetails with authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}