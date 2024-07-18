package com.creavispace.project.domain.hashTag.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.hashTag.repository.CommunityHashTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final CommunityHashTagRepository communityHashTagRepository;

    @Transactional(readOnly = true)
    @Override
    public SuccessResponseDto<List<String>> readPopularHashTagList() {
        // 해시태그 가져오기
        List<String> top3HashTags = communityHashTagRepository.findTop3HashTagsByCount();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "인기 해시태크 10개 조회가 완료되었습니다.", top3HashTags);
    }
    
}
