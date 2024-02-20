package com.creavispace.project.domain.recruit.controller;

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
import com.creavispace.project.domain.recruit.dto.request.RecruitCreateRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitModifyRequestDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitCreateResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitDeleteResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitListReadResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitModifyResponseDto;
import com.creavispace.project.domain.recruit.dto.response.RecruitReadResponseDto;
import com.creavispace.project.domain.recruit.service.RecruitService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

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

    @PostMapping(CREATE_RECRUIT)
    @Operation(summary = "모집 게시글 생성")
    public ResponseEntity<SuccessResponseDto<RecruitCreateResponseDto>> createRecruit(@RequestBody RecruitCreateRequestDto requestBody){
        return ResponseEntity.ok().body(recruitService.createRecruit(requestBody));
    }

    @PutMapping(MODIFY_RECRUIT)
    @Operation(summary = "모집 게시글 수정")
    public ResponseEntity<SuccessResponseDto<RecruitModifyResponseDto>> modifyRecruit(@PathVariable("recruitId") Long recruitId, @RequestBody RecruitModifyRequestDto requestBody){
        return ResponseEntity.ok().body(recruitService.modifyRecruit(recruitId, requestBody));
    }

    @DeleteMapping(DELETE_RECRUIT)
    @Operation(summary = "모집 게시글 삭제")
    public ResponseEntity<SuccessResponseDto<RecruitDeleteResponseDto>> deleteRecruit(@PathVariable("recruitId") Long recruitId){
        return ResponseEntity.ok().body(recruitService.deleteRecruit(recruitId));
    }

    @GetMapping(READ_RECRUIT_LIST)
    @Operation(summary = "모집 게시글 리스트")
    public ResponseEntity<SuccessResponseDto<List<RecruitListReadResponseDto>>> readRecruitList(
        @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page
    ){
        return ResponseEntity.ok().body(recruitService.readRecruitList(size, page));
    }

    @GetMapping(READ_RECRUIT)
    @Operation(summary = "모집 게시글 디테일")
    public ResponseEntity<SuccessResponseDto<RecruitReadResponseDto>> readRecruit(@PathVariable("recruitId") Long recruitId){
        return ResponseEntity.ok().body(recruitService.readRecruit(recruitId));
    }

    @GetMapping(READ_DEADLINE_RECRUIT_LIST)
    @Operation(summary = "모집 마감 리스트")
    public ResponseEntity<SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>>> readDeadlineRecruitList(){
        return ResponseEntity.ok().body(recruitService.readDeadlineRecruitList());
    }
    
}
