package com.creavispace.project.domain.member.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.member.dto.response.MemberResponseDto;
import com.creavispace.project.domain.member.dto.response.SearchMemberResponseDto;

import java.util.List;

public interface MemberService {
    void withdrawn(String memberId);
    SuccessResponseDto<MemberResponseDto> readMember(String memberId);
    SuccessResponseDto<List<SearchMemberResponseDto>> searchMember(String text);
}