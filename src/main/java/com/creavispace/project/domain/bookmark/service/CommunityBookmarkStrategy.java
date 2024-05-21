package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.CommunityBookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTag;
import com.creavispace.project.domain.hashTag.repository.CommunityHashTagRepository;
import com.creavispace.project.domain.hashTag.repository.HashTagRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommunityBookmarkStrategy implements BookmarkStrategy {

    private final CommunityRepository communityRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;
    private final HashTagRepository hashTagRepository;
    private final CommunityHashTagRepository communityHashTagRepository;

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
                    .enable(true)
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
        System.out.println("CommunityBookmarkStrategy.readMyBookmark");
        List<CommunityBookmark> communityBookmarks = communityBookmarkRepository.findByMemberIdAndEnableTrue(memberId, pageRequest);
        List<BookmarkContentsResponseDto> bookmarks = new ArrayList<>();
        List<Long> communityIds = communityBookmarks.stream()
                .filter(bookmark -> bookmark.getCommunity().isStatus())
                .map(bookmark -> bookmark.getCommunity().getId()).toList();
        List<CommunityHashTag> communityHashTags = communityHashTagRepository.findByCommunityIdIn(communityIds);
        List<Community> communities = communityRepository.findByIdIn(communityIds);

        Map<Long, List<CommunityHashTagDto>> hashTags = hashTags(communityHashTags);
        for (int i = 0; i < communityBookmarks.size(); i++) {
            CommunityBookmark bookmark = communityBookmarks.get(i);
            CommunityBookmarkResponseDto build = CommunityBookmarkResponseDto.builder()
                    .id(bookmark.getCommunity().getId())
                    .postType(PostType.COMMUNITY.name())
                    .category(bookmark.getCommunity().getCategory().name())
                    .title(bookmark.getCommunity().getTitle())
                    .content(bookmark.getCommunity().getContent())
                    .viewCount(bookmark.getCommunity().getViewCount())
                    .createdDate(bookmark.getCommunity().getCreatedDate())
                    .modifiedDate(bookmark.getCommunity().getModifiedDate())
                    .hashTags(hashTags.get(bookmark.getCommunity().getId()))
                    .build();
            bookmarks.add(build);
        }
        return bookmarks;
    }

    private Map<Long, List<CommunityHashTagDto>> hashTags(List<CommunityHashTag> communityHashTags) {
        System.out.println("CommunityServiceImpl.hashTags");
        Map<Long, List<CommunityHashTagDto>> hashTags = new HashMap<>();
        List<String> collect = communityHashTags.stream().map(hashTag -> hashTag.getHashTag().getHashTag()).distinct()
                .toList();
        hashTagRepository.findByHashTagIn(collect);
        for (int i = 0; i < communityHashTags.size(); i++) {
            CommunityHashTag communityHashTag = communityHashTags.get(i);
            List<CommunityHashTagDto> hashTag = hashTags.getOrDefault(communityHashTag.getCommunity().getId(),
                    new ArrayList<>());
            CommunityHashTagDto communityHashTagDto = CommunityHashTagDto.builder()
                    .hashTag(communityHashTag.getHashTag().getHashTag())
                    .build();
            hashTag.add(communityHashTagDto);
            hashTags.put(communityHashTag.getCommunity().getId(), hashTag);
        }
        return hashTags;
    }

}
