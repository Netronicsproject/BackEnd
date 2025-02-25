package hello.netro.controller;

import hello.netro.auth.LoginUser;
import hello.netro.domain.Fileitem;
import hello.netro.dto.*;
import hello.netro.domain.User;
import hello.netro.service.CommentService;
import hello.netro.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Tag(name="게시물  API", description = "게시물 업로드,수정,삭제,파일첨부등등  , 게시물의 파일은 게시물의 이미지파일과 첨부파일로 나뉨")
public class PostController {

    @Value("${file.dir}")
    private String uploadPath ;
    @Value("${file.baseurl}")
    private String baseUrl;
    public final PostService postService;
    public final CommentService commentService;

    // 게시글 생성
    @Operation(summary = "게시물 만들기 api",description = "첨부파일을 멀티파트 형식으로 인자에 포함")
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
    @Operation(summary = "게시물 조회 api",description = "페이징을 사용,페이징 번호,사이즈,정렬조건 쿼리 스트링으로 받음 , 페이지 정보도 반환")
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
    //특정 게시물 클릭 이후 상세 정보
    @Operation(summary = "게시물 자세히 보기 api",description = "게시물리스트에서 하나 클릭이후 게시물 상세정보" )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId) {
        PostResponseDto postById = postService.getPostById(postId);

        if (postById == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found 반환
        }

        return ResponseEntity.ok(postById); // 200 OK + Post 데이터 반환
    }



    // 게시글 수정
    @Operation(summary = "게시물 수정 api",description = "필요에 따라 첨부파일도 수정가능 ,filetype이 image인게 본문 첨가되는것 " )
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
    @Operation(summary = "첨부파일 다운  api",description = "첨부파일을 포스트id와 파일 id기반으로 다운 ,파일 id를 프론트에선 가지고있어야함")
    @GetMapping("{postId}/attachments/{fileId}")
    public ResponseEntity<Resource> downloadAttach(
            @PathVariable Long postId,
            @PathVariable Long fileId
    ) throws MalformedURLException {
        Fileitem attachFile = postService.getAttachFile(postId, fileId);
        UrlResource resource = new UrlResource("file:" + uploadPath + attachFile.getFilePath());
        String originalFilename = attachFile.getFileName();
        String encodedFileName = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    // 첨부파일 목록 조회
    @Operation(summary = "첨부파일 조회 api",description = "현재 게시물 id 기반으로 파일정보를 조회 ")
    @GetMapping("/{postId}/attachments")
    public ResponseEntity<List<FileResponseDto>> getAttachments(@PathVariable Long postId) {
        List<FileResponseDto> attachments = postService.getAttachFiles(postId);
        return ResponseEntity.ok(attachments);
    }

    // 이미지 파일 목록 조회
    @Operation(summary = "이미지 파일 조회 api",description = "게시물 이미지 파일을 게시물 id기반 조회  ")
    @GetMapping("/{postId}/images")
    public ResponseEntity<List<FileResponseDto>> getImages(@PathVariable Long postId) {
        List<FileResponseDto> images = postService.getImageFiles(postId);
        return ResponseEntity.ok(images);
    }

    // 게시글에 댓글 작성
    @Operation(summary = "게시물에 댓글달기 api",description = "댓글 달기,parent ID는 null로보냄  ")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestBody CommentRequestDto postCommentRequestDto,
            @Parameter(hidden = true) @LoginUser User user
            ) {
        Long postId = postCommentRequestDto.getPostId();
        String content =postCommentRequestDto.getContent();
        CommentResponseDto commentDto = commentService.createComment(postId, user.getId(), content);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    // 대댓글 작성
    @Operation(summary = "댓글에 대댓글달기 api",description = "댓글에 대댓글달기 api")
    @PostMapping("/{postId}/comments/{parentCommentId}")
    public ResponseEntity<CommentResponseDto> addReply(
           @RequestBody  CommentRequestDto commentRequestDto,
           @Parameter(hidden = true) @LoginUser User user
            ) {
        Long postId = commentRequestDto.getPostId();
        Long userId = user.getId();
        Long parentCommentId = commentRequestDto.getParentId();
        String content = commentRequestDto.getContent();
        CommentResponseDto replyDto = commentService.createReply(postId, userId, content, parentCommentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyDto);
    }

    // 특정 게시글의 댓글 목록 조회
    @Operation(summary = "게시물 댓글목록 조회 api")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @Operation(summary = "게시물 댓글 삭제 api",description = "부모 댓글 삭제하면 자식 댓글도 사라짐 ")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
