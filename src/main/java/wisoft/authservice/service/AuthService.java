package wisoft.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wisoft.authservice.User;
import wisoft.authservice.repository.JdbcAuthRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JdbcAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean authenticate(final String username, final String password) {

        return authRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .isPresent();
    }

    public User getUser(final String username) {
        return authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("관리자 계정을 찾을 수 없습니다."));
    }

    public void signUp(final String username, final String password) {
        if (authRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);

        authRepository.save(newUser);
    }
}
