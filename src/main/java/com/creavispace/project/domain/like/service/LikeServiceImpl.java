package com.creavispace.project.domain.like.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.repository.CommunityLikeRepository;
import com.creavispace.project.domain.like.repository.ProjectLikeRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService{

    private final MemberRepository memberRepository;
    private final Map<PostType, LikeStrategy> strategyMap;

    public LikeServiceImpl(CommunityLikeRepository communityLikeRepository, ProjectLikeRepository projectLikeRepository, MemberRepository memberRepository, ProjectRepository projectRepository, CommunityRepository communityRepository) {
        this.memberRepository = memberRepository;
        this.strategyMap = Map.of(
                PostType.PROJECT, new ProjectLikeStrategy(projectRepository, projectLikeRepository),
                PostType.COMMUNITY, new CommunityLikeStrategy(communityRepository, communityLikeRepository)
        );
    }

    @Override
    @Transactional
    public SuccessResponseDto<LikeResponseDto> likeToggle(String memberId, Long postId, PostType postType) {
        // 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        LikeStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        LikeResponseDto data = strategy.likeToggle(member, postId);

        log.info("/like/service : likeToggle success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "좋아요 토글 기능이 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<LikeResponseDto> readLike(String memberId, Long postId, PostType postType) {
        // 회원이 존재하는지
        memberRepository.findById(memberId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        LikeStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        LikeResponseDto data = strategy.readLike(memberId, postId);

        log.info("/like/service : readLike success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "좋아요 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<LikeCountResponseDto> likeCount(Long postId, PostType postType) {
        LikeStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        LikeCountResponseDto data = strategy.likeCount(postId);

        log.info("/like/service : likeCount success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(Boolean.TRUE, "좋아요 수 조회가 완료되었습니다.", data);
    }
    
}
