package hello.netro.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import hello.netro.dto.AuthResponse;
import hello.netro.exception.InvalidTokenException;
import hello.netro.repository.UserRepository;
import hello.netro.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import hello.netro.domain.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier verifier;
    private final UserRepository userRepository;

    public AuthResponse authenticate(String idTokenString) throws GeneralSecurityException, IOException, IOException {

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new InvalidTokenException("유효하지 않은 구글 토큰");
        }

        // 구글 프로필에서 필요한 정보 추출
        String email = idToken.getPayload().getEmail();
        String name = (String) idToken.getPayload().get("name");

        if (email == null) {
            throw new InvalidTokenException("구글 토큰에 이메일 정보가 없음");
        }

        // 사용자 조회 or 신규 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name));

        // JWT 토큰 생성
        String token = jwtUtil.generateJwtToken(email);
        return new AuthResponse(token);
    }

    private User createNewUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return userRepository.save(user);
    }

}

