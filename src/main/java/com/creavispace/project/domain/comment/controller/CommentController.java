package com.creavispace.project.domain.comment.controller;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.alarm.service.AlarmService;
import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    
    private final CommentService commentService;
    private final AlarmService alarmService;

    private static final String READ_COMMENT_LIST = "";
    private static final String CREATE_COMMENT = "";
    private static final String MODIFY_COMMENT = "/{commentId}";
    private static final String DELETE_COMMENT = "/{commentId}";

    @GetMapping(READ_COMMENT_LIST)
    @Operation(summary = "댓글 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<CommentResponseDto>>> readCommentList(
        @RequestParam("postId") Long postId,
        @RequestParam("postType") PostType postType
    ){
        log.info("/comment/controller : 댓글 리스트 조회");
        return ResponseEntity.ok().body(commentService.readCommentList(postId, postType));
    }

    @PostMapping(CREATE_COMMENT)
    @Operation(summary = "댓글 등록")
    public ResponseEntity<SuccessResponseDto<CommentResponseDto>> createComment(
        @AuthenticationPrincipal String memberId,
        @RequestParam("postId") Long postId,
        @RequestParam("postType") PostType postType,
        @RequestBody CommentRequestDto requestBody
    ) {
        log.info("/comment/controller : 댓글 등록");
        SuccessResponseDto<CommentResponseDto> response = commentService.createComment(memberId, postId, postType, requestBody);
        alarmService.createAlarm(memberId,"댓글", postType, postId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(MODIFY_COMMENT)
    @Operation(summary = "댓글 수정")
    public ResponseEntity<SuccessResponseDto<CommentResponseDto>> modifyComment(
        @AuthenticationPrincipal String memberId,
        @PathVariable("commentId") Long commentId,
        @RequestParam("postType") PostType postType,
        @RequestBody CommentRequestDto requestBody
    ) {
        log.info("/comment/controller : 댓글 수정");
        return ResponseEntity.ok().body(commentService.modifyComment(memberId, commentId, postType, requestBody));
    }

    @DeleteMapping(DELETE_COMMENT)
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<SuccessResponseDto<Long>> deleteComment(
        @AuthenticationPrincipal String memberId,
        @PathVariable("commentId") Long commentId,
        @RequestParam("postType") PostType postType
    ){
        log.info("/comment/controller : 댓글 삭제");
        return ResponseEntity.ok().body(commentService.deleteComment(memberId, commentId, postType));
    }

}
