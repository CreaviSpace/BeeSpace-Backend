package com.creavispace.project.domain.bookmark.dto.response;

import com.creavispace.project.domain.bookmark.entity.Bookmark;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class BookmarkContentsResponseDto {
    private Bookmark bookmark;
    private String postType;
    private Long postId;

}
