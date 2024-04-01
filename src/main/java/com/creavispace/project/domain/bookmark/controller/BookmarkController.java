package com.creavispace.project.domain.bookmark.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.service.BookmarkService;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;

    private static final String TOGGLE_BOOKMARK = "";
    private static final String READ_BOOKMARK = "";

    @PostMapping(TOGGLE_BOOKMARK)
    @Operation(summary = "북마크 토글 기능")
    public ResponseEntity<SuccessResponseDto<BookmarkResponseDto>> bookmark(
        @AuthenticationPrincipal Long memberId,
        @RequestParam("postId") Long postId,
        @RequestParam("postType") String postType
    ) {
        return ResponseEntity.ok().body(bookmarkService.bookmarkToggle(memberId, postId, postType));
    }
    
    @GetMapping(READ_BOOKMARK)
    @Operation(summary = "북마크 상태 조회 기능")
    public ResponseEntity<SuccessResponseDto<BookmarkResponseDto>> readBookmark(
        @AuthenticationPrincipal Long memberId,
        @RequestParam("postId") Long postId,
        @RequestParam("postType") String postType
    ){
        return ResponseEntity.ok().body(bookmarkService.readBookmark(memberId, postId, postType));
    }
    
}
