package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class PostRequestDto {
    private String title;
    private String content;
    private List<String> filePaths;  // 업로드할 파일 경로 리스트
}
