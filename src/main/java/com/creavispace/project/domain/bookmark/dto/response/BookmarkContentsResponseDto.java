package com.creavispace.project.domain.bookmark.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class BookmarkContentsResponseDto {
    private String postType;
    private Long postId;
    private String category;
    private String title;
    private String contents;
    private Integer viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
