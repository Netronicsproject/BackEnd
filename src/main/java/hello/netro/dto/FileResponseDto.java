package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class FileResponseDto {
    private Long fileId;
    private String filePath;
    private String fileName;
}
