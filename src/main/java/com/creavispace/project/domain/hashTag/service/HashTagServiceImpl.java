package com.creavispace.project.domain.hashTag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.repository.CommunityHashTagRepository;
import com.creavispace.project.domain.hashTag.dto.response.PopularHashTagReadResponseDto;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTagResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final CommunityHashTagRepository communityHashTagRepository;
    
    @Override
    public SuccessResponseDto<List<PopularHashTagReadResponseDto>> readPopularHashTagList() {
        List<CommunityHashTagResult> communityHashTags = communityHashTagRepository.findTop10HashTag();
        
        List<PopularHashTagReadResponseDto> popularHashTags = communityHashTags.stream()
            .map(communityHashTag -> PopularHashTagReadResponseDto.builder()
                .hashTagId(communityHashTag.getHashTagId())
                .hashTag(communityHashTag.getHashTag())
                .build())
            .collect(Collectors.toList());

        return new SuccessResponseDto<>(true, "인기 해시태크 10개 조회가 완료되었습니다.", popularHashTags);
    }
    
}
