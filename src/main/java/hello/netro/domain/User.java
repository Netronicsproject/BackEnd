package hello.netro.domain;

import hello.netro.dto.UserProfileDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="Users")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.GUEST;  // 기본값으로 GUEST 설정

    @Column(nullable = true)
    private String profilePath;

    @Column(nullable = true)
    private String introduce;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public User (String name,String email)
    {
        this.name=name;
        this.email=email;
    }

    public void updateProfile(UserProfileDto userProfileDto)
    {
        this.name= userProfileDto.getUserName();
        this.picture=userProfileDto.getPicture();
        this.introduce= userProfileDto.getIntroduce();
    }

}
