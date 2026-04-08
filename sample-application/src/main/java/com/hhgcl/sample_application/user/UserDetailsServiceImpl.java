// sample-application/src/main/java/com/hhgcl/sample_application/user/UserDetailsServiceImpl.java
package com.hhgcl.sample_application.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        
        List<User> results = jdbc.query(
            """
            SELECT u.id, u.username, u.password, r.role
            FROM users u
            JOIN user_roles r ON u.id = r.user_id
            WHERE u.username = ?
            """,
            (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                List.of(rs.getString("role"))
            ),
            username
        );

        if (results.isEmpty()) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        log.info("Found user: {} with {} roles", username, results.size());
        
        User first = results.get(0);
        List<SimpleGrantedAuthority> authorities = results.stream()
                .flatMap(u -> u.roles().stream())
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new AppUserDetails(first.id(), first.username(), first.password(), authorities);
    }
}