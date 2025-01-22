package hello.netro.controller;

import hello.netro.auth.LoginUser;
import hello.netro.domain.Fileitem;
import hello.netro.domain.User;
import hello.netro.dto.FileResponseDto;
import hello.netro.dto.PostRequestDto;
import hello.netro.dto.PostResponseDto;
import hello.netro.service.CommentService;
import hello.netro.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestBody PostRequestDto postRequestDto,
            @RequestParam("files") List<MultipartFile> multipartFiles,
            @LoginUser User user
    )
    {

        PostResponseDto post = postService.createPost(postRequestDto, multipartFiles, user);
        return  ResponseEntity.status(200).body(post);
    }

    @GetMapping("/posts/{postId}/attachments/{fileId}")
    public ResponseEntity<Resource> downloadAttach(
            @PathVariable Long postId,
            @PathVariable Long fileId
    ) throws MalformedURLException {

        // 1. 파일 엔티티 조회 (해당 게시글의 첨부파일인지 확인)
        FileResponseDto attachFile = postService.getAttachFile(postId, fileId);

        // 2. UrlResource 생성 (실제 파일 경로)
        UrlResource resource = new UrlResource("file:" + attachFile.getFilePath());

        // 3. 파일명 인코딩
        String originalFilename = attachFile.getFileName();
        String encodedFileName = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);

        // 4. Content-Disposition 헤더 설정
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        // 5.ResponseEntity로 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
