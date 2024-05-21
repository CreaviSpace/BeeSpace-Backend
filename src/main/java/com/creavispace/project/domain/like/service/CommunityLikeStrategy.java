package com.creavispace.project.domain.like.service;

import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.like.dto.response.LikeCountResponseDto;
import com.creavispace.project.domain.like.dto.response.LikeResponseDto;
import com.creavispace.project.domain.like.entity.CommunityLike;
import com.creavispace.project.domain.like.repository.CommunityLikeRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityLikeStrategy implements LikeStrategy {

    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Override
    public LikeResponseDto likeToggle(Member member, Long postId) {
        LikeResponseDto data = null;
        Community community = communityRepository.findByIdAndStatusTrue(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        CommunityLike communityLike = communityLikeRepository.findByCommunityIdAndMemberId(postId, member.getId());

        if(communityLike == null){
            CommunityLike saveLike = CommunityLike.builder()
                    .member(member)
                    .community(community)
                    .build();
            communityLikeRepository.save(saveLike);
            community.plusLikeCount();
            data = new LikeResponseDto(true);
        }else{
            communityLikeRepository.deleteById(communityLike.getId());
            community.minusLikeCount();
            data = new LikeResponseDto(false);
        }
        communityRepository.save(community);
        return data;
    }

    @Override
    public LikeResponseDto readLike(String memberId, Long postId) {
        communityRepository.findByIdAndStatusTrue(postId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
        boolean isCommunityLike = communityLikeRepository.existsByCommunityIdAndMemberId(postId, memberId);
        return new LikeResponseDto(isCommunityLike);
    }

    @Override
    public LikeCountResponseDto likeCount(Long postId) {
        return new LikeCountResponseDto(communityLikeRepository.countByCommunityId(postId));
    }
}
