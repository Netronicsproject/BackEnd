package hello.netro.controller;

import hello.netro.auth.LoginUser;
import hello.netro.dto.*;
import hello.netro.domain.User;
import hello.netro.service.CommentService;
import hello.netro.service.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.core.io.Resource;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/post")

public class PostController {
    public final PostService postService;
    public final CommentService commentService;
    // 게시글 생성
    @PostMapping("")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto postRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
            @Parameter(hidden = true)@LoginUser User user
    ) {
        PostResponseDto postResponseDto = postService.createPost(postRequestDto, multipartFiles, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }
    // 모든 게시글 조회 (페이징 적용)
    //GET http://localhost:8080/api/post?page=0&size=10&sort=createdDate,desc
    @GetMapping("")
    public ResponseEntity<PostSummaryPageDto> getAllPosts(Pageable pageable) {
        Page<PostSummaryDto> posts = postService.getAllPosts(pageable);

        PostSummaryPageDto response = new PostSummaryPageDto(
                posts.getContent(),
                posts.getTotalPages(),
                posts.getTotalElements(),
                posts.isLast(),
                posts.isFirst()
        );

        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestPart("post") PostRequestDto postRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
            @Parameter(hidden = true)@LoginUser User user
    ) {
        PostResponseDto postResponseDto = postService.updatePost(postId, postRequestDto, multipartFiles, user);
        return ResponseEntity.ok(postResponseDto);
    }

    // 파일 다운로드
    @GetMapping("{postId}/attachments/{fileId}")
    public ResponseEntity<Resource> downloadAttach(
            @PathVariable Long postId,
            @PathVariable Long fileId
    ) throws MalformedURLException {
        FileResponseDto attachFile = postService.getAttachFile(postId, fileId);
        UrlResource resource = new UrlResource("file:" + attachFile.getFilePath());
        String originalFilename = attachFile.getFileName();
        String encodedFileName = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    // 첨부파일 목록 조회
    @GetMapping("/{postId}/attachments")
    public ResponseEntity<List<FileResponseDto>> getAttachments(@PathVariable Long postId) {
        List<FileResponseDto> attachments = postService.getAttachFiles(postId);
        return ResponseEntity.ok(attachments);
    }

    // 이미지 파일 목록 조회
    @GetMapping("/{postId}/images")
    public ResponseEntity<List<FileResponseDto>> getImages(@PathVariable Long postId) {
        List<FileResponseDto> images = postService.getImageFiles(postId);
        return ResponseEntity.ok(images);
    }

    // 게시글에 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam String content) {
        CommentResponseDto commentDto = commentService.createComment(postId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    // 대댓글 작성
    @PostMapping("/{postId}/comments/{parentCommentId}")
    public ResponseEntity<CommentResponseDto> addReply(
            @PathVariable Long postId,
            @PathVariable Long parentCommentId,
            @RequestParam Long userId,
            @RequestParam String content) {
        CommentResponseDto replyDto = commentService.createReply(postId, userId, content, parentCommentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyDto);
    }

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
