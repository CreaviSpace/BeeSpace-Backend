package com.creavispace.project.domain.community.service;

import com.creavispace.project.domain.admin.dto.DailySummary;
import com.creavispace.project.domain.admin.dto.MonthlySummary;
import com.creavispace.project.domain.admin.dto.YearlySummary;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.CommunityCategory;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.global.common.Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityService extends Service {
    public SuccessResponseDto<CommunityResponseDto> createCommunity(String memberId, CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityResponseDto> modifyCommunity(String memberId, Long communityId, CommunityRequestDto requestBody);
    public SuccessResponseDto<CommunityDeleteResponseDto> deleteCommunity(String memberId, Long communityId);
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(String memberId, Long communityId, HttpServletRequest request);
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(CommunityCategory category, String hashTag, Pageable pageRequest);

    //ky
     SuccessResponseDto<List<CommunityResponseDto>> readMyCommunityList(String memberId, Integer size, Integer page, String orderby);
     SuccessResponseDto<List<CommunityResponseDto>> readCommunityListForAdmin(Integer size, Integer page, String status, String sortType);

    SuccessResponseDto<List<MonthlySummary>> countMonthlySummary(int year);

    SuccessResponseDto<List<YearlySummary>> countYearlySummary();

    SuccessResponseDto<List<DailySummary>> countDailySummary(int year, int month);

}
