package com.creavispace.project.domain.comment.service;

import java.util.List;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

public interface CommentService {
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, String postType);
    public SuccessResponseDto<CommentResponseDto> createComment(Long memberId, Long postId, String postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentResponseDto> modifyComment(Long memberId, Long commentId, String postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentDeleteResponseDto> deleteComment(Long memberId, Long commentId, String postType);
}
