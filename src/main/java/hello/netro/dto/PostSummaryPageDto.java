package hello.netro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostSummaryPageDto {
    private List<PostSummaryDto> content;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
    private boolean isFirst;
}
