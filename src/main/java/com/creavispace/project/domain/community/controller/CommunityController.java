package com.creavispace.project.domain.community.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.service.CommunityService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<SuccessResponseDto<CommunityResponseDto>> createCommunity(
        @AuthenticationPrincipal Long memberId, 
        @RequestBody CommunityRequestDto requestBody
    ){
        return ResponseEntity.ok().body(communityService.createCommunity(memberId, requestBody));
    }

    @PutMapping(MODIFY_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 수정")
    public ResponseEntity<SuccessResponseDto<CommunityResponseDto>> modifyCommunity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("communityId") Long communityId, 
        @RequestBody CommunityRequestDto requestBody
    ){
        return ResponseEntity.ok().body(communityService.modifyCommunity(memberId, communityId, requestBody));
    }

    @DeleteMapping(DELETE_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<CommunityDeleteResponseDto>> deleteCommunity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("communityId") Long communityId
    ){
        return ResponseEntity.ok().body(communityService.deleteCommunity(memberId, communityId));
    }

    @GetMapping(READ_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<CommunityReadResponseDto>> readCommunity(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("communityId") Long communityId,
        HttpServletRequest request
    ){
        return ResponseEntity.ok().body(communityService.readCommunity(memberId, communityId, request));
    }
    
    @GetMapping(READ_COMMUNITY_LIST)
    @Operation(summary = "커뮤니티 게시글 리스트 조회 / 인기 태그 게시글 조회")
    public ResponseEntity<SuccessResponseDto<List<CommunityResponseDto>>> readCommunityList(
        @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "category", required = false) String category,
        @RequestParam(value = "hashTag", required = false) String hashTag,
        @RequestParam(value = "orderby", required = false, defaultValue = "latest-activity") String orderby

    ){
        return ResponseEntity.ok().body(communityService.readCommunityList(size, page, category, hashTag, orderby));
    }


}
