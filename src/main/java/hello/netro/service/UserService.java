package hello.netro.service;

import hello.netro.auth.LoginUser;
import hello.netro.domain.FileType;
import hello.netro.domain.Fileitem;
import hello.netro.domain.Post;
import hello.netro.domain.User;
import hello.netro.dto.UserProfileDto;
import hello.netro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public void updateUserProfile(User user, UserProfileDto userProfileDto)
    {
        user.updateProfile(userProfileDto);
    }

    // 프포필 파일 저장 메서드 (중복 방지를 위해 UUID를 파일명에 추가)
    public String save(List<MultipartFile> multipartFiles, Post post, FileType fileType) {
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                // 원본 파일명 획득
                String originalFilename = multipartFile.getOriginalFilename();
                // UUID를 이용해 중복 방지를 위한 저장용 파일명 생성
                String storeFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                // 저장할 전체 경로 생성
                String fullPath = uploadPath + storeFileName;
                try {
                    // 파일을 로컬 디스크에 저장
                    multipartFile.transferTo(new File(fullPath));
                } catch (IOException e) {
                    log.error("파일 저장 실패: {}", fullPath, e);
                    throw new RuntimeException("파일 저장에 실패하였습니다.");
                }
                // Fileitem 엔티티 생성 및 매핑
                Fileitem fileEntity = new Fileitem();
                fileEntity.setPost(post);
                fileEntity.setFilePath(fullPath);
                fileEntity.setFileName(originalFilename);
                fileEntity.setFileType(fileType);
                // DB에 저장
                fileRepository.save(fileEntity);
            }
        }
    }


}
