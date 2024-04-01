package com.creavispace.project.domain.recruit.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.response.DeadLineRecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitDeleteResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface RecruitService {
    public SuccessResponseDto<RecruitResponseDto> createRecruit(Long memberId, RecruitRequestDto dto);
    public SuccessResponseDto<RecruitResponseDto> modifyRecruit(Long memberId, Long recruitId, RecruitRequestDto dto);
    public SuccessResponseDto<RecruitDeleteResponseDto> deleteRecruit(Long memberId, Long recruitId);
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitList(Integer size, Integer page, String category);
    public SuccessResponseDto<RecruitReadResponseDto> readRecruit(Long memberId, Long recruitId, HttpServletRequest request);
    public SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>> readDeadlineRecruitList();
}
