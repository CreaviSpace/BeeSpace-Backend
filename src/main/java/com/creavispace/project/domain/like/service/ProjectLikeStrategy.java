package com.creavispace.project.domain.like.service;

import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.entity.ProjectLike;
import com.creavispace.project.domain.like.repository.ProjectLikeRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectLikeStrategy implements LikeStrategy {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;


    @Override
    public LikeResponseDto likeToggle(Member member, Long postId) {
        LikeResponseDto data = null;
        Project project = projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(postId, member.getId());

        if(projectLike == null){
            ProjectLike saveLike = ProjectLike.builder()
                    .member(member)
                    .project(project)
                    .build();
            projectLikeRepository.save(saveLike);
            data = new LikeResponseDto(true);
        }else{
            projectLikeRepository.deleteById(projectLike.getId());
            data = new LikeResponseDto(false);
        }
        return data;
    }

    @Override
    public LikeResponseDto readLike(String memberId, Long postId) {
        projectRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
        boolean isProjectLike = projectLikeRepository.existsByProjectIdAndMemberId(postId, memberId);
        return new LikeResponseDto(isProjectLike);
    }

    @Override
    public LikeCountResponseDto likeCount(Long postId) {
        return new LikeCountResponseDto(projectLikeRepository.countByProjectId(postId));
    }
}
