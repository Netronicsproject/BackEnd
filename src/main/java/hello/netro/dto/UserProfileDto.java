package hello.netro.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private String UserName;
    private String introduce;

}
