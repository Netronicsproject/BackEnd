package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class CommentRequestDto {
    private Long postId;  // 어느 게시물에 댓글을 달 것인지
    private Long parentId;  // 부모 댓글 ID (대댓글일 경우)
    private String content;
}
