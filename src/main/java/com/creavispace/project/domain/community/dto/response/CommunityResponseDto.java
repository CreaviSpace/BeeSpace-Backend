package com.creavispace.project.domain.community.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponseDto implements SearchListReadResponseDto{
    private Long id;
    private String postType;
    private String category;
    private Long memberId;
    private String memberNickName;
    private String memberProfile;
    private Integer viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String title;
    private String content;
    private List<CommunityHashTagDto> hashTags;
}
