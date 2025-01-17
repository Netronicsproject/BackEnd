package hello.netro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class ProfileResponseDto {
    private Long profileId;
    private String profileImage;
    private String bio;
}
