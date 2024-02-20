package com.creavispace.project.domain.community.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityModifyRequestDto {
    // enum으로 교체
    private String kind;
    private String title;
    private String content;
    private List<String> hashTagList;
}
