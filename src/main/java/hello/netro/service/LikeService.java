package hello.netronics.service;

import hello.netronics.domain.Like;
import hello.netronics.domain.Post;
import hello.netronics.domain.User;
import hello.netronics.dto.LikeResponseDto;
import hello.netronics.repository.FavoriteRepository;
import hello.netronics.repository.PostRepository;
import hello.netronics.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public LikeResponseDto addLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("해당 사용자가 없습니다. : %d", userId)));

        List<Like> likes = user.getLikes();
        for (Like like : likes) {
            if (like.getPost().getId() == postId) {
                throw new RuntimeException("already likes this post");
            }
        }

        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new NoSuchElementException("해당 게시물이 없습니다."));

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        user.getLikes().add(like);
        post.getLikes().add(like);
        favoriteRepository.save(like);

        return LikeToDto(like);
    }

    @Transactional
    public void deleteLike(Long userId, Long postId) {
        Like like = favoriteRepository.findByUserIdAndPostId(userId, postId)
                        .orElseThrow(() -> new NoSuchElementException());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException());

        user.getLikes().remove(like);
        post.getLikes().remove(like);

        favoriteRepository.delete(like);
    }

    public List<LikeResponseDto> loadLikeAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException());

        List<Like> likes = user.getLikes();

        List<LikeResponseDto> dtoList = new ArrayList<>();

        for (Like like : likes) {
            dtoList.add(LikeToDto(like));
        }

        return dtoList;
    }

    private LikeResponseDto LikeToDto(Like like) {
        LikeResponseDto dto = new LikeResponseDto();

        dto.setUsername(like.getUser().getName());
        dto.setLikeId(like.getId());
        dto.setPostId(like.getPost().getId());

        return dto;
    }
}
