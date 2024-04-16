package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.UsableConst;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityBookmarkStrategy implements BookmarkStrategy {

    private final CommunityRepository communityRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;

    @Override
    public BookmarkResponseDto bookmarkToggle(Long postId, Member member) {
        BookmarkResponseDto data = null;
        Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
        CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, member.getId());

        if(communityBookmark == null){
            CommunityBookmark saveBookmark = CommunityBookmark.builder()
                .member(member)
                .community(community)
                .contentsCreatedDate(community.getCreatedDate())
                .build();
            communityBookmarkRepository.save(saveBookmark);
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }else{
            communityBookmarkRepository.deleteById(communityBookmark.getId());
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }
        return data;
    }

    @Override
    public BookmarkResponseDto readBookmark(Long postId, String memberId) {
        BookmarkResponseDto data = null;
        communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, memberId);
        if(communityBookmark == null){
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }else{
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }
        return data;
    }

    @Override
    public List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest) {
        List<CommunityBookmark> bookmarks = communityBookmarkRepository.findByMemberId(memberId, pageRequest);
        List<BookmarkContentsResponseDto> bookmarkDto = bookmarks.stream()
                .map(bookmark -> BookmarkContentsResponseDto.builder()
                        .bookmark(bookmark)
                        .postId(bookmark.getCommunity().getId())
                        .postType(UsableConst.typeIsName(bookmark.getCommunity()))
                        .build()).toList();
        return bookmarkDto;
    }
}
