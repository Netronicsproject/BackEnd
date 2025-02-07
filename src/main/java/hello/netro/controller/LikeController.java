package hello.netro.controller;


import hello.netro.auth.LoginUser;
import hello.netro.domain.User;
import hello.netro.dto.LikeResponseDto;
import hello.netro.service.LikeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/")
    public ResponseEntity<List<LikeResponseDto>> likeList(@Parameter(hidden = true)@LoginUser User loginUser) {
        List<LikeResponseDto> posts = likeService.loadLikeAllByUserId(loginUser.getId());

        return ResponseEntity.status(200).body(posts);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<LikeResponseDto> addLike(@PathVariable("postId") Long postId,@Parameter(hidden = true) @LoginUser User loginUser) {
        LikeResponseDto dto = likeService.addLike(loginUser.getId(), postId);
        return ResponseEntity.status(200).body(dto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteLike(@PathVariable("postId") Long postId,@Parameter(hidden = true) @LoginUser User loginUser) {
        likeService.deleteLike(loginUser.getId(), postId);
        return ResponseEntity.status(200).build();
    }
    
}
