package hello.netro.controller;

import hello.netro.dto.AuthResponse;
import hello.netro.exception.InvalidTokenException;
import hello.netro.exception.TokenRefreshException;
import hello.netro.service.LoginService;
import hello.netro.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="로그인 API", description = "구글 oauth 로그인.")
public class AuthController {

    private final LoginService loginService; // 새로운 서비스 계층
    private final RefreshTokenService refreshTokenService;
    @Operation(summary = "ID 토큰 기반 로그인", description = "바디로 Google ID 토큰을 받음")
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

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 API
     */
    @Operation(summary = "리프레시 토큰을 이용한 새로운 액세스 토큰 발급", description = "리프레시 토큰을 받아 새로운 액세스 토큰을 반환")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String newAccessToken = refreshTokenService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshTokenRequest.getRefreshToken()));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않습니다.");
        }
    }

    // DTO
    @Data
    static class GoogleTokenRequest {
        private String idToken;
    }

    @Data
    static class RefreshTokenRequest {
        private String refreshToken;
    }
}
