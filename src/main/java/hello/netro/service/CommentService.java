package hello.netro.service;

import hello.netro.domain.Comment;
import hello.netro.domain.Post;
import hello.netro.domain.User;
import hello.netro.repository.CommentRepository;
import hello.netro.repository.PostRepository;
import hello.netro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment createComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 없음"));

        Comment comment = Comment.createComment(post, user, content);
        return commentRepository.save(comment);
    }

    //대댓글 작성
    @Transactional
    public Comment createReply(Long postId, Long userId, String content, Long parentCommentId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 없음"));
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 없음"));
        if(parent.isReplyComment()) //부모가 대댓글인 경우
        {
            throw new RuntimeException("대댓글에 댓글달기 불가능 ");
        }
        Comment reply = Comment.createReply(post, user, content, parent);
        return commentRepository.save(reply);
    }
}
