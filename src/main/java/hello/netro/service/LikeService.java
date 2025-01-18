package hello.netro.service;


import hello.netro.domain.Like;
import hello.netro.domain.Post;
import hello.netro.domain.User;
import hello.netro.dto.LikeResponseDto;
import hello.netro.repository.FavoriteRepository;
import hello.netro.repository.PostRepository;
import hello.netro.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
            if (Objects.equals(like.getPost().getId(), postId)) {
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

        return like.likeToDto();
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
            dtoList.add(like.likeToDto());
        }

        return dtoList;
    }


}
