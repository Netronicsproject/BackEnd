package hello.netro.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {
    private Long fileId;
    private String filePath;
    private String fileName;
}
