package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.UsableConst;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectBookmarkStrategy implements BookmarkStrategy {

    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;

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
        List<ProjectBookmark> bookmarks = projectBookmarkRepository.findByMemberId(memberId, pageRequest);
        List<BookmarkContentsResponseDto> bookmarkDto = bookmarks.stream()
                .map(bookmark -> BookmarkContentsResponseDto.builder()
                        .bookmark(bookmark)
                        .postId(bookmark.getProject().getId())
                        .postType(UsableConst.typeIsName(bookmark.getProject()))
                        .build()).toList();
        return bookmarkDto;
    }
}
