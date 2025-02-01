package hello.netro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String content;
    private String author;  // 작성자 정보
    private int commentCount;  // 댓글 개수
    private int likeCount;  // 좋아요 개수
}
