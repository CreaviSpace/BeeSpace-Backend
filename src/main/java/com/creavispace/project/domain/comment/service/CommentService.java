package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;

import java.util.List;

public interface CommentService {
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType);
    public SuccessResponseDto<CommentResponseDto> createComment(String memberId, Long postId, PostType postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentResponseDto> modifyComment(String memberId, Long commentId, PostType postType, CommentRequestDto dto);
    public SuccessResponseDto<CommentDeleteResponseDto> deleteComment(String memberId, Long commentId, PostType postType);

    //ky
    SuccessResponseDto<List<MyPageCommentResponseDto>> readMyContentsList(String memberId, Integer page, Integer size,
                                                                          String postType, String sortType);
}
