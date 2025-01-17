package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class LikeResponseDto {
    private Long likeId;
    private Long postId;
    private String username;  // 좋아요를 누른 사용자 정보
}
