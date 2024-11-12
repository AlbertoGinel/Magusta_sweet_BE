package com.aikelt.Aikelt.repository;

import com.aikelt.Aikelt.model.User;
import com.aikelt.Aikelt.repository.mappers.UserRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserRepository {

    private final JdbcClient jdbcClient;
    private final UserRowMapper userRowMapper;

    public JdbcUserRepository(JdbcClient jdbcClient, UserRowMapper userRowMapper) {
        this.jdbcClient = jdbcClient;
        this.userRowMapper = userRowMapper;
    }

    // Get all users from the database
    public List<User> findAllUsers() {
        return jdbcClient.sql("SELECT * FROM users")
                .query(userRowMapper)
                .list();
    }

    // Find a user by username
    public Optional<User> findUserByUsername(String username) {
        return jdbcClient.sql("SELECT * FROM users WHERE username = :username")
                .param("username", username)
                .query(userRowMapper)
                .optional();
    }

    // Find a user by their UUID
    public Optional<User> findUserById(UUID userId) {
        return jdbcClient.sql("SELECT * FROM users WHERE id = :userId")
                .param("userId", userId)
                .query(userRowMapper)
                .optional();
    }

    // Insert a new user into the database
    public int saveUser(User user) {
        return jdbcClient.sql("INSERT INTO users (id, username, password, role, enabled, tokensLeft) " +
                        "VALUES (:id, :username, :password, :role, :enabled, :tokensLeft)")
                .param("id", user.getId())
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("role", user.getRole().name()) // Storing role as a string
                .param("enabled", user.isEnabled())
                .param("tokensLeft", user.getTokensLeft())
                .update();
    }

    // Update an existing user
    public int updateUser(User user) {
        return jdbcClient.sql("UPDATE users SET username = :username, password = :password, role = :role, " +
                        "enabled = :enabled, tokensLeft = :tokensLeft WHERE id = :id")
                .param("id", user.getId())
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("role", user.getRole().name())
                .param("enabled", user.isEnabled())
                .param("tokensLeft", user.getTokensLeft())
                .update();
    }

    // Delete a user by UUID
    public int deleteUser(UUID userId) {
        return jdbcClient.sql("DELETE FROM users WHERE id = :userId")
                .param("userId", userId)
                .update();
    }
}
