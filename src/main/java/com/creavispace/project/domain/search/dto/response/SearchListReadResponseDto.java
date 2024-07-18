package com.creavispace.project.domain.search.dto.response;

import com.creavispace.project.common.dto.type.PostType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SearchListReadResponseDto {
    private Long id;
    private PostType postType;
    private String thumbnail;
    private String bannerContent;
    private String title;
    private String content;
    private LocalDateTime createdDate;

}
