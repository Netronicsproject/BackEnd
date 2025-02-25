package hello.netro.service;

import hello.netro.domain.*;
import hello.netro.dto.FileResponseDto;
import hello.netro.dto.PostRequestDto;
import hello.netro.dto.PostResponseDto;
import hello.netro.dto.PostSummaryDto;
import hello.netro.repository.FileRepository;
import hello.netro.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    // 예: 로컬 디렉토리 경로
    @Value("${file.dir}")
    private String uploadPath ;
    @Value("${file.baseurl}")
    private String baseUrl;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;


    //특정 사용자의 게시물 조회
    public Page<PostResponseDto> getPostByUserId(Pageable pageable,Long userId)
    {
        return postRepository.findByUserId(userId,pageable).map(post->post.toDto(baseUrl));
    }
    //특정 id의 게시물 조회

    public PostResponseDto getPostById(Long id) {
        return postRepository.findById(id).map(post->post.toDto(baseUrl)).orElse(null);
    }

    // 모든 게시글 조회 (페이징 적용) - 목록 조회는 PostSummaryDto 사용
    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getUser().getName())
                        .commentCount(post.getComments().size()) // 댓글 개수
                        .likeCount(post.getLikes().size()) // 좋아요 개수
                        .build());
    }
    // 게시글 생성
    public PostResponseDto createPost(PostRequestDto postRequestDto, List<MultipartFile> multipartFiles, User user) {
        // 1. 게시글 엔티티 생성 및 내용 설정
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setUser(user);

        // 2. 게시글 저장
        postRepository.save(post);

        // 3. 파일이 존재하면 저장 로직 호출
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            saveFile(multipartFiles, post, postRequestDto.getFileType());
        }

        return post.toDto(baseUrl);
    }

    // 게시글 수정
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, List<MultipartFile> multipartFiles, User user) {
        // 1. 기존 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시물이 존재하지 않습니다. postId=" + postId));

        // 2. 수정 권한 검증 (작성자 확인)
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 3. 게시글 내용 수정
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        postRepository.save(post);

        // 4. 파일이 존재하면 추가 저장 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            saveFile(multipartFiles, post, postRequestDto.getFileType());
        }

        return post.toDto(baseUrl);
    }

    // 파일 저장 메서드 (수정 후)
    public void saveFile(List<MultipartFile> multipartFiles, Post post, FileType fileType) {
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                String originalFilename = multipartFile.getOriginalFilename();
                String storeFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                // fullPath는 실제 저장되는 경로
                String fullPath = uploadPath + storeFileName;
                try {
                    multipartFile.transferTo(new File(fullPath));
                } catch (IOException e) {
                    log.error("파일 저장 실패: {}", fullPath, e);
                    throw new RuntimeException("파일 저장에 실패하였습니다.");
                }
                // 여기서는 DB에는 storeFileName(즉, 상대 경로)를 저장하도록 함
                Fileitem fileEntity = new Fileitem();
                fileEntity.setPost(post);
                fileEntity.setFilePath(storeFileName); // fullPath 대신 storeFileName 저장
                fileEntity.setFileName(originalFilename);
                fileEntity.setFileType(fileType);
                fileRepository.save(fileEntity);
            }
        }
    }

    // 파일 다운로드, 첨부파일/이미지 목록 조회, 단일 파일 조회 메서드
    public List<FileResponseDto> getImageFiles(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시물이 없습니다. postId=" + postId));
        return post.getFileitems().stream()
                .filter(file -> file.getFileType() == FileType.IMAGE)
                .map(file-> new FileResponseDto(file,baseUrl))
                .collect(Collectors.toList());
    }

    public List<FileResponseDto> getAttachFiles(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시물이 없습니다. postId=" + postId));
        return post.getFileitems().stream()
                .filter(file -> file.getFileType() == FileType.ATTACH)
                .map(file-> new FileResponseDto(file,baseUrl))
                .collect(Collectors.toList());
    }

    public Fileitem getAttachFile(Long postId, Long fileId) {
        Fileitem fileitem = fileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("해당 파일이 존재하지 않습니다. fileId=" + fileId));

        if (!fileitem.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("이 파일은 해당 게시글에 속해 있지 않습니다. postId=" + postId);
        }
        if (fileitem.getFileType() != FileType.ATTACH) {
            throw new IllegalArgumentException("이 파일은 첨부파일이 아닙니다.");
        }

        return fileitem;
    }
}
