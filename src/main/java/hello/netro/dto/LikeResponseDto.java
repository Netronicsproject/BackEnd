package hello.netro.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseDto {
    private Long likeId;
    private Long postId;
    private String username;  // 좋아요를 누른 사용자 정보
}
