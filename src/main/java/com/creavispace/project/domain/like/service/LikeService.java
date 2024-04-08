package com.creavispace.project.domain.like.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;

public interface LikeService {
    public SuccessResponseDto<LikeResponseDto> likeToggle(String memberId, Long postId, PostType postType);
    public SuccessResponseDto<LikeResponseDto> readLike(String memberId, Long postId, PostType postType);
    public SuccessResponseDto<LikeCountResponseDto> likeCount(Long postId, PostType postType);
}
