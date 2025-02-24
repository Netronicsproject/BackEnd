package hello.netro.controller;


import hello.netro.auth.LoginUser;
import hello.netro.domain.User;
import hello.netro.dto.LikeResponseDto;
import hello.netro.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name="좋아요 관련 API", description = "좋아요 추가,삭제,확인")
public class LikeController {
    private final LikeService likeService;

    @Operation(summary = "좋아요 누른 게시물 목록")
    @GetMapping("/")
    public ResponseEntity<List<LikeResponseDto>> likeList(@Parameter(hidden = true)@LoginUser User loginUser) {
        List<LikeResponseDto> posts = likeService.loadLikeAllByUserId(loginUser.getId());

        return ResponseEntity.status(200).body(posts);
    }
    @Operation(summary = "좋아요 누르기",description = "게시물 id기반 좋아요")
    @PostMapping("/{postId}")
    public ResponseEntity<LikeResponseDto> addLike(@PathVariable("postId") Long postId,@Parameter(hidden = true) @LoginUser User loginUser) {
        LikeResponseDto dto = likeService.addLike(loginUser.getId(), postId);
        return ResponseEntity.status(200).body(dto);
    }
    @Operation(summary = "좋아요 삭제",description = "게시물 id기반 좋아요 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteLike(@PathVariable("postId") Long postId,@Parameter(hidden = true) @LoginUser User loginUser) {
        likeService.deleteLike(loginUser.getId(), postId);
        return ResponseEntity.status(200).build();
    }
    
}
