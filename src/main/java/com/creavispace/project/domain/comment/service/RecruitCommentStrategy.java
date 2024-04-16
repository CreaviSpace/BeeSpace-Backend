package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.comment.entity.RecruitComment;
import com.creavispace.project.domain.comment.repository.RecruitCommentRepository;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
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
public class RecruitCommentStrategy implements CommentStrategy{

    private final RecruitRepository recruitRepository;
    private final RecruitCommentRepository recruitCommentRepository;

    @Override
    public List<CommentResponseDto> readCommentList(Long postId) {
        recruitRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        List<RecruitComment> recruitComments = recruitCommentRepository.findByRecruitId(postId);

        return recruitComments.stream()
                .map(recruitComment -> CommentResponseDto.builder()
                        .id(recruitComment.getId())
                        .memberId(recruitComment.getMember().getId())
                        .memberNickName(recruitComment.getMember().getMemberNickname())
                        .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                        .modifiedDate(recruitComment.getModifiedDate())
                        .content(recruitComment.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(Long postId, Member member, String content) {
        Optional<Recruit> optionalRecruit = recruitRepository.findByIdAndStatusTrue(postId);
        Recruit recruit = optionalRecruit.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        RecruitComment recruitComment = RecruitComment.builder()
                .recruit(recruit)
                .member(member)
                .content(content)
                .build();

        recruitCommentRepository.save(recruitComment);

        return CommentResponseDto.builder()
                .id(recruitComment.getId())
                .memberId(recruitComment.getMember().getId())
                .memberNickName(recruitComment.getMember().getMemberNickname())
                .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                .modifiedDate(recruitComment.getModifiedDate())
                .content(recruitComment.getContent())
                .build();
    }

    @Override
    public CommentResponseDto modifyComment(Member member, Long commentId, CommentRequestDto dto) {
        RecruitComment recruitComment = recruitCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(recruitComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        recruitComment.modify(dto);
        recruitCommentRepository.save(recruitComment);

        return CommentResponseDto.builder()
                .id(recruitComment.getId())
                .memberId(recruitComment.getMember().getId())
                .memberNickName(recruitComment.getMember().getMemberNickname())
                .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                .modifiedDate(recruitComment.getModifiedDate())
                .content(recruitComment.getContent())
                .build();
    }

    @Override
    public void deleteComment(Member member, Long commentId) {
        RecruitComment recruitComment = recruitCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(recruitComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        recruitCommentRepository.deleteById(commentId);
    }

    @Override
    public List<MyPageCommentResponseDto> readMyContentsList(String memberId, Pageable pageRequest) {
        List<RecruitComment> recruitComments = recruitCommentRepository.findByMemberId(memberId, pageRequest);

        return recruitComments.stream()
                .map(recruitComment -> MyPageCommentResponseDto.builder()
                        .contentsTitle(recruitComment.getRecruit().getTitle())
                        .id(recruitComment.getId())
                        .memberId(recruitComment.getMember().getId())
                        .memberNickName(recruitComment.getMember().getMemberNickname())
                        .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                        .modifiedDate(recruitComment.getModifiedDate())
                        .content(recruitComment.getContent())
                        .postId(recruitComment.getRecruit().getId())
                        .postType(UsableConst.typeIsName(recruitComment.getRecruit()))
                        .build())
                .collect(Collectors.toList());
    }
}
