package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.Bookmark;
import com.creavispace.project.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkStrategy {
    BookmarkResponseDto bookmarkToggle(Long postId, Member member);
    BookmarkResponseDto readBookmark(Long postId, String memberId);

    List<Bookmark> readMyBookmark(String memberId, Pageable pageRequest);
}
