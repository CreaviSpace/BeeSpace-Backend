package com.creavispace.project.domain.community.service;

import java.util.List;

import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;

public interface CommunityService {
    public SuccessResponseDto<CommunityResponseDto> createCommunity(CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityResponseDto> modifyCommunity(Long communityId, CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityDeleteResponseDto> deleteCommunity(Long communityId);
    public SuccessResponseDto<CommunityResponseDto> readCommunity(Long communityId);
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(Integer size, Integer page, String category, String hashTag);
}
