package com.creavispace.project.domain.community.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import com.creavispace.project.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
    
    private final CommunityService communityService;

    private static final String CREATE_COMMUNITY = "";
    private static final String MODIFY_COMMUNITY = "/{communityId}";
    private static final String DELETE_COMMUNITY = "/{communityId}";
    private static final String READ_COMMUNITY = "/{communityId}";
    private static final String READ_COMMUNITY_LIST = "";

    @PostMapping(CREATE_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 생성")
    public ResponseEntity<SuccessResponseDto<Long>> createCommunity(
        @AuthenticationPrincipal String memberId,
        @RequestBody CommunityRequestDto requestBody
    ){
        log.info("/community/controller : 커뮤니티 게시글 생성");
        return ResponseEntity.ok().body(communityService.createCommunity(memberId, requestBody));
    }

    @PutMapping(MODIFY_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 수정")
    public ResponseEntity<SuccessResponseDto<Long>> modifyCommunity(
        @AuthenticationPrincipal String memberId,
        @PathVariable("communityId") Long communityId, 
        @RequestBody CommunityRequestDto requestBody
    ){
        log.info("/community/controller : 커뮤니티 게시글 수정");
        return ResponseEntity.ok().body(communityService.modifyCommunity(memberId, communityId, requestBody));
    }

    @DeleteMapping(DELETE_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<Long>> deleteCommunity(
        @AuthenticationPrincipal String memberId,
        @PathVariable("communityId") Long communityId
    ){
        log.info("/community/controller : 커뮤니티 게시글 삭제");
        return ResponseEntity.ok().body(communityService.deleteCommunity(memberId, communityId));
    }

    @GetMapping(READ_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<CommunityReadResponseDto>> readCommunity(
        @AuthenticationPrincipal String memberId,
        @PathVariable("communityId") Long communityId,
        HttpServletRequest request
    ){
        log.info("/community/controller : 커뮤니티 게시글 디테일");
        // todo : 조회수 증가 Service 추가 필요(커뮤니티 조회 Service에서 분리)
        return ResponseEntity.ok().body(communityService.readCommunity(memberId, communityId));
    }
    
    @GetMapping(READ_COMMUNITY_LIST)
    @Operation(summary = "커뮤니티 게시글 리스트 조회 / 인기 태그 게시글 조회")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> readCommunityList(
            @SortDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageRequest,
            @RequestParam(value = "category", required = false) CommunityCategory category,
        @RequestParam(value = "hashTag", required = false) String hashTag

    ){
        log.info("/community/controller : 커뮤니티 게시글 리스트 조회 / 인기 태그 게시글 조회");
        return ResponseEntity.ok().body(communityService.readCommunityList(category, hashTag, pageRequest));
    }


}
