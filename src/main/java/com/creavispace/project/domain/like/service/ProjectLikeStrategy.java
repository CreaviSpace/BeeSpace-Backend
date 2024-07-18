package com.creavispace.project.domain.like.service;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.entity.ProjectLike;
import com.creavispace.project.domain.like.repository.ProjectLikeRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.project.entity.Project;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ProjectLikeStrategy implements LikeStrategy {

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;


    @Override
    public LikeResponseDto likeToggle(Member member, Long postId) {
        LikeResponseDto data = null;
        Project project = projectRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("프로젝트 id("+postId+")가 존재하지 않습니다."));

        if(!project.getStatus().equals(Post.Status.PUBLIC)) throw new CreaviCodeException(GlobalErrorCode.NOT_PUBLIC_CONTENT);

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
        projectRepository.findById(postId).orElseThrow(()-> new NoSuchElementException("프로젝트 id("+postId+")가 존재하지 않습니다."));

        boolean isProjectLike = projectLikeRepository.existsByProjectIdAndMemberId(postId, memberId);

        return new LikeResponseDto(isProjectLike);
    }

    @Override
    public LikeCountResponseDto likeCount(Long postId) {
        return new LikeCountResponseDto(projectLikeRepository.countByProjectId(postId));
    }
}
