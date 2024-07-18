package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecruitBookmarkStrategy implements BookmarkStrategy {

    private final RecruitRepository recruitRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final TechStackRepository techStackRepository;
    @Override
    public BookmarkResponseDto bookmarkToggle(Long postId, Member member) {

        BookmarkResponseDto data = null;

        Recruit recruit = recruitRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("모집 id("+postId+")가 존재하지 않습니다."));

        if(!recruit.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException(GlobalErrorCode.NOT_PUBLIC_CONTENT);

        Optional<RecruitBookmark> recruitBookmarkOptional = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, member.getId());

        if(recruitBookmarkOptional.isEmpty()){
            RecruitBookmark saveBookmark = RecruitBookmark.builder()
                .member(member)
                .recruit(recruit)
                .build();

            recruitBookmarkRepository.save(saveBookmark);

            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }else{
            recruitBookmarkRepository.deleteById(recruitBookmarkOptional.get().getId());

            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }

        return data;
    }

    @Override
    public BookmarkResponseDto readBookmark(Long postId, String memberId) {

        BookmarkResponseDto data = null;

        recruitRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("모집 id("+postId+")가 존재하지 않습니다."));

        boolean isRecruit = recruitBookmarkRepository.existsByRecruitIdAndMemberId(postId, memberId);

        return new BookmarkResponseDto(isRecruit);
    }

//    @Override
//    public List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest) {
//        System.out.println("RecruitBookmarkStrategy.readMyBookmark");
//        List<RecruitBookmark> recruitBookmarks = recruitBookmarkRepository.findByMemberIdAndEnableTrue(memberId, pageRequest);
//        List<Long> recruitIds = recruitBookmarks.stream()
//                .filter(bookmark -> bookmark.getRecruit().getStatus())
//                .map(bookmarks -> bookmarks.getRecruit().getId()).toList();
//        List<RecruitTechStack> recruitTechStacks = recruitTechStackRepository.findByRecruitIdIn(recruitIds);
//        List<Recruit> recruits = recruitRepository.findByIdIn(recruitIds);
//
//        Map<Long, List<RecruitTechStackResponseDto>> techStacks = techStacks(recruitTechStacks);
//
//        List<BookmarkContentsResponseDto> bookmarks = new ArrayList<>();
//        for (int i = 0; i < recruitBookmarks.size(); i++) {
//            RecruitBookmark bookmark = recruitBookmarks.get(i);
//            RecruitBookmarkResponseDto build = RecruitBookmarkResponseDto.builder()
//                    .techStacks(techStacks.get(bookmark.getRecruit().getId()))
//                    .id(bookmark.getRecruit().getId())
//                    .postType(PostType.RECRUIT.name())
//                    .category(bookmark.getRecruit().getCategory().name())
//                    .title(bookmark.getRecruit().getTitle())
//                    .content(bookmark.getRecruit().getContent())
//                    .viewCount(bookmark.getRecruit().getViewCount())
//                    .createdDate(bookmark.getRecruit().getCreatedDate())
//                    .modifiedDate(bookmark.getRecruit().getModifiedDate())
//                    .build();
//            bookmarks.add(build);
//        }
//        return bookmarks;
//    }

//    private Map<Long, List<RecruitTechStackResponseDto>> techStacks(List<RecruitTechStack> recruitTechStacks) {
//        Map<Long, List<RecruitTechStackResponseDto>> techStackMap = new HashMap<>();
//        List<String> collect = recruitTechStacks.stream().map(techStack -> techStack.getTechStack().getTechStack())
//                .distinct().toList();
//        techStackRepository.findByTechStackIn(collect);
//        for (int i = 0; i < recruitTechStacks.size(); i++) {
//            RecruitTechStack recruitTechStack = recruitTechStacks.get(i);
//            List<RecruitTechStackResponseDto> links = techStackMap.getOrDefault(recruitTechStack.getRecruit().getId(),
//                    new ArrayList<>());
//            RecruitTechStackResponseDto recruitTechStackResponseDto = RecruitTechStackResponseDto.builder()
//                    .techStack(recruitTechStack.getTechStack().getTechStack())
//                    .iconUrl(recruitTechStack.getTechStack().getIconUrl())
//                    .build();
//            links.add(recruitTechStackResponseDto);
//            techStackMap.put(recruitTechStack.getRecruit().getId(), links);
//        }
//        return techStackMap;
//    }
}
