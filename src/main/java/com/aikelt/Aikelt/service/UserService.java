package com.aikelt.Aikelt.service;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.repository.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final JdbcUserRepository userRepository;

    @Autowired
    public UserService(JdbcUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Existing methods remain unchanged
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    public void saveUser(User user) {
        userRepository.saveUser(user);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("Attempting to load user by username: " + username);

        Optional<User> optionalUser = getUserByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_PLAYER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
