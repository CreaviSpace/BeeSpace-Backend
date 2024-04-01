package com.creavispace.project.domain.comment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.service.CommentService;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    
    private final CommentService commentService;

    private static final String READ_COMMENT_LIST = "";
    private static final String CREATE_COMMENT = "";
    private static final String MODIFY_COMMENT = "/{commentId}";
    private static final String DELETE_COMMENT = "/{commentId}";

    @GetMapping(READ_COMMENT_LIST)
    @Operation(summary = "댓글 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<CommentResponseDto>>> readCommentList(
        @RequestParam("postId") Long postId,
        @RequestParam("postType") String postType
    ){
        return ResponseEntity.ok().body(commentService.readCommentList(postId, postType));
    }

    @PostMapping(CREATE_COMMENT)
    @Operation(summary = "댓글 등록")
    public ResponseEntity<SuccessResponseDto<CommentResponseDto>> createComment(
        @AuthenticationPrincipal Long memberId,
        @RequestParam("postId") Long postId,
        @RequestParam("postType") String postType,
        @RequestBody CommentRequestDto requestBody) {
        return ResponseEntity.ok().body(commentService.createComment(memberId, postId, postType, requestBody));
    }

    @PutMapping(MODIFY_COMMENT)
    @Operation(summary = "댓글 수정")
    public ResponseEntity<SuccessResponseDto<CommentResponseDto>> modifyComment(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("commentId") Long commentId,
        @RequestParam("postType") String postType,
        @RequestBody CommentRequestDto requestBody
    ) {
        return ResponseEntity.ok().body(commentService.modifyComment(memberId, commentId, postType, requestBody));
    }

    @DeleteMapping(DELETE_COMMENT)
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<SuccessResponseDto<CommentDeleteResponseDto>> deleteComment(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("commentId") Long commentId,
        @RequestParam("postType") String postType
    ){
        return ResponseEntity.ok().body(commentService.deleteComment(memberId, commentId, postType));
    }

}
