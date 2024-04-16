package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.comment.entity.ProjectComment;
import com.creavispace.project.domain.comment.repository.ProjectCommentRepository;
import com.creavispace.project.domain.member.Role;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectCommentStrategy implements CommentStrategy {

    private final ProjectRepository projectRepository;
    private final ProjectCommentRepository projectCommentRepository;

    @Override
    public List<CommentResponseDto> readCommentList(Long postId) {

        projectRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        List<ProjectComment> projectComments = projectCommentRepository.findByProjectId(postId);

        return projectComments.stream()
                .map(projectComment -> CommentResponseDto.builder()
                        .id(projectComment.getId())
                        .memberId(projectComment.getMember().getId())
                        .memberNickName(projectComment.getMember().getMemberNickname())
                        .memberProfileUrl(projectComment.getMember().getProfileUrl())
                        .modifiedDate(projectComment.getModifiedDate())
                        .content(projectComment.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(Long postId, Member member, String content) {
        Optional<Project> optionalProject = projectRepository.findByIdAndStatusTrue(postId);
        Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

        ProjectComment projectComment = ProjectComment.builder()
                .project(project)
                .member(member)
                .content(content)
                .build();

        projectCommentRepository.save(projectComment);

        return CommentResponseDto.builder()
                .id(projectComment.getId())
                .memberId(projectComment.getMember().getId())
                .memberNickName(projectComment.getMember().getMemberNickname())
                .memberProfileUrl(projectComment.getMember().getProfileUrl())
                .modifiedDate(projectComment.getModifiedDate())
                .content(projectComment.getContent())
                .build();
    }

    @Override
    public CommentResponseDto modifyComment(Member member, Long commentId, CommentRequestDto dto) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(projectComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        projectComment.modify(dto);
        projectCommentRepository.save(projectComment);

        return CommentResponseDto.builder()
                .id(projectComment.getId())
                .memberId(projectComment.getMember().getId())
                .memberNickName(projectComment.getMember().getMemberNickname())
                .memberProfileUrl(projectComment.getMember().getProfileUrl())
                .modifiedDate(projectComment.getModifiedDate())
                .content(projectComment.getContent())
                .build();
    }

    @Override
    public void deleteComment(Member member, Long commentId) {
        ProjectComment projectComment = projectCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(projectComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        projectCommentRepository.deleteById(commentId);
    }

    @Override
    public List<MyPageCommentResponseDto> readMyContentsList(String memberId, Pageable pageRequest) {
        List<ProjectComment> projectComments = projectCommentRepository.findByMemberId(memberId, pageRequest);

        return projectComments.stream()
                .map(projectComment -> MyPageCommentResponseDto.builder()
                        .contentsTitle(projectComment.getProject().getTitle())
                        .id(projectComment.getId())
                        .memberId(projectComment.getMember().getId())
                        .memberNickName(projectComment.getMember().getMemberNickname())
                        .memberProfileUrl(projectComment.getMember().getProfileUrl())
                        .modifiedDate(projectComment.getModifiedDate())
                        .content(projectComment.getContent())
                        .postId(projectComment.getProject().getId())
                        .postType(UsableConst.typeIsName(projectComment.getProject()))
                        .build())
                .collect(Collectors.toList());
    }
}
