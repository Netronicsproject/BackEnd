package hello.netro.repository;


import hello.netro.domain.Fileitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<Fileitem, Long> {

    List<Fileitem> findByPostId(Long postId);

}
