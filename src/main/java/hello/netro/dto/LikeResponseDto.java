package hello.netro.dto;

import hello.netro.domain.Like;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseDto {
    private Long likeId;
    private Long postId;
    private String username;  // 좋아요를 누른 사용자 정보

    public LikeResponseDto(Like like) {
        this.likeId = like.getId();
        this.postId = like.getPost().getId();
        this.username=like.getUser().getName();
    }
}
