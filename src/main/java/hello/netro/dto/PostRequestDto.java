package hello.netro.dto;

import hello.netro.domain.FileType;
import lombok.*;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private FileType fileType;
    private List<String> filePaths;  // 업로드할 파일 경로 리스트
}
