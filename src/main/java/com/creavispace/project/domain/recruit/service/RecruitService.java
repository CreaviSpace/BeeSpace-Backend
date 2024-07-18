package com.creavispace.project.domain.recruit.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.response.DeadLineRecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitReadResponseDto;
import com.creavispace.project.domain.recruit.entity.Recruit;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecruitService {
    public SuccessResponseDto<Long> createRecruit(String memberId, RecruitRequestDto dto);
    public SuccessResponseDto<Long> modifyRecruit(String memberId, Long recruitId, RecruitRequestDto dto);
    public SuccessResponseDto<Long> deleteRecruit(String memberId, Long recruitId);
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitList(Pageable pageRequest, Recruit.Category category);
    public SuccessResponseDto<RecruitReadResponseDto> readRecruit(String memberId, Long recruitId);
    public SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>> readDeadlineRecruitList();
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitListByMemberId(Pageable pageable, Recruit.Category category, String memberId, String memberId_param);
}
