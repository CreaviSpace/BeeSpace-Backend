package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkContentsResponseDto;
import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.hashTag.repository.CommunityHashTagRepository;
import com.creavispace.project.domain.hashTag.repository.HashTagRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.repository.ProjectLinkRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.recruit.repository.RecruitTechStackRepository;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.utils.UsableConst;
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
public class BookmarkServiceImpl implements BookmarkService {

    private final MemberRepository memberRepository;
    public BookmarkServiceImpl(ProjectBookmarkRepository projectBookmarkRepository,
                               RecruitBookmarkRepository recruitBookmarkRepository,
                               CommunityBookmarkRepository communityBookmarkRepository,
                               MemberRepository memberRepository,
                               ProjectRepository projectRepository,
                               RecruitRepository recruitRepository,
                               CommunityRepository communityRepository,
                               ProjectLinkRepository projectLinkRepository,
                               RecruitTechStackRepository recruitTechStackRepository,
                               TechStackRepository techStackRepository,
                               HashTagRepository hashTagRepository,
                               CommunityHashTagRepository communityHashTagRepository) {
        this.memberRepository = memberRepository;
        this.strategyMap = Map.of(
                PostType.PROJECT, new ProjectBookmarkStrategy(projectRepository, projectBookmarkRepository, projectLinkRepository),
                PostType.COMMUNITY, new CommunityBookmarkStrategy(communityRepository, communityBookmarkRepository, hashTagRepository, communityHashTagRepository),
                PostType.RECRUIT, new RecruitBookmarkStrategy(recruitRepository, recruitBookmarkRepository, recruitTechStackRepository, techStackRepository)
        );
    }

    private final Map<PostType, BookmarkStrategy> strategyMap;

    @Override
    @Transactional
    public SuccessResponseDto<BookmarkResponseDto> bookmarkToggle(String memberId, Long postId, PostType postType) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        BookmarkStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        BookmarkResponseDto data = strategy.bookmarkToggle(postId, member);

        log.info("/comment/service : bookmarkToggle success data = {}", data);
        return new SuccessResponseDto<>(true, "북마크 토글 기능이 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<BookmarkResponseDto> readBookmark(String memberId, Long postId, PostType postType) {
        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        BookmarkStrategy strategy = strategyMap.get(postType);
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        BookmarkResponseDto data = strategy.readBookmark(postId, memberId);

        log.info("/comment/service : readBookmark success data = {}", data);
        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<BookmarkContentsResponseDto>> readMyBookmark(String memberId, Integer page, Integer size, String postType, String sortType) {
        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        BookmarkStrategy strategy = strategyMap.get(PostType.valueOf(postType.toUpperCase()));
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        List<BookmarkContentsResponseDto> data = strategy.readMyBookmark(memberId, pageRequest);

        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", data);
    }
}