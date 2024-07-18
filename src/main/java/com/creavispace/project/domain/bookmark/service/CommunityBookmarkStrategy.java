package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommunityBookmarkStrategy implements BookmarkStrategy {

    private final CommunityRepository communityRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;

    @Override
    public BookmarkResponseDto bookmarkToggle(Long postId, Member member) {
        BookmarkResponseDto data = null;
        Community community = communityRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("커뮤니티 id("+postId+")가 존재하지 않습니다."));

        if(!community.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException(GlobalErrorCode.NOT_PUBLIC_CONTENT);

        Optional<CommunityBookmark> communityBookmarkOptional = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, member.getId());

        if(communityBookmarkOptional.isEmpty()){
            CommunityBookmark communityBookmark = CommunityBookmark.builder()
                .member(member)
                .community(community)
                .build();
            communityBookmarkRepository.save(communityBookmark);
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }else{
            communityBookmarkRepository.deleteById(communityBookmarkOptional.get().getId());
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }
        return data;
    }

    @Override
    public BookmarkResponseDto readBookmark(Long postId, String memberId) {
        BookmarkResponseDto data = null;
        communityRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("커뮤니티 id("+postId+")가 존재하지 않습니다."));

        boolean isBookmark = communityBookmarkRepository.existsByCommunityIdAndMemberId(postId, memberId);
        return new BookmarkResponseDto(isBookmark);
    }

//    @Override
//    public List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest) {
//        System.out.println("CommunityBookmarkStrategy.readMyBookmark");
//        List<CommunityBookmark> communityBookmarks = communityBookmarkRepository.findByMemberIdAndEnableTrue(memberId, pageRequest);
//        List<BookmarkContentsResponseDto> bookmarks = new ArrayList<>();
//        List<Long> communityIds = communityBookmarks.stream()
//                .filter(bookmark -> bookmark.getCommunity().isStatus())
//                .map(bookmark -> bookmark.getCommunity().getId()).toList();
//        List<CommunityHashTag> communityHashTags = communityHashTagRepository.findByCommunityIdIn(communityIds);
//        List<Community> communities = communityRepository.findByIdIn(communityIds);
//
//        Map<Long, List<CommunityHashTagDto>> hashTags = hashTags(communityHashTags);
//        for (int i = 0; i < communityBookmarks.size(); i++) {
//            CommunityBookmark bookmark = communityBookmarks.get(i);
//            CommunityBookmarkResponseDto build = CommunityBookmarkResponseDto.builder()
//                    .id(bookmark.getCommunity().getId())
//                    .postType(PostType.COMMUNITY.name())
//                    .category(bookmark.getCommunity().getCategory().name())
//                    .title(bookmark.getCommunity().getTitle())
//                    .content(bookmark.getCommunity().getContent())
//                    .viewCount(bookmark.getCommunity().getViewCount())
//                    .createdDate(bookmark.getCommunity().getCreatedDate())
//                    .modifiedDate(bookmark.getCommunity().getModifiedDate())
//                    .hashTags(hashTags.get(bookmark.getCommunity().getId()))
//                    .build();
//            bookmarks.add(build);
//        }
//        return bookmarks;
//    }

//    private Map<Long, List<CommunityHashTagDto>> hashTags(List<CommunityHashTag> communityHashTags) {
//        System.out.println("CommunityServiceImpl.hashTags");
//        Map<Long, List<CommunityHashTagDto>> hashTags = new HashMap<>();
//        List<String> collect = communityHashTags.stream().map(hashTag -> hashTag.getHashTag().getHashTag()).distinct()
//                .toList();
//        hashTagRepository.findByHashTagIn(collect);
//        for (int i = 0; i < communityHashTags.size(); i++) {
//            CommunityHashTag communityHashTag = communityHashTags.get(i);
//            List<CommunityHashTagDto> hashTag = hashTags.getOrDefault(communityHashTag.getCommunity().getId(),
//                    new ArrayList<>());
//            CommunityHashTagDto communityHashTagDto = CommunityHashTagDto.builder()
//                    .hashTag(communityHashTag.getHashTag().getHashTag())
//                    .build();
//            hashTag.add(communityHashTagDto);
//            hashTags.put(communityHashTag.getCommunity().getId(), hashTag);
//        }
//        return hashTags;
//    }

}
