package com.creavispace.project.domain.bookmark.dto.response;

import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CommunityBookmarkResponseDto extends BookmarkContentsResponseDto{
    private List<CommunityHashTagDto> hashTags;

}
