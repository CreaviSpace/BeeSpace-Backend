package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.RecruitBookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.dto.response.RecruitTechStackResponseDto;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.entity.RecruitTechStack;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.recruit.repository.RecruitTechStackRepository;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitBookmarkStrategy implements BookmarkStrategy {

    private final RecruitRepository recruitRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final RecruitTechStackRepository recruitTechStackRepository;
    private final TechStackRepository techStackRepository;
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
                    .enable(true)
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
        System.out.println("RecruitBookmarkStrategy.readMyBookmark");
        List<RecruitBookmark> recruitBookmarks = recruitBookmarkRepository.findByMemberIdAndEnableTrue(memberId, pageRequest);
        List<Long> recruitIds = recruitBookmarks.stream()
                .filter(bookmark -> bookmark.getRecruit().getStatus())
                .map(bookmarks -> bookmarks.getRecruit().getId()).toList();
        List<RecruitTechStack> recruitTechStacks = recruitTechStackRepository.findByRecruitIdIn(recruitIds);
        List<Recruit> recruits = recruitRepository.findByIdIn(recruitIds);

        Map<Long, List<RecruitTechStackResponseDto>> techStacks = techStacks(recruitTechStacks);

        List<BookmarkContentsResponseDto> bookmarks = new ArrayList<>();
        for (int i = 0; i < recruitBookmarks.size(); i++) {
            RecruitBookmark bookmark = recruitBookmarks.get(i);
            RecruitBookmarkResponseDto build = RecruitBookmarkResponseDto.builder()
                    .techStacks(techStacks.get(bookmark.getRecruit().getId()))
                    .id(bookmark.getRecruit().getId())
                    .postType(PostType.RECRUIT.name())
                    .category(bookmark.getRecruit().getCategory().name())
                    .title(bookmark.getRecruit().getTitle())
                    .content(bookmark.getRecruit().getContent())
                    .viewCount(bookmark.getRecruit().getViewCount())
                    .createdDate(bookmark.getRecruit().getCreatedDate())
                    .modifiedDate(bookmark.getRecruit().getModifiedDate())
                    .build();
            bookmarks.add(build);
        }
        return bookmarks;
    }

    private Map<Long, List<RecruitTechStackResponseDto>> techStacks(List<RecruitTechStack> recruitTechStacks) {
        Map<Long, List<RecruitTechStackResponseDto>> techStackMap = new HashMap<>();
        List<String> collect = recruitTechStacks.stream().map(techStack -> techStack.getTechStack().getTechStack())
                .distinct().toList();
        techStackRepository.findByTechStackIn(collect);
        for (int i = 0; i < recruitTechStacks.size(); i++) {
            RecruitTechStack recruitTechStack = recruitTechStacks.get(i);
            List<RecruitTechStackResponseDto> links = techStackMap.getOrDefault(recruitTechStack.getRecruit().getId(),
                    new ArrayList<>());
            RecruitTechStackResponseDto recruitTechStackResponseDto = RecruitTechStackResponseDto.builder()
                    .techStack(recruitTechStack.getTechStack().getTechStack())
                    .iconUrl(recruitTechStack.getTechStack().getIconUrl())
                    .build();
            links.add(recruitTechStackResponseDto);
            techStackMap.put(recruitTechStack.getRecruit().getId(), links);
        }
        return techStackMap;
    }
}
