package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.mypage.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType);
    SuccessResponseDto<CommentResponseDto> createComment(String memberId, Long postId, PostType postType, CommentRequestDto dto);
    SuccessResponseDto<CommentResponseDto> modifyComment(String memberId, Long commentId, PostType postType, CommentRequestDto dto);
    SuccessResponseDto<Long> deleteComment(String memberId, Long commentId, PostType postType);
    SuccessResponseDto<List<MyPageCommentResponseDto>> mypageCommentPost(String memberId, PostType postType, Pageable pageable);
}
