package com.creavispace.project.domain.community.dto.request;

import com.creavispace.project.domain.community.entity.CommunityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRequestDto {
    private CommunityCategory category;
    private String title;
    private String content;
    private List<String> hashTags;
    private List<String> images;
}
