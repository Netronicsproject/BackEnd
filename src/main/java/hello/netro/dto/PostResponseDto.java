package hello.netro.dto;

import lombok.*;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String author;  // 작성자 정보
    private List<FileResponseDto> files;
    private List<CommentResponseDto> comments;
    private List<LikeResponseDto> likes;
}
