package com.creavispace.project.domain.recruit.controller;

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
import com.creavispace.project.domain.common.dto.type.RecruitCategory;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.response.DeadLineRecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitDeleteResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitResponseDto;
import com.creavispace.project.domain.recruit.service.RecruitService;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recruit")
public class RecruitController {
    
    private final RecruitService recruitService;

    private static final String CREATE_RECRUIT = "";
    private static final String MODIFY_RECRUIT = "/{recruitId}";
    private static final String DELETE_RECRUIT = "/{recruitId}";
    private static final String READ_RECRUIT_LIST = "";
    private static final String READ_RECRUIT = "/{recruitId}";
    private static final String READ_DEADLINE_RECRUIT_LIST = "/deadline";

    @PostMapping(CREATE_RECRUIT)
    @Operation(summary = "모집 게시글 생성")
    public ResponseEntity<SuccessResponseDto<RecruitResponseDto>> createRecruit(
        @AuthenticationPrincipal Long memberId,
        @RequestBody RecruitRequestDto requestBody
    ){
        log.info("/recruit/controller : 모집 게시글 생성");
        return ResponseEntity.ok().body(recruitService.createRecruit(memberId, requestBody));
    }

    @PutMapping(MODIFY_RECRUIT)
    @Operation(summary = "모집 게시글 수정")
    public ResponseEntity<SuccessResponseDto<RecruitResponseDto>> modifyRecruit(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("recruitId") Long recruitId, 
        @RequestBody RecruitRequestDto requestBody
    ){
        log.info("/recruit/controller : 모집 게시글 수정");
        return ResponseEntity.ok().body(recruitService.modifyRecruit(memberId, recruitId, requestBody));
    }

    @DeleteMapping(DELETE_RECRUIT)
    @Operation(summary = "모집 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<RecruitDeleteResponseDto>> deleteRecruit(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("recruitId") Long recruitId
    ){
        log.info("/recruit/controller : 모집 게시글 삭제");
        return ResponseEntity.ok().body(recruitService.deleteRecruit(memberId, recruitId));
    }

    @GetMapping(READ_RECRUIT_LIST)
    @Operation(summary = "모집 게시글 리스트")
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> readRecruitList(
        @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "category", required = false) String category
    ){
        log.info("/recruit/controller : 모집 게시글 리스트");
        RecruitCategory recruitCategory = null;
        if(category != null){
            recruitCategory = CustomValueOf.valueOf(RecruitCategory.class, category, GlobalErrorCode.NOT_FOUND_RECRUIT_CATEGORY);
        }
        return ResponseEntity.ok().body(recruitService.readRecruitList(size, page, recruitCategory));
    }

    @GetMapping(READ_RECRUIT)
    @Operation(summary = "모집 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<RecruitReadResponseDto>> readRecruit(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("recruitId") Long recruitId,
        HttpServletRequest request
    ){
        log.info("/recruit/controller : 모집 게시글 디테일");
        return ResponseEntity.ok().body(recruitService.readRecruit(memberId, recruitId, request));
    }

    @GetMapping(READ_DEADLINE_RECRUIT_LIST)
    @Operation(summary = "모집 마감 리스트 / 모집 베너 조회")
    public ResponseEntity<SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>>> readDeadlineRecruitList(){
        log.info("/recruit/controller : 모집 마감 리스트 / 모집 베너 조회");
        return ResponseEntity.ok().body(recruitService.readDeadlineRecruitList());
    }

}
