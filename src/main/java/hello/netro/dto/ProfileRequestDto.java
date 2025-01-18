package hello.netro.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestDto {
    private String profileImage;
    private String bio;
}
