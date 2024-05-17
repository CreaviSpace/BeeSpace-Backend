package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.ProjectBookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.dto.response.ProjectLinkResponseDto;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.entity.ProjectLink;
import com.creavispace.project.domain.project.repository.ProjectLinkRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
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
public class ProjectBookmarkStrategy implements BookmarkStrategy {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ProjectLinkRepository projectLinkRepository;

    @Override
    public BookmarkResponseDto bookmarkToggle(Long postId, Member member) {
        BookmarkResponseDto data = null;
        Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
        ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, member.getId());

        if(projectBookmark == null){
            ProjectBookmark saveBookmark = ProjectBookmark.builder()
                    .member(member)
                    .project(project)
                    .contentsCreatedDate(project.getCreatedDate())
                    .build();
            projectBookmarkRepository.save(saveBookmark);
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }else{
            projectBookmarkRepository.deleteById(projectBookmark.getId());
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }

        return data;
    }

    @Override
    public BookmarkResponseDto readBookmark(Long postId, String memberId) {
        BookmarkResponseDto data = null;
        projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, memberId);
        if(projectBookmark == null){
            data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
        }else{
            data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
        }
        return data;
    }

    @Override
    public List<BookmarkContentsResponseDto> readMyBookmark(String memberId, Pageable pageRequest) {
        System.out.println("ProjectBookmarkStrategy.readMyBookmark");
        List<ProjectBookmark> projectBookmarks = projectBookmarkRepository.findByMemberId(memberId, pageRequest);
        List<Long> projectIds = projectBookmarks.stream()
                .filter(projectBookmark -> projectBookmark.getProject().isStatus())
                .map(projectBookmark -> projectBookmark.getProject().getId())
                .toList();
        projectBookmarkRepository.findByProjectIdIn(projectIds);
        projectRepository.findByIdIn(projectIds);
        List<ProjectLink> projectLinks = projectLinkRepository.findByProjectIdIn(projectIds);
        Map<Long, List<ProjectLinkResponseDto>> links = projectLinks(projectLinks);
        return getBookmarkContentsResponseDtos(
                projectBookmarks, links);
    }

    private static List<BookmarkContentsResponseDto> getBookmarkContentsResponseDtos(
            List<ProjectBookmark> projectBookmarks, Map<Long, List<ProjectLinkResponseDto>> links) {
        List<BookmarkContentsResponseDto> bookmarks = new ArrayList<>();
        for (int i = 0; i < projectBookmarks.size(); i++) {
            ProjectBookmark bookmark = projectBookmarks.get(i);
            ProjectBookmarkResponseDto build = ProjectBookmarkResponseDto.builder()
                    .thumbnail(bookmark.getProject().getThumbnail())
                    .links(links.get(bookmark.getProject().getId()))
                    .id(bookmark.getProject().getId())
                    .postType(PostType.PROJECT.name())
                    .category(bookmark.getProject().getCategory().name())
                    .title(bookmark.getProject().getTitle())
                    .content(bookmark.getProject().getContent())
                    .createdDate(bookmark.getProject().getCreatedDate())
                    .modifiedDate(bookmark.getProject().getModifiedDate())
                    .viewCount(bookmark.getProject().getViewCount())
                    .build();
            bookmarks.add(build);
        }
        return bookmarks;
    }

    private Map<Long, List<ProjectLinkResponseDto>> projectLinks(List<ProjectLink> projectLinks) {
        Map<Long, List<ProjectLinkResponseDto>> linkMap = new HashMap<>();
        for (int i = 0; i < projectLinks.size(); i++) {
            ProjectLink projectLink = projectLinks.get(i);
            List<ProjectLinkResponseDto> links = linkMap.getOrDefault(projectLink.getProject().getId(),
                    new ArrayList<>());
            ProjectLinkResponseDto projectLinkResponseDto = ProjectLinkResponseDto.builder()
                    .linkType(projectLink.getLinkType())
                    .url(projectLink.getUrl())
                    .build();
            links.add(projectLinkResponseDto);
            linkMap.put(projectLink.getProject().getId(), links);
        }
        return linkMap;
    }

}
