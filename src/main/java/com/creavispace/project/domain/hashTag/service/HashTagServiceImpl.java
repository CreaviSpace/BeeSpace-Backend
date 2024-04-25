package com.creavispace.project.domain.hashTag.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.hashTag.dto.response.PopularHashTagReadResponseDto;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTagResult;
import com.creavispace.project.domain.hashTag.repository.CommunityHashTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final CommunityHashTagRepository communityHashTagRepository;
    
    @Override
    public SuccessResponseDto<List<PopularHashTagReadResponseDto>> readPopularHashTagList() {
        List<PopularHashTagReadResponseDto> data = null;

        // 해시태그 가져오기
        List<CommunityHashTagResult> communityHashTags = communityHashTagRepository.findTop10HashTag();
        
        // 해시태그 DTO
        data = communityHashTags.stream()
            .map(communityHashTag -> PopularHashTagReadResponseDto.builder()
                .hashTag(communityHashTag.getHashTag())
                .build())
            .collect(Collectors.toList());

        log.info("/feedback/service : analysisFeedback success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "인기 해시태크 10개 조회가 완료되었습니다.", data);
    }
    
}
