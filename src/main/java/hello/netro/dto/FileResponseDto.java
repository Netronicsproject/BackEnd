package hello.netro.dto;

import hello.netro.domain.FileType;
import hello.netro.domain.Fileitem;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {
    private Long fileId;
    private String fileUrl;   // 클라이언트가 접근할 수 있는 공개 URL
    private String fileName;  // 사용자가 저장한 논리적 파일명
    @Enumerated(EnumType.STRING)
    private FileType fileType;
    // 기존 생성자 대신 공개 URL을 만드는 생성자 추가
    public FileResponseDto(Fileitem fileitem, String baseUrl) {
        this.fileId = fileitem.getId();
        // fileitem.getFilePath()는 저장된 상대 경로(예, "UUID_filename")
        this.fileType = fileitem.getFileType();
        this.fileUrl = baseUrl + fileitem.getFilePath();
    }
}
