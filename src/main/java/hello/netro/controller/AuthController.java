package hello.netro.controller;

import hello.netro.dto.AuthResponse;
import hello.netro.exception.InvalidTokenException;
import hello.netro.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final LoginService loginService; // 새로운 서비스 계층

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleTokenRequest googleTokenRequest) {
        try {
            AuthResponse authResponse = loginService.authenticate(googleTokenRequest.getIdToken());
            return ResponseEntity.ok(authResponse);
        } catch (InvalidTokenException e) {
            log.error("유효하지 않은 토큰", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 ID 토큰입니다.");
        } catch (GeneralSecurityException | IOException e) {
            log.error("구글 토큰 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류.");
        }
    }

    // DTO들
    @Data
    static class GoogleTokenRequest {
        private String idToken;
    }
}
