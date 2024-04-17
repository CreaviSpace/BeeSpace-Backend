package com.creavispace.project.domain.recruit.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.RecruitCategory;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RecruitService {
    public SuccessResponseDto<RecruitResponseDto> createRecruit(String memberId, RecruitRequestDto dto);
    public SuccessResponseDto<RecruitResponseDto> modifyRecruit(String memberId, Long recruitId, RecruitRequestDto dto);
    public SuccessResponseDto<RecruitDeleteResponseDto> deleteRecruit(String memberId, Long recruitId);
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitList(Integer size, Integer page, RecruitCategory category);
    public SuccessResponseDto<RecruitReadResponseDto> readRecruit(String memberId, Long recruitId, HttpServletRequest request);
    public SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>> readDeadlineRecruitList();

    //ky
    SuccessResponseDto<List<RecruitListReadResponseDto>> readMyRecruitList(Member member, Integer size, Integer page, RecruitCategory category, String sortType);
    SuccessResponseDto<List<RecruitListReadResponseDto>> readMyRecruitList(String memberId, Integer size, Integer page, String sortType);
    SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitListForAdmin(Integer size, Integer page, String status, String sortType);
}
