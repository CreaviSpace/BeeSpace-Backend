package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.UsableConst;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitBookmarkStrategy implements BookmarkStrategy {

    private final RecruitRepository recruitRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    @Override
    public BookmarkResponseDto bookmarkToggle(Long postId, Member member) {
        BookmarkResponseDto data = null;
        Recruit recruit = recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
        RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, member.getId());

        if(recruitBookmark == null){
            RecruitBookmark saveBookmark = RecruitBookmark.builder()
                .member(member)
                .recruit(recruit)
                    .contentsCreatedDate(recruit.getCreatedDate())
                .build();
            recruitBookmarkRepository.save(saveBookmark);
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }else{
            recruitBookmarkRepository.deleteById(recruitBookmark.getId());
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }

        return data;
    }

    @Override
    public BookmarkResponseDto readBookmark(Long postId, String memberId) {
        BookmarkResponseDto data = null;
        recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, memberId);
        if(recruitBookmark == null){
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }else{
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }
        return data;
    }

    @Override
    public List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest) {
        List<RecruitBookmark> bookmarks = recruitBookmarkRepository.findByMemberId(memberId, pageRequest);
        List<BookmarkContentsResponseDto> bookmarkDto = bookmarks.stream()
                .map(bookmark -> BookmarkContentsResponseDto.builder()
                        .bookmark(bookmark)
                        .postId(bookmark.getRecruit().getId())
                        .postType(UsableConst.typeIsName(bookmark.getRecruit()))
                        .build()).toList();
        return bookmarkDto;
    }
}
