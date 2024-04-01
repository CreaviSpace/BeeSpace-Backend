package com.creavispace.project.domain.community.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.CommunityCategory;
import com.creavispace.project.domain.common.dto.type.OrderBy;
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
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(Integer size, Integer page, CommunityCategory category, String hashTag, OrderBy orderby);
}
