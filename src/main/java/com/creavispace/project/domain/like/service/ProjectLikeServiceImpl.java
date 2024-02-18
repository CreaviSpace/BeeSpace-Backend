package com.creavispace.project.domain.like.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.SuccessResponseDto;
import com.creavispace.project.domain.like.dto.response.ProjectLikeResponseDto;
import com.creavispace.project.domain.like.entity.ProjectLike;
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
public class ProjectLikeServiceImpl implements ProjectLikeService{

    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    
    @Override
    @Transactional
    public SuccessResponseDto<ProjectLikeResponseDto> projectLike(Long projectId) {
        // todo : JWT 토큰의 member정보 사용 예정
        Long memberId =1L;

        // 회원 ID로 회원을 찾음
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        Optional<Project> optionalProject = projectRepository.findById(projectId);

        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        ProjectLike projectLike = projectLikeRepository.findByProjectIdAndMemberId(projectId, memberId);

        if(projectLike == null){
            ProjectLike saveLike = ProjectLike.builder()
                .member(member)
                .project(project)
                .build();
            projectLikeRepository.save(saveLike);
            return new SuccessResponseDto<ProjectLikeResponseDto>(true, "좋아요 등록이 완료되었습니다.",new ProjectLikeResponseDto(true));
        }else{
            Long projectLikeId = projectLike.getId();
            projectLikeRepository.deleteById(projectLikeId);
            return new SuccessResponseDto<ProjectLikeResponseDto>(true, "좋아요 취소가 완료되었습니다.",new ProjectLikeResponseDto(false));
        }
    }
    
}
