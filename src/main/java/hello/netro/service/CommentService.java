package hello.netro.service;

import hello.netro.domain.Comment;
import hello.netro.domain.Post;
import hello.netro.domain.User;
import hello.netro.dto.CommentResponseDto;
import hello.netro.repository.CommentRepository;
import hello.netro.repository.PostRepository;
import hello.netro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 없음"));

        Comment comment = Comment.createComment(post, user, content);
        commentRepository.save(comment);

        return convertToDto(comment);
    }

    @Transactional
    public CommentResponseDto createReply(Long postId, Long userId, String content, Long parentCommentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 없음"));
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 없음"));

        if (parent.isReplyComment()) {  // 부모가 대댓글인 경우
            throw new RuntimeException("대댓글에 댓글 달기 불가능");
        }

        Comment reply = Comment.createReply(post, user, content, parent);
        commentRepository.save(reply);

        return convertToDto(reply);
    }

    // 특정 게시글의 모든 댓글 조회 (대댓글 포함)
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .filter(comment -> comment.getParent() == null) // 최상위 댓글만 가져옴
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 없음"));
        commentRepository.delete(comment);
    }

    // 엔티티를 DTO로 변환하는 메서드
    private CommentResponseDto convertToDto(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .author(comment.getUser().getName())
                .createdAt(comment.getCreatedDate())
                .replies(comment.getReplies().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
