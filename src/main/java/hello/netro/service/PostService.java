package hello.netro.service;

import hello.netro.domain.FileType;
import hello.netro.domain.Fileitem;
import hello.netro.domain.Post;
import hello.netro.domain.User;
import hello.netro.dto.FileResponseDto;
import hello.netro.dto.PostRequestDto;
import hello.netro.dto.PostResponseDto;
import hello.netro.repository.FileRepository;
import hello.netro.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.webresources.FileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    // 예: 로컬 디렉토리 경로
    @Value("${file.dir}")
    private String uploadPath ;
    private PostRepository postRepository;
    private FileRepository fileRepository;

    public PostResponseDto createPost(PostRequestDto postRequestDto, List<MultipartFile> multipartFiles, User user) {
        // 1. Post 엔티티 생성 및 저장
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
         post.setUser(user);
        savefile(multipartFiles,post,postRequestDto.getFileType());
        postRepository.save(post);
        return post.PostToDto();
    }
    public void savefile(List<MultipartFile> multipartFiles,Post post,FileType fileType)
    {
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                // 실제 디스크에 저장할 파일명(중복방지를 위해 UUID 사용 예시)
                String originalFilename = multipartFile.getOriginalFilename();
                //db에 저장되는 파일 이름 및 경로
                String storeFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                // 저장할 경로
                String fullPath = uploadPath + storeFileName;
                try {
                    // 파일을 로컬 디스크에 저장
                    multipartFile.transferTo(new File(fullPath));
                } catch (IOException e) {
                    // 예외 처리
                    e.printStackTrace();
                }
                // 3. Fileitem 엔티티 생성 및 매핑
                Fileitem fileEntity = new Fileitem();
                fileEntity.setPost(post);
                fileEntity.setFilePath(fullPath);
                fileEntity.setFileName(originalFilename);
                fileEntity.setFileType(fileType);
                // 4. DB에 저장
                fileRepository.save(fileEntity);
            }
        }
    }
    // 특정 Post에 속한 첨부파일(ATTACH)만 조회하는 예시
    public List<FileResponseDto> getAttachFiles(Long postId) {
        // 해당 Post를 먼저 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시물이 없습니다. " + postId));
        // Post가 가진 모든 Fileitem 중에서 type이 ATTACH인 것만 필터
        return post.getFileitems().stream()
                .filter(file -> file.getFileType() == FileType.ATTACH)
                .map(FileResponseDto::new) // DTO로 변환
                .collect(Collectors.toList());
    }

    // 특정 파일 하나만 조회하는 메서드 (다운로드 용)
    public FileResponseDto getAttachFile(Long postId, Long fileId) {
        // 1. fileId로 Fileitem 조회
        Fileitem fileitem = fileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("해당 파일이 존재하지 않습니다. fileId=" + fileId));

        // 2. 정말 postId에 속한 파일인지 검증 (보안상 필요)
        if (!fileitem.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("이 파일은 해당 게시글에 속해 있지 않습니다. postId=" + postId);
        }

        // 3. FileType이 ATTACH인지 확인 (이미지인지 첨부파일인지 구분)
        if (fileitem.getFileType() != FileType.ATTACH) {
            throw new IllegalArgumentException("이 파일은 첨부파일이 아닙니다.");
        }

        return fileitem.fileToDto();
    }
}
