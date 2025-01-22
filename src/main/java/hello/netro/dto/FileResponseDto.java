package hello.netro.dto;

import hello.netro.domain.Fileitem;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {
    private Long fileId;
    private String filePath;
    private String fileName;

    public FileResponseDto(Fileitem fileitem) {
        this.fileId = fileitem.getId();
        this.filePath = fileitem.getFilePath();
        this.fileName = fileitem.getFileName();
    }
}
