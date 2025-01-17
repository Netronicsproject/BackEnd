package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> replies;  // 대댓글 리스트
}
