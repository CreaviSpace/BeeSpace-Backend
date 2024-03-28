package com.creavispace.project.domain.community.service;

import com.creavispace.project.domain.member.entity.Member;
import java.util.List;

import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;

import jakarta.servlet.http.HttpServletRequest;

import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;

public interface CommunityService {
    public SuccessResponseDto<CommunityResponseDto> createCommunity(Long memberId, CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityResponseDto> modifyCommunity(Long memberId, Long communityId, CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityDeleteResponseDto> deleteCommunity(Long memberId, Long communityId);
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(Long memberId, Long communityId, HttpServletRequest request);
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(Integer size, Integer page, String category, String hashTag, String orderby);

    //ky
     SuccessResponseDto<List<CommunityResponseDto>> readMyCommunityList(Long memberId, Integer size, Integer page, String orderby);
     SuccessResponseDto<List<CommunityResponseDto>> readMyCommunityList(Member member, Integer size, Integer page, String orderby);
}
