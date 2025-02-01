package hello.netro.domain;

import hello.netro.dto.CommentResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    // 부모 댓글을 참조하는 필드 (대댓글일 경우)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 대댓글 목록 (해당 댓글의 대댓글들)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();



    public boolean isReplyComment() {
        return parent != null;
    }
    public void addReply(Comment child) {
        this.replies.add(child);
        child.setParent(this);
    }
    public static Comment createComment(Post post, User user, String content) {
        Comment comment = new Comment();
        comment.post = post;
        comment.user = user;
        comment.content = content;
        // 필요 시 추가 검증이나 로직
        return comment;
    }
    //대댓글 생성
    public static Comment createReply(Post post, User user, String content, Comment parent) {
        if (parent.isReplyComment()) {
            throw new IllegalArgumentException("대댓글의 대댓글은 허용되지 않는다");
        }
        Comment reply = new Comment();
        reply.post = post;
        reply.user = user;
        reply.content = content;
        reply.parent = parent;
        return reply;
    }



}