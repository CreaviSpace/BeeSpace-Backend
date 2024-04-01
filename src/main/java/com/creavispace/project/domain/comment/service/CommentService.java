package com.creavispace.project.domain.comment.service;

import java.util.List;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;

public interface CommentService {
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType);
    public SuccessResponseDto<CommentResponseDto> createComment(Long memberId, Long postId, PostType postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentResponseDto> modifyComment(Long memberId, Long commentId, PostType postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentDeleteResponseDto> deleteComment(Long memberId, Long commentId, PostType postType);
}
