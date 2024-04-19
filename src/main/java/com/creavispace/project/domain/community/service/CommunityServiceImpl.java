package com.creavispace.project.domain.community.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.CommunityCategory;
import com.creavispace.project.domain.common.dto.type.OrderBy;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.entity.CommunityHashTag;
import com.creavispace.project.domain.community.repository.CommunityHashTagRepository;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.hashTag.entity.HashTag;
import com.creavispace.project.domain.hashTag.repository.HashTagRepository;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;
import com.creavispace.project.global.util.UsableConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final HashTagRepository hashTagRepository;
    private final CommunityHashTagRepository communityHashTagRepository;

    @Override
    @Transactional
    public SuccessResponseDto<CommunityResponseDto> createCommunity(String memberId, CommunityRequestDto dto) {
        CommunityResponseDto data = null;
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        CommunityCategory category = CustomValueOf.valueOf(CommunityCategory.class, dto.getCategory(),
                GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);

        // 커뮤니티 게시글 생성
        Community community = Community.builder()
                .member(member)
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCount(0)
                .status(Boolean.TRUE)
                .build();

        // 커뮤니티 게시글 저장
        communityRepository.save(community);

        List<String> hashTagDtos = dto.getHashTags();
        // 커뮤니티 해시태그 정보가 있는지
        if (hashTagDtos != null && !hashTagDtos.isEmpty()) {
            List<CommunityHashTag> communityHashTags = dto.getHashTags().stream()
                    .map(hashTagDto -> {
                        Boolean hasHashTag = hashTagRepository.existsByHashTag(hashTagDto);
                        HashTag hashTag;
                        if (hasHashTag) {
                            hashTag = hashTagRepository.findByHashTag(hashTagDto);
                        } else {
                            hashTag = hashTagRepository.save(HashTag.builder().hashTag(hashTagDto).build());
                        }
                        return CommunityHashTag.builder()
                                .community(community)
                                .hashTag(hashTag)
                                .build();
                    })
                    .collect(Collectors.toList());

            // 커뮤니티 해시태그 정보 저장
            communityHashTagRepository.saveAll(communityHashTags);
        }

        // 저장한 커뮤니티 해시태그 정보 가져오기
        List<CommunityHashTag> communityHashTags = communityHashTagRepository.findByCommunityId(community.getId());

        // 가져온 커뮤니티 해시태그 정보 DTO 변환
        List<CommunityHashTagDto> communityHashTagDtos = communityHashTags.stream()
                .map(communityHashTag -> CommunityHashTagDto.builder()
                        .hashTagId(communityHashTag.getHashTag().getId())
                        .hashTag(communityHashTag.getHashTag().getHashTag())
                        .build())
                .collect(Collectors.toList());

        // 커뮤니티 게시글 생성 DTO
        data = CommunityResponseDto.builder()
                .id(community.getId())
                .postType(PostType.COMMUNITY.name())
                .category(community.getCategory().name())
                .memberId(community.getMember().getId())
                .memberNickName(community.getMember().getMemberNickname())
                .memberProfile(community.getMember().getProfileUrl())
                .viewCount(community.getViewCount())
                .createdDate(community.getCreatedDate())
                .modifiedDate(community.getModifiedDate())
                .title(community.getTitle())
                .content(community.getContent())
                .hashTags(communityHashTagDtos)
                .build();

        log.info("/community/service : createCommunity success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 생성이 완료되었습니다.", data);

    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityResponseDto> modifyCommunity(String memberId, Long communityId,
                                                                    CommunityRequestDto dto) {

        CommunityResponseDto data = null;
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 수정할 커뮤니티 게시글이 존재하는지
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 수정 권한이 있는지
        if (!memberId.equals(community.getMember().getId()) && !member.getRole().equals(Role.ADMIN)) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 커뮤니티 게시글 수정 및 저장
        community.modify(dto);
        communityRepository.save(community);

        // 기존 커뮤니티 해시태그 삭제
        communityHashTagRepository.deleteByCommunityId(communityId);

        List<String> hashTagDtos = dto.getHashTags();
        // 수정 DTO에 해시태그 정보가 있는지
        if (hashTagDtos != null && !hashTagDtos.isEmpty()) {
            List<CommunityHashTag> communityHashTags = hashTagDtos.stream()
                    .map(hashTagDto -> {
                        Boolean hasHashTag = hashTagRepository.existsByHashTag(hashTagDto);
                        HashTag hashTag;
                        if (hasHashTag) {
                            hashTag = hashTagRepository.findByHashTag(hashTagDto);
                        } else {
                            hashTag = hashTagRepository.save(HashTag.builder().hashTag(hashTagDto).build());
                        }
                        return CommunityHashTag.builder()
                                .community(community)
                                .hashTag(hashTag)
                                .build();
                    })
                    .collect(Collectors.toList());
            // 수정 DTO의 해시태그 정보 저장
            communityHashTagRepository.saveAll(communityHashTags);
        }

        // 저장한 커뮤니티 해시태그 가져오기
        List<CommunityHashTag> communityHashTags = communityHashTagRepository.findByCommunityId(communityId);

        // 가져온 커뮤니티 해시태그 정보를 DTO 변환
        List<CommunityHashTagDto> hashTags = communityHashTags.stream()
                .map(communityHashTag -> CommunityHashTagDto.builder()
                        .hashTagId(communityHashTag.getHashTag().getId())
                        .hashTag(communityHashTag.getHashTag().getHashTag())
                        .build())
                .collect(Collectors.toList());

        // 커뮤니티 수정 DTO
        data = CommunityResponseDto.builder()
                .id(community.getId())
                .postType(PostType.COMMUNITY.name())
                .category(community.getCategory().name())
                .memberId(community.getMember().getId())
                .memberNickName(community.getMember().getMemberNickname())
                .memberProfile(community.getMember().getProfileUrl())
                .viewCount(community.getViewCount())
                .createdDate(community.getCreatedDate())
                .modifiedDate(community.getModifiedDate())
                .title(community.getTitle())
                .content(community.getContent())
                .hashTags(hashTags)
                .build();

        log.info("/community/service : modifyCommunity success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 수정이 완료되었습니다.", data);

    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityDeleteResponseDto> deleteCommunity(String memberId, Long communityId) {
        CommunityDeleteResponseDto data = null;

        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 삭제할 커뮤니티 게시글이 존재하는지
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 삭제할 권한이 있는지
        if (!memberId.equals(community.getMember().getId()) && !member.getRole().equals("Administrator")) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 커뮤니티 비활성화 및 저장
        community.disable();
        communityRepository.save(community);

        // 커뮤니티 삭제 결과 DTO변환
        data = CommunityDeleteResponseDto.builder()
                .communityId(community.getId())
                .postType(PostType.COMMUNITY.name())
                .build();

        log.info("/community/service : deleteCommunity success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 삭제가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(String memberId, Long communityId,
                                                                      HttpServletRequest request) {
        CommunityReadResponseDto data = null;
        Optional<Community> optionalCommunity;
        // JWT 회원과 커뮤니티 게시글 작성자와 일치하는지
        boolean isWriter = communityRepository.existsByIdAndMemberId(communityId, memberId);
        // 일치하면 비활성화 커뮤니티 게시글도 조회가능
        if (isWriter) {
            optionalCommunity = communityRepository.findById(communityId);
        }
        // 일치하지 않으면 활성화된 커뮤니티 게시글만 조회가능
        else {
            optionalCommunity = communityRepository.findByIdAndStatusTrue(communityId);
        }

        Community community = optionalCommunity.orElseThrow(
                () -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 조회수 증가
        String communityViewLogHeader = request.getHeader("communityViewLog");
        // 헤더에 communityViewLog 값이 없으면
        if (communityViewLogHeader == null) {
            community.plusViewCount();
            communityRepository.save(community);
            communityViewLogHeader = "[" + communityId + "]";
        }
        // 헤더에 communityViewLog 값이 있으면
        else {
            // 조회한적이 없는 커뮤니티일 경우
            if (!communityViewLogHeader.contains("[" + communityId + "]")) {
                community.plusViewCount();
                communityRepository.save(community);
                communityViewLogHeader += "_[" + communityId + "]";
            }
        }

        // 커뮤니티 디테일 DTO변환
        data = CommunityReadResponseDto.builder()
                .id(community.getId())
                .postType(PostType.COMMUNITY.name())
                .category(community.getCategory().name())
                .memberId(community.getMember().getId())
                .memberNickName(community.getMember().getMemberNickname())
                .memberProfile(community.getMember().getProfileUrl())
                .viewCount(community.getViewCount())
                .createdDate(community.getCreatedDate())
                .modifiedDate(community.getModifiedDate())
                .title(community.getTitle())
                .content(community.getContent())
                .hashTags(community.getCommunityHashTags().stream()
                        .map(hashTag -> CommunityHashTagDto.builder()
                                .hashTagId(hashTag.getHashTag().getId())
                                .hashTag(hashTag.getHashTag().getHashTag())
                                .build())
                        .collect(Collectors.toList()))
                .communityViewLog(communityViewLogHeader)
                .build();

        log.info("/community/service : readCommunity success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 디테일 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(Integer size, Integer page,
                                                                            CommunityCategory category, String hashTag,
                                                                            OrderBy orderby) {
        List<CommunityResponseDto> data = null;
        Pageable pageRequest = PageRequest.of(page - 1, size);
        Page<Community> pageable;

        // 정렬 조건
        switch (orderby.name()) {
            case "LATEST_ACTIVITY":
                // 카테고리와 해시태그 둘다 존재할 경우
                if (category != null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagId(category, hashTagId,
                            pageRequest);
                }
                // 해시태그만 존재할 경우
                else if (category == null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagId(hashTagId, pageRequest);
                }
                // 카테고리만 존재할 경우
                else if (hashTag == null && category != null) {
                    pageable = communityRepository.findAllByStatusTrueAndCategory(category, pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else {
                    pageable = communityRepository.findAllByStatusTrue(pageRequest);
                }
                break;

            case "RECOMMENDED":
                // 카테고리와 해시태그 둘다 존재할 경우
                if (category != null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagIdOrderByLikeCountDesc(
                            category, hashTagId, pageRequest);
                }
                // 해시태그만 존재할 경우
                else if (category == null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagIdOrderByLikeCountDesc(hashTagId,
                            pageRequest);
                }
                // 카테고리만 존재할 경우
                else if (hashTag == null && category != null) {
                    pageable = communityRepository.findAllByStatusTrueAndCategoryOrderByLikeCountDesc(category,
                            pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else {
                    pageable = communityRepository.findAllByStatusTrueOrderByLikeCountDesc(pageRequest);
                }
                break;

            case "MOST_VIEWED":
                // 카테고리와 해시태그 둘다 존재할 경우
                if (category != null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagId(category, hashTagId,
                            pageRequest);
                }
                // 해시태그만 존재할 경우
                else if (category == null && hashTag != null) {
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagId(hashTagId, pageRequest);
                }
                // 카테고리만 존재할 경우
                else if (hashTag == null && category != null) {
                    pageable = communityRepository.findAllByStatusTrueAndCategoryOrderByViewCountDesc(category,
                            pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else {
                    pageable = communityRepository.findAllByStatusTrueOrderByViewCountDesc(pageRequest);
                }
                break;

            default:
                throw new CreaviCodeException(GlobalErrorCode.ORDERBY_NOT_FOUND);
        }

        // 조건에 맞는 커뮤니티 게시글이 존재하는지
        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_COMMUNITY_CONTENT);
        }
        List<Community> communities = pageable.getContent();

        // 조건에 맞는 게시글 리스트 DTO 변환
        data = buildDto(communities);

        log.info("/community/service : readCommunityList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 리스트 조회가 완료되었습니다.", data);

    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityListForAdmin(
            Integer size, Integer page, String status, String sortType) {
        System.out.println("CommunityServiceImpl.readCommunityListForAdmin");

        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Community> pageable = getCommunity(status, pageRequest);

        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_COMMUNITY_CONTENT);
        }

        List<Community> communities = pageable.getContent();
        List<CommunityResponseDto> data = buildDto(communities);

        log.info("/community/service : readCommunityList success data = {}", data);
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> readMyCommunityList(
            String memberId, Integer size, Integer page, String sortType) {
        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Community> pageable = getCommunities(memberId, pageRequest);

        // 조건에 맞는 커뮤니티 게시글이 존재하는지
        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_COMMUNITY_CONTENT);
        }

        List<Community> communities = pageable.getContent();
        List<CommunityResponseDto> data = buildDto(communities);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 리스트 조회가 완료되었습니다.", data);

    }

    private Page<Community> getCommunities(String memberId, Pageable pageRequest) {
        Page<Community> pageable;
        pageable = communityRepository.findByMemberIdAndStatusTrue(memberId, pageRequest);
        return pageable;
    }

    private Page<Community> getCommunity(String status, Pageable pageRequest) {
        if (status.equalsIgnoreCase("true")) {
            return communityRepository.findAllByStatusTrue(pageRequest);
        }
        if (status.equalsIgnoreCase("false")) {
            return communityRepository.findByStatusFalse(pageRequest);
        }
        if (status.equalsIgnoreCase("all")) {
            return communityRepository.findAll(pageRequest);
        }
        return communityRepository.findAll(pageRequest);
    }

    private Map<Long, List<CommunityHashTagDto>> hashTags(List<CommunityHashTag> communityHashTags) {
        System.out.println("CommunityServiceImpl.hashTags");
        Map<Long, List<CommunityHashTagDto>> hashTags = new HashMap<>();
        List<Long> collect = communityHashTags.stream().map(hashTag -> hashTag.getHashTag().getId()).distinct()
                .toList();
        hashTagRepository.findByIdIn(collect);
        for (int i = 0; i < communityHashTags.size(); i++) {
            CommunityHashTag communityHashTag = communityHashTags.get(i);
            List<CommunityHashTagDto> hashTag = hashTags.getOrDefault(communityHashTag.getCommunity().getId(),
                    new ArrayList<>());
            CommunityHashTagDto communityHashTagDto = CommunityHashTagDto.builder()
                    .hashTagId(communityHashTag.getHashTag().getId())
                    .hashTag(communityHashTag.getHashTag().getHashTag())
                    .build();
            hashTag.add(communityHashTagDto);
            hashTags.put(communityHashTag.getCommunity().getId(), hashTag);
        }
        return hashTags;
    }

    private List<CommunityResponseDto> buildDto(
            List<Community> communities) {
        System.out.println("CommunityServiceImpl.buildDto");
        List<Long> communityIds = communities.stream().map(Community::getId).toList();
        List<CommunityHashTag> communityHashTags = communityHashTagRepository.findByCommunityIdIn(communityIds);
        List<String> collect = communities.stream().map(community -> community.getMember().getId())
                .toList();

        memberRepository.findByIdIn(collect);
        Map<Long, List<CommunityHashTagDto>> hashTags = hashTags(communityHashTags);

        return communities.stream()
                .map(community -> CommunityResponseDto.builder()
                        .id(community.getId())
                        .memberProfile(community.getMember().getProfileUrl())
                        .memberNickName(community.getMember().getMemberNickname())
                        .postType(PostType.COMMUNITY.name())
                        .category(community.getCategory().name())
                        .memberId(community.getMember().getId())
                        .viewCount(community.getViewCount())
                        .createdDate(community.getCreatedDate())
                        .modifiedDate(community.getModifiedDate())
                        .title(community.getTitle())
                        .content(community.getContent())
                        .hashTags(hashTags.get(community.getId()))
                        .createdDate(community.getCreatedDate())
                        .modifiedDate(community.getModifiedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
