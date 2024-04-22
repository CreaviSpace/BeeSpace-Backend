package com.creavispace.project.domain.bookmark.dto.response;

import com.creavispace.project.domain.project.dto.response.ProjectLinkResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ProjectBookmarkResponseDto extends BookmarkContentsResponseDto{

    private String thumbnail;
    private List<ProjectLinkResponseDto> links;

}
