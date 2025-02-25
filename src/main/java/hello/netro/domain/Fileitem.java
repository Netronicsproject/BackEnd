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
    @JoinColumn(name = "post_id", nullable = true) //post가 없으면 프로필 이미지임
    private Post post;

    //논리적 파일이름( 저장할때는 여기에 추가로 UUID를 덧붙임)
    @Column(nullable = false)
    private String fileName;

    //파일 저장 상대경로
    @Column(nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    private FileType fileType; // 예: IMAGE, ATTACHMENT, VIDEO 등
    // getters and setters



}