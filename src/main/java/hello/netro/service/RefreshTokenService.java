package hello.netro.service;

import hello.netro.domain.RefreshToken;
import hello.netro.domain.User;
import hello.netro.exception.TokenRefreshException;
import hello.netro.repository.RefreshTokenRepository;
import hello.netro.repository.UserRepository;
import hello.netro.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.RefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Refresh Token 생성 (기존 토큰이 있으면 갱신)
     */
    public RefreshToken createRefreshToken(User user) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(refreshToken);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh Token 검증 후 새로운 Access Token 발급
     */
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "유효하지 않은 리프레시 토큰입니다."));

        // 리프레시 토큰이 만료되었는지 확인
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "리프레시 토큰이 만료되었습니다. 다시 로그인하세요.");
        }

        // 유저 정보 조회 후 새로운 Access Token 발급
        User user = token.getUser();
        return jwtUtil.generateJwtToken(user.getEmail());
    }

    /**
     * 특정 사용자 ID의 Refresh Token 삭제 (로그아웃 시 사용)
     */
    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        refreshTokenRepository.deleteByUser(user);
        return 1;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "리프레시 토큰이 만료되었습니다.");
        }
        return token;
    }
}