package com.creavispace.project.domain.like.service;

import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.member.entity.Member;

public interface LikeStrategy {

    LikeResponseDto likeToggle(Member member,Long postId);

    LikeResponseDto readLike(String memberId, Long postId);

    LikeCountResponseDto likeCount(Long postId);
}
