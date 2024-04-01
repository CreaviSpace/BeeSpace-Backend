package com.creavispace.project.domain.bookmark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.CommunityBookmark;
import com.creavispace.project.domain.bookmark.entity.ProjectBookmark;
import com.creavispace.project.domain.bookmark.entity.RecruitBookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
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
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(Long memberId, Long postId, String postType) {
        BookmarkResponseDto data;
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType) {
            case "project":
                Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, memberId);

                if(projectBookmark == null){
                    ProjectBookmark saveBookmark = ProjectBookmark.builder()
                        .member(member)
                        .project(project)
                        .build();
                    projectBookmarkRepository.save(saveBookmark);
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 등록이 완료되었습니다.", data);
                }else{
                    projectBookmarkRepository.deleteById(projectBookmark.getId());
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 취소가 완료되었습니다.", data);
                }
            case "recruit":
                Recruit recruit = recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
                RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, memberId);

                if(recruitBookmark == null){
                    RecruitBookmark saveBookmark = RecruitBookmark.builder()
                        .member(member)
                        .recruit(recruit)
                        .build();
                    recruitBookmarkRepository.save(saveBookmark);
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 등록이 완료되었습니다.", data);
                }else{
                    recruitBookmarkRepository.deleteById(recruitBookmark.getId());
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 취소가 완료되었습니다.", data);
                }
        
            case "community":
                Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                CommunityBookmark communityBookmark = communityBookmarkRepository.findByCommunityIdAndMemberId(postId, memberId);

                if(communityBookmark == null){
                    CommunityBookmark saveBookmark = CommunityBookmark.builder()
                        .member(member)
                        .community(community)
                        .build();
                    communityBookmarkRepository.save(saveBookmark);
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 등록이 완료되었습니다.", data);
                }else{
                    communityBookmarkRepository.deleteById(communityBookmark.getId());
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                    return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 취소가 완료되었습니다.", data);
                }
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }
    }

    @Override
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(Long memberId, Long postId, String postType) {
        BookmarkResponseDto data;

        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType) {
            case "project":
                projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

                ProjectBookmark projectBookmark = projectBookmarkRepository.findByProjectIdAndMemberId(postId, memberId);
                if(projectBookmark == null){
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }else{
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }
                break;
        
            case "recruit":
                recruitRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

                RecruitBookmark recruitBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(postId, memberId);
                if(recruitBookmark == null){
                    data = BookmarkResponseDto.builder().bookmarkStatus(false).build();
                }else{
                    data = BookmarkResponseDto.builder().bookmarkStatus(true).build();
                }
                break;
        
            case "community":
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
        
        return new SuccessResponseDto<BookmarkResponseDto>(true, "북마크 조회가 완료되었습니다.", data);
    }
    
}
