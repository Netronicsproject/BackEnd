package hello.netro.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long profileId;
    private String profileImage;
    private String bio;
}
