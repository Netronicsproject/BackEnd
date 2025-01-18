package hello.netro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResult {
    private String code;  // Error type (e.g., "NOT_FOUND", "INTERNAL_ERROR")
    private String message; // Error message
}
