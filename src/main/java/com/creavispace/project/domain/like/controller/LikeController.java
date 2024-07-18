package com.creavispace.project.domain.like.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        @RequestParam(value = "postType") PostType postType
    ) {
        log.info("/like/controller : 좋아요 토글 기능");
        return ResponseEntity.ok().body(likeService.likeToggle(memberId, postId, postType));
    }

    @GetMapping(READ_LIKE)
    @Operation(summary = "좋아요 상태 조회 기능")
    public ResponseEntity<SuccessResponseDto<LikeResponseDto>> readLike(
        @AuthenticationPrincipal String memberId,
        @RequestParam(value = "postId") Long postId,
        @RequestParam(value = "postType") PostType postType
    ){
        log.info("/like/controller : 좋아요 상태 조회 기능");
        return ResponseEntity.ok().body(likeService.readLike(memberId, postId, postType));
    }

    @GetMapping(READ_LIKE_COUNT)
    @Operation(summary = "좋아요 수 조회 기능")
    public ResponseEntity<SuccessResponseDto<LikeCountResponseDto>> readlikeCount(
        @RequestParam(value = "postId") Long postId,
        @RequestParam(value = "postType") PostType postType
    ){
        log.info("/like/controller : 좋아요 수 조회 기능");
        return ResponseEntity.ok().body(likeService.likeCount(postId, postType));
    }
    
}
