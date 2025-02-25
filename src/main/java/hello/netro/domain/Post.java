package hello.netro.domain;

import hello.netro.dto.FileResponseDto;
import hello.netro.dto.LikeResponseDto;
import hello.netro.dto.PostResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Fileitem> fileitems = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    // getters and setters

    public PostResponseDto toDto(String baseUrl) {
        return PostResponseDto.builder()
                .postId(this.id)
                .title(this.title)
                .author(this.user.getName())
                .content(this.content)
                .files(this.fileitems.stream()
                .map(file->new FileResponseDto(file,baseUrl))
                .collect(Collectors.toList())
                )
                .likes(this.likes.stream()
                        .map(LikeResponseDto::new)
                        .collect(Collectors.toList()))
                .lastModified(this.getModifiedDate())
                .build();
    }
}
