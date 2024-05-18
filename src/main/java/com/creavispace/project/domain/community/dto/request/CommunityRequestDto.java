package com.creavispace.project.domain.community.dto.request;

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
    private String category;
    private String title;
    private String content;
    private List<String> hashTags;
    private List<String> images;
}
