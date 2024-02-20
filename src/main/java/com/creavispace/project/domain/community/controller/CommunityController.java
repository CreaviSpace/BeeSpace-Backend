package com.creavispace.project.domain.community.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.community.dto.request.CommunityCreateRequestDto;
import com.creavispace.project.domain.community.dto.request.CommunityModifyRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityCreateResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityModifyResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityListReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.service.CommunityService;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<SuccessResponseDto<CommunityCreateResponseDto>> createCommunity(@RequestBody CommunityCreateRequestDto requestBody){
        return ResponseEntity.ok().body(communityService.createCommunity(requestBody));
    }

    @PutMapping(MODIFY_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 수정")
    public ResponseEntity<SuccessResponseDto<CommunityModifyResponseDto>> modifyCommunity(@PathVariable("communityId") Long communityId, @RequestBody CommunityModifyRequestDto requestBody){
        return ResponseEntity.ok().body(communityService.modifyCommunity(communityId, requestBody));
    }

    @DeleteMapping(DELETE_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<CommunityDeleteResponseDto>> deleteCommunity(@PathVariable("communityId") Long communityId){
        return ResponseEntity.ok().body(communityService.deleteCommunity(communityId));
    }

    @GetMapping(READ_COMMUNITY)
    @Operation(summary = "커뮤니티 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<CommunityReadResponseDto>> readCommunity(@PathVariable("communityId") Long communityId){
        return ResponseEntity.ok().body(communityService.readCommunity(communityId));
    }
    
    @GetMapping(READ_COMMUNITY_LIST)
    @Operation(summary = "커뮤니티 게시글 리스트 조회 / 인기 태그 게시글 조회")
    public ResponseEntity<SuccessResponseDto<List<CommunityListReadResponseDto>>> readCommunityList(
        @RequestParam(value = "hashTag", required = false) String hashTag,
        @RequestParam(value = "type", required = false) String type
    ){
        return ResponseEntity.ok().body(communityService.readCommunityList(hashTag, type));
    }


}
