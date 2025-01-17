package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class LikeRequestDto {
    private Long postId;  // 좋아요를 누를 게시물의 ID
    private Long userId;  // 좋아요를 누르는 사용자의 ID
}
