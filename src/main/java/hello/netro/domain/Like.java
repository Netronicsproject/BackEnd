package hello.netro.domain;

import hello.netro.dto.LikeResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="Likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public LikeResponseDto likeToDto() {
        return LikeResponseDto.builder()
                .likeId(this.id)
                .postId(this.post.getId())
                .username(this.user.getName())
                .build();
    }
}
