package hello.netro.controller;


import hello.netro.auth.LoginUser;
import hello.netro.domain.User;
import hello.netro.dto.UserProfileDto;
import hello.netro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name="프로필 API", description = "프로필 만들기, 로그인되어 있는 사용자에 대해 카테고리 정보랑 성별 입력시켜줌 ")
public class ProfileController { //로그인되어 있는 사용자에 대해 카테고리 정보랑 성별 입력시켜줌

    private final UserService userService;

    @Operation(summary = "프로필 완성 api",description = "프로필 사진도 첨부 가능")
    @PostMapping("/new")
    public ResponseEntity<?> completeUserProfile(
            @RequestPart MultipartFile profilePicture,
            @RequestBody UserProfileDto userProfileDTO,
            @Parameter(hidden = true) @LoginUser User user) {

        log.info("User details: {}", user);
        log.info("User email: {}", user.getEmail());
        try {
            // 실제 업데이트 로직
        } catch (Exception e) {
            log.error("Error occurred while updating user profile", e);
            throw e; // 예외를 다시 던져 컨트롤러에서 처리
        }
        try {
            userService.updateUserProfile(user, userProfileDTO);
            return ResponseEntity.ok().body(new SuccessResponse("프로필이 성공적으로 업데이트되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("프로필 업데이트 실패", e.getMessage()));
        }
    }
    // 응답용 DTO 클래스들
    @Data
    @AllArgsConstructor
    static class SuccessResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String error;
        private Object details;
    }
}
