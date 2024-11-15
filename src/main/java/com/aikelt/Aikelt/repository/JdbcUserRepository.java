package com.aikelt.Aikelt.repository;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.repository.mappers.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    // Get all users from the database
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    // Find a user by username
    public Optional<User> findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.stream().findFirst();
    }

    public Optional<UUID> findIDByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        List<UUID> ids = jdbcTemplate.queryForList(sql, UUID.class, username);
        return ids.stream().findFirst();
    }

    // Find a user by their UUID
    public Optional<User> findUserById(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.stream().findFirst();
    }

    // Insert a new user into the database
    public int saveUser(User user) {
        String sql = "INSERT INTO users (id, username, password, authorities, enabled, tokens_left) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        // Map roles as a String array
        String[] authoritiesArray = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Use getAuthority() instead of name()
                .toArray(String[]::new);


        return jdbcTemplate.update(sql,
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authoritiesArray,
                user.isEnabled(),
                user.getTokensLeft());
    }

    // Update an existing user
    public int updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, authorities = ?, " +
                "enabled = ?, tokens_left = ? WHERE id = ?";
        // Map roles as a String array
        String[] authoritiesArray = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Use getAuthority() instead of Enum::name
                .toArray(String[]::new);


        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                authoritiesArray,
                user.isEnabled(),
                user.getTokensLeft(),
                user.getId());
    }

    // Delete a user by UUID
    public int deleteUser(UUID userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, userId);
    }
}
