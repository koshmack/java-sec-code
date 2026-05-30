package org.joychou.security;

import java.sql.*;
import java.util.Optional;

public class MkSqlInjection {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/appdb";
    private final String dbUser = "app";
    private final String dbPass = "secret";

    public Optional<String> findRoleByUsername(String username) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("role"));
                }
                return Optional.empty();
            }
        }
    }
}