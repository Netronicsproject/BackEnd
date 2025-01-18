package hello.netro.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileRequestDto {
    private Long postId;
    private String filePath;
    private String fileName;
}
