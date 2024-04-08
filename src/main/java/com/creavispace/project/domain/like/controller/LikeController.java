package com.creavispace.project.domain.like.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.service.LikeService;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    
    private final LikeService likeService;

    private static final String LIKE_TOGGLE = "";
    private static final String READ_LIKE = "";
    private static final String READ_LIKE_COUNT = "/count";

    @PostMapping(LIKE_TOGGLE)
    @Operation(summary = "좋아요 토글 기능")
    public ResponseEntity<SuccessResponseDto<LikeResponseDto>> likeToggle(
        @AuthenticationPrincipal String memberId,
        @RequestParam(value = "postId") Long postId,
        @RequestParam(value = "postType") String postType
    ) {
        log.info("/like/controller : 좋아요 토글 기능");
        PostType postTypeEnum = CustomValueOf.valueOf(PostType.class, postType, GlobalErrorCode.NOT_FOUND_POST_TYPE);
        return ResponseEntity.ok().body(likeService.likeToggle(memberId, postId, postTypeEnum));
    }

    @GetMapping(READ_LIKE)
    @Operation(summary = "좋아요 상태 조회 기능")
    public ResponseEntity<SuccessResponseDto<LikeResponseDto>> readLike(
        @AuthenticationPrincipal String memberId,
        @RequestParam(value = "postId") Long postId,
        @RequestParam(value = "postType") String postType
    ){
        log.info("/like/controller : 좋아요 상태 조회 기능");
        PostType postTypeEnum = CustomValueOf.valueOf(PostType.class, postType, GlobalErrorCode.NOT_FOUND_POST_TYPE);
        return ResponseEntity.ok().body(likeService.readLike(memberId, postId, postTypeEnum));
    }

    @GetMapping(READ_LIKE_COUNT)
    @Operation(summary = "좋아요 수 조회 기능")
    public ResponseEntity<SuccessResponseDto<LikeCountResponseDto>> readlikeCount(
        @RequestParam(value = "postId") Long postId,
        @RequestParam(value = "postType") String postType
    ){
        log.info("/like/controller : 좋아요 수 조회 기능");
        PostType postTypeEnum = CustomValueOf.valueOf(PostType.class, postType, GlobalErrorCode.NOT_FOUND_POST_TYPE);
        return ResponseEntity.ok().body(likeService.likeCount(postId, postTypeEnum));
    }
    
}
