package com.creavispace.project.domain.comment.service;

import java.awt.Cursor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.entity.CommunityComment;
import com.creavispace.project.domain.comment.entity.ProjectComment;
import com.creavispace.project.domain.comment.entity.RecruitComment;
import com.creavispace.project.domain.comment.repository.CommunityCommentRepository;
import com.creavispace.project.domain.comment.repository.ProjectCommentRepository;
import com.creavispace.project.domain.comment.repository.RecruitCommentRepository;
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
public class CommentServiceImpl implements CommentService {

    private final ProjectCommentRepository projectCommentRepository;
    private final RecruitCommentRepository recruitCommentRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final RecruitRepository recruitRepository;
    private final CommunityRepository communityRepository;

    @Override
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType) {
        
        List<CommentResponseDto> data = null;

        switch (postType.name()) {
            case "PROJECT":
                projectRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
                List<ProjectComment> projectComments = projectCommentRepository.findByProjectId(postId);
                data = projectComments.stream()
                    .map(projectComment -> CommentResponseDto.builder()
                        .id(projectComment.getId())
                        .memberId(projectComment.getMember().getId())
                        .memberNickName(projectComment.getMember().getMemberNickname())
                        .memberProfileUrl(projectComment.getMember().getProfileUrl())
                        .modifiedDate(projectComment.getModifiedDate())
                        .content(projectComment.getContent())
                        .build())
                    .collect(Collectors.toList());
                break;
        
            case "COMMUNITY":
                communityRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
                List<CommunityComment> communityComments = communityCommentRepository.findByCommunityId(postId);
                data = communityComments.stream()
                    .map(communityComment -> CommentResponseDto.builder()
                        .id(communityComment.getId())
                        .memberId(communityComment.getMember().getId())
                        .memberNickName(communityComment.getMember().getMemberNickname())
                        .memberProfileUrl(communityComment.getMember().getProfileUrl())
                        .modifiedDate(communityComment.getModifiedDate())
                        .content(communityComment.getContent())
                        .build())
                    .collect(Collectors.toList());
                break;
                
            case "RECRUIT":
                recruitRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
                List<RecruitComment> recruitComments = recruitCommentRepository.findByRecruitId(postId);
                data = recruitComments.stream()
                    .map(recruitComment -> CommentResponseDto.builder()
                        .id(recruitComment.getId())
                        .memberId(recruitComment.getMember().getId())
                        .memberNickName(recruitComment.getMember().getMemberNickname())
                        .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                        .modifiedDate(recruitComment.getModifiedDate())
                        .content(recruitComment.getContent())
                        .build())
                    .collect(Collectors.toList());
                break;
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }
        log.info("/comment/service : readCommentList success data = {}", data);
        return new SuccessResponseDto<>(false, "댓글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> createComment(Long memberId, Long postId, PostType postType, CommentRequestDto dto) {
        CommentResponseDto data = null;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                Optional<Project> optionalProject = projectRepository.findByIdAndStatusTrue(postId);
                Project project = optionalProject.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));

                ProjectComment projectComment = ProjectComment.builder()
                    .project(project)
                    .member(member)
                    .content(dto.getContent())
                    .build();

                projectCommentRepository.save(projectComment);

                data = CommentResponseDto.builder()
                    .id(projectComment.getId())
                    .memberId(projectComment.getMember().getId())
                    .memberNickName(projectComment.getMember().getMemberNickname())
                    .memberProfileUrl(projectComment.getMember().getProfileUrl())
                    .modifiedDate(projectComment.getModifiedDate())
                    .content(projectComment.getContent())
                    .build();
                break;
        
            case "RECRUIT":
                Optional<Recruit> optionalRecruit = recruitRepository.findByIdAndStatusTrue(postId);
                Recruit recruit = optionalRecruit.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

                RecruitComment recruitComment = RecruitComment.builder()
                    .recruit(recruit)
                    .member(member)
                    .content(dto.getContent())
                    .build();

                recruitCommentRepository.save(recruitComment);

                data = CommentResponseDto.builder()
                    .id(recruitComment.getId())
                    .memberId(recruitComment.getMember().getId())
                    .memberNickName(recruitComment.getMember().getMemberNickname())
                    .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                    .modifiedDate(recruitComment.getModifiedDate())
                    .content(recruitComment.getContent())
                    .build();
                break;
        
            case "COMMUNITY":
                Optional<Community> optionalCommunity = communityRepository.findByIdAndStatusTrue(postId);
                Community community = optionalCommunity.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

                CommunityComment communityComment = CommunityComment.builder()
                    .community(community)
                    .member(member)
                    .content(dto.getContent())
                    .build();

                communityCommentRepository.save(communityComment);

                data = CommentResponseDto.builder()
                    .id(communityComment.getId())
                    .memberId(communityComment.getMember().getId())
                    .memberNickName(communityComment.getMember().getMemberNickname())
                    .memberProfileUrl(communityComment.getMember().getProfileUrl())
                    .modifiedDate(communityComment.getModifiedDate())
                    .content(communityComment.getContent())
                    .build();
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }
        log.info("/comment/service : createComment success data = {}", data);
        return new SuccessResponseDto<CommentResponseDto>(true, "댓글 생성이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> modifyComment(Long memberId, Long commentId, PostType postType, CommentRequestDto dto) {
        CommentResponseDto data = null;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        
        switch (postType.name()) {
            case "PROJECT":
                ProjectComment projectComment = projectCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

                if(memberId != projectComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                projectComment.modify(dto);
                projectCommentRepository.save(projectComment);

                data = CommentResponseDto.builder()
                    .id(projectComment.getId())
                    .memberId(projectComment.getMember().getId())
                    .memberNickName(projectComment.getMember().getMemberNickname())
                    .memberProfileUrl(projectComment.getMember().getProfileUrl())
                    .modifiedDate(projectComment.getModifiedDate())
                    .content(projectComment.getContent())
                    .build();
                break;
        
            case "RECRUIT":
                RecruitComment recruitComment = recruitCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

                if(memberId != recruitComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                recruitComment.modify(dto);
                recruitCommentRepository.save(recruitComment);

                data = CommentResponseDto.builder()
                    .id(recruitComment.getId())
                    .memberId(recruitComment.getMember().getId())
                    .memberNickName(recruitComment.getMember().getMemberNickname())
                    .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                    .modifiedDate(recruitComment.getModifiedDate())
                    .content(recruitComment.getContent())
                    .build();
                break;
        
            case "COMMUNITY":
                CommunityComment communityComment = communityCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));

                if(memberId != communityComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                communityComment.modify(dto);
                communityCommentRepository.save(communityComment);

                data = CommentResponseDto.builder()
                    .id(communityComment.getId())
                    .memberId(communityComment.getMember().getId())
                    .memberNickName(communityComment.getMember().getMemberNickname())
                    .memberProfileUrl(communityComment.getMember().getProfileUrl())
                    .modifiedDate(communityComment.getModifiedDate())
                    .content(communityComment.getContent())
                    .build();
                    
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        log.info("/comment/service : modifyComment success data = {}", data);
        return new SuccessResponseDto<>(true, "댓글 수정이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentDeleteResponseDto> deleteComment(Long memberId, Long commentId, PostType postType) {
        CommentDeleteResponseDto data = null;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        switch (postType.name()) {
            case "PROJECT":
                ProjectComment projectComment = projectCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));
                
                if(memberId != projectComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                projectCommentRepository.deleteById(commentId);

                break;
        
            case "RECRUIT":
                RecruitComment recruitComment = recruitCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));
                
                if(memberId != recruitComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                recruitCommentRepository.deleteById(commentId);
                
                break;
        
            case "COMMUNITY":
                CommunityComment communityComment = communityCommentRepository.findById(commentId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMENT_NOT_FOUND));
                    
                if(memberId != communityComment.getMember().getId() && !member.getRole().equals("Administrator")){
                    throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
                }

                communityCommentRepository.deleteById(commentId);
                
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        data = CommentDeleteResponseDto.builder().commentId(commentId).postType(postType.name()).build();

        log.info("/comment/service : deleteComment success data = {}", data);
        return new SuccessResponseDto<CommentDeleteResponseDto>(true, "댓글 삭제가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<CommentResponseDto>> readMyContentsList(Long memberId, Integer page, Integer size,
                                                                           String postType, String sortType) {
        Pageable pageRequest = pageable(page, size, sortType);
        List<CommentResponseDto> data;

        switch (postType.toLowerCase()) {
            case "project":
                System.out.println("CommentServiceImpl.readMyContentsList");
                List<ProjectComment> projectComments = projectCommentRepository.findByMemberId(memberId, pageRequest);
                data = projectComments.stream()
                        .map(projectComment -> CommentResponseDto.builder()
                                .contentsTitle(projectComment.getProject().getTitle())
                                .id(projectComment.getId())
                                .memberId(projectComment.getMember().getId())
                                .memberNickName(projectComment.getMember().getMemberNickname())
                                .memberProfileUrl(projectComment.getMember().getProfileUrl())
                                .modifiedDate(projectComment.getModifiedDate())
                                .content(projectComment.getContent())
                                .build())
                        .collect(Collectors.toList());
                break;

            case "community":
                List<CommunityComment> communityComments = communityCommentRepository.findByCommunityId(memberId, pageRequest);
                data = communityComments.stream()
                        .map(communityComment -> CommentResponseDto.builder()
                                .contentsTitle(communityComment.getCommunity().getTitle())
                                .id(communityComment.getId())
                                .memberId(communityComment.getMember().getId())
                                .memberNickName(communityComment.getMember().getMemberNickname())
                                .memberProfileUrl(communityComment.getMember().getProfileUrl())
                                .modifiedDate(communityComment.getModifiedDate())
                                .content(communityComment.getContent())
                                .build())
                        .collect(Collectors.toList());
                break;

            case "recruit":
                List<RecruitComment> recruitComments = recruitCommentRepository.findByRecruitId(memberId, pageRequest);
                data = recruitComments.stream()
                        .map(recruitComment -> CommentResponseDto.builder()
                                .contentsTitle(recruitComment.getRecruit().getTitle())
                                .id(recruitComment.getId())
                                .memberId(recruitComment.getMember().getId())
                                .memberNickName(recruitComment.getMember().getMemberNickname())
                                .memberProfileUrl(recruitComment.getMember().getProfileUrl())
                                .modifiedDate(recruitComment.getModifiedDate())
                                .content(recruitComment.getContent())
                                .build())
                        .collect(Collectors.toList());
                break;
            default:
                throw new CreaviCodeException(GlobalErrorCode.TYPE_NOT_FOUND);
        }

        return new SuccessResponseDto<>(true, "댓글 리스트 조회가 완료되었습니다.", data);
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
