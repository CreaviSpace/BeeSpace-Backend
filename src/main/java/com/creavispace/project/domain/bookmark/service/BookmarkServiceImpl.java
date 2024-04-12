package com.creavispace.project.domain.bookmark.service;

import com.creavispace.project.domain.bookmark.dto.response.BookmarkResponseDto;
import com.creavispace.project.domain.bookmark.entity.Bookmark;
import com.creavispace.project.domain.bookmark.repository.CommunityBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.ProjectBookmarkRepository;
import com.creavispace.project.domain.bookmark.repository.RecruitBookmarkRepository;
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
public class BookmarkServiceImpl implements BookmarkService {

    private final MemberRepository memberRepository;
    private final Map<PostType, BookmarkStrategy> strategyMap;

    public BookmarkServiceImpl(ProjectBookmarkRepository projectBookmarkRepository, RecruitBookmarkRepository recruitBookmarkRepository, CommunityBookmarkRepository communityBookmarkRepository, MemberRepository memberRepository, ProjectRepository projectRepository, RecruitRepository recruitRepository, CommunityRepository communityRepository) {
        this.memberRepository = memberRepository;
        this.strategyMap = Map.of(
                PostType.PROJECT, new ProjectBookmarkStrategy(projectRepository, projectBookmarkRepository),
                PostType.COMMUNITY, new CommunityBookmarkStrategy(communityRepository, communityBookmarkRepository),
                PostType.RECRUIT, new RecruitBookmarkStrategy(recruitRepository, recruitBookmarkRepository)
        );
    }

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
    public SuccessResponseDto<List<Bookmark>> readMyBookmark(String memberId, Integer page, Integer size, String postType, String sortType) {
        Pageable pageRequest = pageable(page, size, sortType);
        memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        BookmarkStrategy strategy = strategyMap.get(PostType.valueOf(postType));
        if(strategy == null) throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);

        List<Bookmark> data = strategy.readMyBookmark(memberId, pageRequest);

        return new SuccessResponseDto<>(true, "북마크 조회가 완료되었습니다.", data);
    }

    private static PageRequest pageable(Integer page, Integer size, String sortType) {
        if (sortType.equalsIgnoreCase("asc")) {
            return PageRequest.of(page - 1, size, Sort.by("contentsCreatedDate").ascending());
        }
        return PageRequest.of(page - 1, size, Sort.by("contentsCreatedDate").descending());
    }
}