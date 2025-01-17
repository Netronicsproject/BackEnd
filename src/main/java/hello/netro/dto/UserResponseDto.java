package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
}
