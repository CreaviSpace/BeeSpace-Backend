package com.creavispace.project.domain.like.service;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.entity.CommunityLike;
import com.creavispace.project.domain.like.entity.ProjectLike;
import com.creavispace.project.domain.like.repository.CommunityLikeRepository;
import com.creavispace.project.domain.like.repository.ProjectLikeRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final CommunityLikeRepository communityLikeRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final CommunityRepository communityRepository;

    @Override
    @Transactional
    public SuccessResponseDto<LikeResponseDto> likeToggle(String memberId, Long postId, PostType postType) {
        LikeResponseDto data = null;
        String message;

        // 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                
                ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(postId, memberId);

                if(projectLike == null){
                    ProjectLike saveLike = ProjectLike.builder()
                        .member(member)
                        .project(project)
                        .build();
                    projectLikeRepository.save(saveLike);
                    message = "좋아요가 등록되었습니다.";
                    data = new LikeResponseDto(true);
                }else{
                    projectLikeRepository.deleteById(projectLike.getId());
                    message = "좋아요가 취소 되었습니다.";
                    data = new LikeResponseDto(false);
                }
                break;
            case "COMMUNITY":
                Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

                CommunityLike communityLike = communityLikeRepository.findByCommunityIdAndMemberId(postId, memberId);

                if(communityLike == null){
                    CommunityLike saveLike = CommunityLike.builder()
                        .member(member)
                        .community(community)
                        .build();
                    communityLikeRepository.save(saveLike);
                    message = "좋아요가 등록되었습니다.";
                    data = new LikeResponseDto(true);
                }else{
                    communityLikeRepository.deleteById(communityLike.getId());
                    message = "좋아요가 취소 되었습니다.";
                    data = new LikeResponseDto(false);
                }
                break;
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        log.info("/like/service : likeToggle success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, message, data);
    }

    @Override
    public SuccessResponseDto<LikeResponseDto> readLike(String memberId, Long postId, PostType postType) {
        LikeResponseDto data = null;

        // 회원이 존재하는지
        memberRepository.findById(memberId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                boolean isProjectLike = projectLikeRepository.existsByProjectIdAndMemberId(postId, memberId);
                data = new LikeResponseDto(isProjectLike);
                break;
        
            case "COMMUNITY":
                communityRepository.findByIdAndStatusTrue(postId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                boolean isCommunityLike = communityLikeRepository.existsByCommunityIdAndMemberId(postId, memberId);
                data = new LikeResponseDto(isCommunityLike);
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        log.info("/like/service : readLike success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "좋아요 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<LikeCountResponseDto> likeCount(Long postId, PostType postType) {
        LikeCountResponseDto data = null;
        int likeCount = 0;

        switch (postType.name()) {
            case "PROJECT":
                likeCount = projectLikeRepository.countByProjectId(postId);
                break;

            case "COMMUNITY":
                likeCount = communityLikeRepository.countByCommunityId(postId);
                break;

            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        data = new LikeCountResponseDto(likeCount);

        log.info("/like/service : likeCount success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(Boolean.TRUE, "좋아요 수 조회가 완료되었습니다.", data);
    }
    
}
