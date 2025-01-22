package hello.netro.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequestDto {
    private Long postId;  // 좋아요를 누를 게시물의 ID
    private Long userId;  // 좋아요를 누르는 사용자의 ID
}
