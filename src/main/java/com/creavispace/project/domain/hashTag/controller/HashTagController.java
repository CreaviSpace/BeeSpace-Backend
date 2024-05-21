package com.creavispace.project.domain.hashTag.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.hashTag.dto.response.PopularHashTagReadResponseDto;
import com.creavispace.project.domain.hashTag.service.HashTagService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hashtag")
public class HashTagController {
    
    private final HashTagService hashTagService;

    private static final String READ_POPULAR_HASHTAG = "/popular";

    @GetMapping(READ_POPULAR_HASHTAG)
    @Operation(summary = "인기 해쉬태그 10개 조회")
    public ResponseEntity<SuccessResponseDto<List<PopularHashTagReadResponseDto>>> readPopularHashTagList(){
        log.info("/hashtag/controller : 인기 해쉬태그 10개 조회");
        return ResponseEntity.ok().body(hashTagService.readPopularHashTagList());
    }
}
