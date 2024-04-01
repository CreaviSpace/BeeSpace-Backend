package com.creavispace.project.domain.like.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;

public interface LikeService {
    public SuccessResponseDto<LikeResponseDto> likeToggle(Long memberId, Long postId, String postType);
    public SuccessResponseDto<LikeResponseDto> readLike(Long memberId, Long postId, String postType);
    public SuccessResponseDto<LikeCountResponseDto> likeCount(Long postId, String postType);
}
