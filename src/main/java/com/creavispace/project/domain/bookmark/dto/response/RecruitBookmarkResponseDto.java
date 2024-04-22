package com.creavispace.project.domain.bookmark.dto.response;

import com.creavispace.project.domain.recruit.dto.response.RecruitTechStackResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class RecruitBookmarkResponseDto extends BookmarkContentsResponseDto{
    List<RecruitTechStackResponseDto> techStacks;
}
