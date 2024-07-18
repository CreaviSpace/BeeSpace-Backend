package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.member.entity.Member;

public interface BookmarkStrategy {
    BookmarkResponseDto bookmarkToggle(Long postId, Member member);
    BookmarkResponseDto readBookmark(Long postId, String memberId);

//    List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest);
}
