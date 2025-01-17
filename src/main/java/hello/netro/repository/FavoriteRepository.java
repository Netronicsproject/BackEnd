package hello.netro.repository;


import hello.netro.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    List<Like> findAllByUserId(Long userId);
}
