package wisoft.authservice.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wisoft.authservice.User;

@Repository
@RequiredArgsConstructor
public class JdbcAuthRepository implements AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<User> findByUsername(final String username) {
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("user_name"));
                u.setPassword(rs.getString("password"));
                return u;
            });
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(final User newUser) {
        String sql = "INSERT INTO users (user_name, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, newUser.getUsername(), newUser.getPassword());

    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}
