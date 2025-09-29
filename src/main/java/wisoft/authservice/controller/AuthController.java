package wisoft.authservice.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import wisoft.authservice.dto.LoginRequest;
import wisoft.authservice.dto.SignUpRequest;
import wisoft.authservice.dto.TokenResponse;
import wisoft.authservice.security.JwtTokenProvider;
import wisoft.authservice.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank() || request.getPassword() == null ||  request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (!authService.authenticate(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtTokenProvider.createToken(request.getUsername());
        return ResponseEntity.ok(
                TokenResponse.builder()
                        .accessToken(token)
                        .build()
        );
    }

    @PostMapping("/extend")
    public ResponseEntity<TokenResponse> extend(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        try {
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                String newToken = jwtTokenProvider.createToken(username);
                return ResponseEntity.ok(TokenResponse.builder().accessToken(newToken).build());
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse.builder().message("토큰이 만료되었습니다").build());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse.builder().message("유효하지 않은 토큰입니다").build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/validate")
    public ResponseEntity<TokenResponse> validate(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        try {
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(TokenResponse.builder().message("유효한 토큰입니다").build());
            }
        } catch(ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse.builder().message("토큰이 만료되었습니다").build());
        } catch(JwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse.builder().message("유효하지 않은 토큰입니다").build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(TokenResponse.builder().message("토큰 검증에 실패했습니다").build());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        try {
            authService.signUp(request.getUsername(), request.getPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
