package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkStrategyFactory bookmarkStrategyFactory;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(String memberId, Long postId, String postType) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
        BookmarkStrategy strategy = bookmarkStrategyFactory.getStrategy(PostType.valueOf(postType.toUpperCase()));
        BookmarkResponseDto data = strategy.bookmarkToggle(postId, member);
        log.info("/comment/service : bookmarkToggle success data = {}", data);
        return new SuccessResponseDto<>(true, "북마크 토글 기능이 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(String memberId, Long postId, String postType) {
        BookmarkStrategy strategy = bookmarkStrategyFactory.getStrategy(PostType.valueOf(postType.toUpperCase()));
        BookmarkResponseDto data = strategy.readBookmark(postId, memberId);
        log.info("/comment/service : readBookmark success data = {}", data);
        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", data);
    }

//    @Override
//    public SuccessResponseDto<List<BookmarkContentsResponseDto>> readMyBookmark(String memberId, Integer page, Integer size, String postType, String sortType) {
//        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
//        BookmarkStrategy strategy = bookmarkStrategyFactory.getStrategy(PostType.valueOf(postType.toUpperCase()));
//        List<BookmarkContentsResponseDto> data = strategy.readMyBookmark(memberId, pageRequest);
//
//        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", data);
//    }
}