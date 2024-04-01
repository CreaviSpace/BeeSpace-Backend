package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

public interface BookmarkService {
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(Long memberId, Long postId, String postType);
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(Long memberId, Long postId, String postType);
}
