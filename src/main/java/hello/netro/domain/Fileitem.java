package hello.netro.domain;

import hello.netro.dto.FileResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Fileitem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    private FileType fileType; // 예: IMAGE, ATTACHMENT, VIDEO 등
    // getters and setters

    public FileResponseDto fileToDto()
    {
        return FileResponseDto.builder()
                .fileName(this.fileName)
                .fileId(this.id)
                .filePath(this.filePath).build();
    }

}