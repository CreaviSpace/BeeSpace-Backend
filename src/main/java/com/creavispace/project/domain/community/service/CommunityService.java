package com.creavispace.project.domain.community.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityService {
    public SuccessResponseDto<Long> createCommunity(String memberId, CommunityRequestDto requestBody);
    public SuccessResponseDto<Long> modifyCommunity(String memberId, Long communityId, CommunityRequestDto requestBody);
    public SuccessResponseDto<Long> deleteCommunity(String memberId, Long communityId);
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(String memberId, Long communityId);
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(CommunityCategory category, String hashTag, Pageable pageRequest);
    public SuccessResponseDto<List<CommunityResponseDto>> mypageCommunityList(Pageable pageable, CommunityCategory category, String memberId, String memberId_param);
}
