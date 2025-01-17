package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class FileRequestDto {
    private Long postId;
    private String filePath;
    private String fileName;
}
