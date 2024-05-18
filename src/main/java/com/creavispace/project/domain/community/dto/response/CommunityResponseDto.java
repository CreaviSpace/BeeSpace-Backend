package com.creavispace.project.domain.community.dto.response;

import com.creavispace.project.domain.search.dto.response.SearchListReadResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponseDto implements SearchListReadResponseDto{
    private Long id;
    private String postType;
    private String category;
    private String memberId;
    private String memberNickName;
    private String memberProfile;
    private Integer viewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String title;
    private String content;
    private List<CommunityHashTagDto> hashTags;
    private List<String> images;
}
