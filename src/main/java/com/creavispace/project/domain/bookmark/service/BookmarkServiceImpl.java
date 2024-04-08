package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.entity.Bookmark;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final RecruitRepository recruitRepository;
    private final CommunityRepository communityRepository;

    @Override
    @Transactional
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(Long memberId, Long postId, PostType postType) {
        BookmarkResponseDto data= null;
        String message;
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, memberId);

                if(projectBookmark == null){
                    ProjectBookmark saveBookmark = ProjectBookmark.builder()
                        .member(member)
                        .project(project)
                            .contentsCreatedDate(project.getCreatedDate())
                        .build();
                    projectBookmarkRepository.save(saveBookmark);
                    message = "북마크 등록이 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }else{
                    projectBookmarkRepository.deleteById(projectBookmark.getId());
                    message = "북마크 취소가 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }
                break;
            case "RECRUIT":
                Recruit recruit = recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
                RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, memberId);

                if(recruitBookmark == null){
                    RecruitBookmark saveBookmark = RecruitBookmark.builder()
                        .member(member)
                        .recruit(recruit)
                            .contentsCreatedDate(recruit.getCreatedDate())
                        .build();
                    recruitBookmarkRepository.save(saveBookmark);
                    message = "북마크 등록이 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }else{
                    recruitBookmarkRepository.deleteById(recruitBookmark.getId());
                    message = "북마크 취소가 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }
                break;
            case "COMMUNITY":
                Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, memberId);

                if(communityBookmark == null){
                    CommunityBookmark saveBookmark = CommunityBookmark.builder()
                        .member(member)
                        .community(community)
                        .contentsCreatedDate(community.getCreatedDate())
                        .build();
                    communityBookmarkRepository.save(saveBookmark);
                    message = "북마크 등록이 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }else{
                    communityBookmarkRepository.deleteById(communityBookmark.getId());
                    message = "북마크 취소가 완료되었습니다.";
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }
                break;
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }
        log.info("/comment/service : bookmarkToggle success data = {}", data);
        return new SuccessResponseDto<BookmarkResponseDto>(true, message, data);
    }

    @Override
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(Long memberId, Long postId, PostType postType) {
        BookmarkResponseDto data;

        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

                ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, memberId);
                if(projectBookmark == null){
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }else{
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }
                break;
        
            case "RECRUIT":
                recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

                RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, memberId);
                if(recruitBookmark == null){
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }else{
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }
                break;
        
            case "COMMUNITY":
                communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

                CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, memberId);
                if(communityBookmark == null){
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }else{
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

      log.info("/comment/service : readBookmark success data = {}", data);
        return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<Bookmark>> readMyBookmark(Long memberId, Integer page, Integer size, String postType, String sortType) {
        Pageable pageRequest = pageable(page, size, sortType);
        List<Bookmark> bookmarks;
        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.toLowerCase()) {
            case "project":
                List<ProjectBookmark> projectBookmark = null;
                    projectBookmark = projectBookmarkRepository.findByMemberId(memberId, pageRequest);
                bookmarks = new ArrayList<>(projectBookmark);
                break;

            case "recruit":
                List<RecruitBookmark> recruitBookmark = null;
                    recruitBookmark = recruitBookmarkRepository.findByMemberId(memberId, pageRequest);
                bookmarks = new ArrayList<>(recruitBookmark);
                break;

            case "community":
                List<CommunityBookmark> communityBookmark = null;
                    communityBookmark = communityBookmarkRepository.findByMemberId(memberId, pageRequest);

                bookmarks = new ArrayList<>(communityBookmark);
                break;

            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", bookmarks);
    }

    private static PageRequest pageable(Integer page, Integer size, String sortType) {
        if (sortType.equalsIgnoreCase("asc")) {
            return PageRequest.of(page - 1, size, Sort.by("contentsCreatedDate").ascending());
        }
        if (sortType.equalsIgnoreCase("desc")) {
            return PageRequest.of(page - 1, size, Sort.by("contentsCreatedDate").descending());
        }
        return PageRequest.of(page - 1, size, Sort.by("contentsCreatedDate").descending());
    }
}