package hello.netro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotNull
    private Long postId;  // 어느 게시물에 댓글을 달 것인지
    private Long parentId;  // 부모 댓글 ID (대댓글일 경우)
    @NotBlank
    private String content;
}
