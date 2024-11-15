package com.aikelt.Aikelt.repository.mappers;

import com.aikelt.Aikelt.model.Role;
import com.aikelt.Aikelt.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        // Map the basic fields
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setTokensLeft(rs.getInt("tokens_left"));

        // Retrieve the array and cast it to Object[]
        Object[] authoritiesObjArray = (Object[]) rs.getArray("authorities").getArray();

        // Map each role name from the database to the Role enum constants
        Set<Role> authorities = Arrays.stream(authoritiesObjArray)
                .map(Object::toString)  // Convert each Object to String
                .map(roleName -> {
                    switch (roleName) {
                        case "ADMIN": return Role.ROLE_ADMIN;
                        case "PLAYER": return Role.ROLE_USER;
                        default: throw new IllegalArgumentException("Unknown role: " + roleName);
                    }
                })
                .collect(Collectors.toSet());

        user.setAuthorities(authorities);

        return user;
    }
}
