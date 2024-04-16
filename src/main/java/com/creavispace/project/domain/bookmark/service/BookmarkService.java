package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.creavispace.project.domain.common.dto.type.PostType;
import java.util.List;

public interface BookmarkService {
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(String memberId, Long postId, PostType postType);
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(String memberId, Long postId, PostType postType);


    //ky
    public SuccessResponseDto<List<BookmarkContentsResponseDto>> readMyBookmark(String memberId, Integer page, Integer size, String postType, String sortType)
            throws JsonProcessingException;
}
