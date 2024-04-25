package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.comment.entity.CommunityComment;
import com.creavispace.project.domain.comment.repository.CommunityCommentRepository;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
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
public class CommunityCommentStrategy implements CommentStrategy{

    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository communityCommentRepository;

    @Override
    public List<CommentResponseDto> readCommentList(Long postId) {
        communityRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        List<CommunityComment> communityComments = communityCommentRepository.findByCommunityId(postId);

        return communityComments.stream()
                .map(communityComment -> CommentResponseDto.builder()
                        .id(communityComment.getId())
                        .memberId(communityComment.getMember().getId())
                        .memberNickName(communityComment.getMember().getMemberNickname())
                        .memberProfileUrl(communityComment.getMember().getProfileUrl())
                        .modifiedDate(communityComment.getModifiedDate())
                        .content(communityComment.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(Long postId, Member member, String content) {
        Optional<Community> optionalCommunity = communityRepository.findByIdAndStatusTrue(postId);
        Community community = optionalCommunity.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        CommunityComment communityComment = CommunityComment.builder()
                .community(community)
                .member(member)
                .content(content)
                .build();

        communityCommentRepository.save(communityComment);

        return CommentResponseDto.builder()
                .id(communityComment.getId())
                .memberId(communityComment.getMember().getId())
                .memberNickName(communityComment.getMember().getMemberNickname())
                .memberProfileUrl(communityComment.getMember().getProfileUrl())
                .modifiedDate(communityComment.getModifiedDate())
                .content(communityComment.getContent())
                .build();
    }

    @Override
    public CommentResponseDto modifyComment(Member member, Long commentId, CommentRequestDto dto) {
        CommunityComment communityComment = communityCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(communityComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        communityComment.modify(dto);
        communityCommentRepository.save(communityComment);

        return CommentResponseDto.builder()
                .id(communityComment.getId())
                .memberId(communityComment.getMember().getId())
                .memberNickName(communityComment.getMember().getMemberNickname())
                .memberProfileUrl(communityComment.getMember().getProfileUrl())
                .modifiedDate(communityComment.getModifiedDate())
                .content(communityComment.getContent())
                .build();
    }

    @Override
    public void deleteComment(Member member, Long commentId) {
        CommunityComment communityComment = communityCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if(!member.getId().equals(communityComment.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        communityCommentRepository.deleteById(commentId);
    }

    @Override
    public List<MyPageCommentResponseDto> readMyContentsList(String memberId, Pageable pageRequest) {
        List<CommunityComment> communityComments = communityCommentRepository.findByMemberId(memberId, pageRequest);

        return communityComments.stream()
                .map(communityComment -> MyPageCommentResponseDto.builder()
                        .contentsTitle(communityComment.getCommunity().getTitle())
                        .memberId(communityComment.getMember().getId())
                        .memberNickName(communityComment.getMember().getMemberNickname())
                        .memberProfileUrl(communityComment.getMember().getProfileUrl())
                        .modifiedDate(communityComment.getModifiedDate())
                        .content(communityComment.getContent())
                        .id(communityComment.getCommunity().getId())
                        .postType(UsableConst.typeIsName(communityComment.getCommunity()))
                        .build())
                .collect(Collectors.toList());
    }
}
