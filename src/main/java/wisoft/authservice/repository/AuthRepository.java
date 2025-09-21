package wisoft.authservice.repository;

import java.util.Optional;
import wisoft.authservice.User;

public interface AuthRepository {
    Optional<User> findByUsername(final String username);
}
