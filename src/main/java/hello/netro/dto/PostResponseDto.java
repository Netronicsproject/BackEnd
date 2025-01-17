package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String author;  // 작성자 정보
    private List<FileResponseDto> files;
    private List<CommentResponseDto> comments;
    private List<LikeResponseDto> likes;
}
