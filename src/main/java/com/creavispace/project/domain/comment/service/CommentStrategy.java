package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentStrategy {
    List<CommentResponseDto> readCommentList(Long postId);
    CommentResponseDto createComment(Long postId, Member member, String content);

    CommentResponseDto modifyComment(Member member, Long commentId, CommentRequestDto dto);

    void deleteComment(Member member, Long commentId);

    List<CommentResponseDto> readMyContentsList(String memberId, Pageable pageRequest);
}
