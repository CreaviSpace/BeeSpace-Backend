package com.creavispace.project.domain.recruit.dto.response;

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
public class RecruitListReadResponseDto implements SearchListReadResponseDto {
    private Long id;
    private String postType;
    // enum으로 관리 변경
    private String category;
    private String title;
    private String content;
    private Integer amount;
    private Integer now;
    private List<RecruitTechStackResponseDto> techStacks;
    private LocalDateTime createdDate;
    private String memberNickname;
    private String memberProfile;
}
