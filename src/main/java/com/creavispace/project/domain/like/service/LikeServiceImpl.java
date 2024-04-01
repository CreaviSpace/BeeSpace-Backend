package com.creavispace.project.domain.like.service;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
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
    public SuccessResponseDto<LikeResponseDto> likeToggle(Long memberId, Long postId, String postType) {
        LikeResponseDto data;
        String message;
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType) {
            case "project":
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
            case "community":
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

        return new SuccessResponseDto<>(true, message, data);
    }

    @Override
    public SuccessResponseDto<LikeResponseDto> readLike(Long memberId, Long postId, String postType) {
        LikeResponseDto data;
        memberRepository.findById(memberId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType) {
            case "project":
                projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                boolean isProjectLike = projectLikeRepository.existsByProjectIdAndMemberId(postId, memberId);
                data = new LikeResponseDto(isProjectLike);
                break;
        
            case "community":
                communityRepository.findByIdAndStatusTrue(postId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                boolean isCommunityLike = communityLikeRepository.existsByCommunityIdAndMemberId(postId, memberId);
                data = new LikeResponseDto(isCommunityLike);
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        return new SuccessResponseDto<>(true, "좋아요 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<LikeCountResponseDto> likeCount(Long postId, String postType) {
        int likeCount;
        switch (postType) {
            case "project":
                likeCount = projectLikeRepository.countByProjectId(postId);
                break;
        
            case "community":
                likeCount = communityLikeRepository.countByCommunityId(postId);
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        return new SuccessResponseDto<>(Boolean.TRUE, "좋아요 수 조회가 완료되었습니다.", new LikeCountResponseDto(likeCount));
    }
    
}
