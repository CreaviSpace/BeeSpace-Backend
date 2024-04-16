package com.creavispace.project.domain.comment.service;

import com.creavispace.project.domain.comment.dto.request.CommentRequestDto;
import com.creavispace.project.domain.comment.dto.response.CommentDeleteResponseDto;
import com.creavispace.project.domain.comment.dto.response.CommentResponseDto;
import com.creavispace.project.domain.comment.dto.response.MyPageCommentResponseDto;
import com.creavispace.project.domain.comment.repository.CommunityCommentRepository;
import com.creavispace.project.domain.comment.repository.ProjectCommentRepository;
import com.creavispace.project.domain.comment.repository.RecruitCommentRepository;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final Map<PostType, CommentStrategy> strategyMap;

    public CommentServiceImpl(ProjectCommentRepository projectCommentRepository, RecruitCommentRepository recruitCommentRepository, CommunityCommentRepository communityCommentRepository, MemberRepository memberRepository, ProjectRepository projectRepository, RecruitRepository recruitRepository, CommunityRepository communityRepository) {
        this.memberRepository = memberRepository;
        this.strategyMap = Map.of(
                PostType.PROJECT, new ProjectCommentStrategy(projectRepository, projectCommentRepository),
                PostType.COMMUNITY, new CommunityCommentStrategy(communityRepository, communityCommentRepository),
                PostType.RECRUIT, new RecruitCommentStrategy(recruitRepository, recruitCommentRepository)
        );
    }

    @Override
    public SuccessResponseDto<List<CommentResponseDto>> readCommentList(Long postId, PostType postType) {
        CommentStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        List<CommentResponseDto> data = strategy.readCommentList(postId);

        log.info("/comment/service : readCommentList success data = {}", data);
        return new SuccessResponseDto<>(true, "댓글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> createComment(String memberId, Long postId, PostType postType, CommentRequestDto dto) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        CommentStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        CommentResponseDto data = strategy.createComment(postId, member, dto.getContent());

        log.info("/comment/service : createComment success data = {}", data);
        return new SuccessResponseDto<>(true, "댓글 생성이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentResponseDto> modifyComment(String memberId, Long commentId, PostType postType, CommentRequestDto dto) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        CommentStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        CommentResponseDto data = strategy.modifyComment(member, commentId, dto);

        log.info("/comment/service : modifyComment success data = {}", data);
        return new SuccessResponseDto<>(true, "댓글 수정이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommentDeleteResponseDto> deleteComment(String memberId, Long commentId, PostType postType) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        CommentStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        strategy.deleteComment(member, commentId);

        CommentDeleteResponseDto data = CommentDeleteResponseDto.builder().commentId(commentId).postType(postType.name()).build();

        log.info("/comment/service : deleteComment success data = {}", data);
        return new SuccessResponseDto<>(true, "댓글 삭제가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<MyPageCommentResponseDto>> readMyContentsList(String memberId, Integer page, Integer size,
                                                                                 String postType, String sortType) {
        Pageable pageRequest = pageable(page, size, sortType);
        List<MyPageCommentResponseDto> data;

        CommentStrategy strategy = strategyMap.get(PostType.valueOf(postType.toUpperCase()));
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        data = strategy.readMyContentsList(memberId, pageRequest);

        return new SuccessResponseDto<>(true, "댓글 리스트 조회가 완료되었습니다.", data);
    }

    private static PageRequest pageable(Integer page, Integer size, String sortType) {
        if (sortType.equalsIgnoreCase("asc")) {
            return PageRequest.of(page - 1, size, Sort.by("createdDate").ascending());
        }
        return PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    }
}
