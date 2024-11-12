package com.aikelt.Aikelt.repository.mappers;

import com.aikelt.Aikelt.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id"))); // Convert UUID from the DB string
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.Role.valueOf(rs.getString("role"))); // Convert String to Role enum
        user.setEnabled(rs.getBoolean("enabled"));
        user.setTokensLeft(rs.getInt("tokensLeft"));
        return user;
    }
}
