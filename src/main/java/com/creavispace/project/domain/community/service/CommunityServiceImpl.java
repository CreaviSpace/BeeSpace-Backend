package com.creavispace.project.domain.community.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.CommunityCategory;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.entity.CommunityHashTag;
import com.creavispace.project.domain.community.repository.CommunityHashTagRepository;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.hashTag.entity.HashTag;
import com.creavispace.project.domain.hashTag.repository.HashTagRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.creavispace.project.domain.community.dto.response.CommunityDeleteResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService{

    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final HashTagRepository hashTagRepository;
    private final CommunityHashTagRepository communityHashTagRepository;

    @Override
    @Transactional
    public SuccessResponseDto<CommunityResponseDto> createCommunity(Long memberId, CommunityRequestDto dto) {
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        CommunityCategory category = CustomValueOf.valueOf(CommunityCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);

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
        if(hashTagDtos != null && !hashTagDtos.isEmpty()){
            List<CommunityHashTag> communityHashTags = dto.getHashTags().stream()
                .map(hashTagDto -> {
                    Boolean hasHashTag = hashTagRepository.existsByHashTag(hashTagDto);
                    HashTag hashTag;
                    if(hasHashTag){
                        hashTag = hashTagRepository.findByHashTag(hashTagDto);
                    }else{
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
        CommunityResponseDto create = CommunityResponseDto.builder()
            .id(community.getId())
            .postType(PostType.COMMUNITY.getName())
            .category(community.getCategory().getName())
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

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 생성이 완료되었습니다.", create);
        
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityResponseDto> modifyCommunity(Long memberId, Long communityId,
        CommunityRequestDto dto) {
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 수정할 커뮤니티 게시글이 존재하는지
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 수정 권한이 있는지
        if(community.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 커뮤니티 게시글 수정 및 저장
        community.modify(dto);
        communityRepository.save(community);

        // 기존 커뮤니티 해시태그 삭제
        communityHashTagRepository.deleteByCommunityId(communityId);

        List<String> hashTagDtos = dto.getHashTags();
        // 수정 DTO에 해시태그 정보가 있는지
        if(hashTagDtos != null && !hashTagDtos.isEmpty()){
            List<CommunityHashTag> communityHashTags = hashTagDtos.stream()
                .map(hashTagDto -> {
                    Boolean hasHashTag = hashTagRepository.existsByHashTag(hashTagDto);
                    HashTag hashTag;
                    if(hasHashTag){
                        hashTag = hashTagRepository.findByHashTag(hashTagDto);
                    }else{
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
        CommunityResponseDto modify = CommunityResponseDto.builder()
            .id(community.getId())
            .postType(PostType.COMMUNITY.getName())
            .category(community.getCategory().getName())
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

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 수정이 완료되었습니다.", modify);

    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityDeleteResponseDto> deleteCommunity(Long memberId, Long communityId) {
        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 삭제할 커뮤니티 게시글이 존재하는지
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 삭제할 권한이 있는지
        if(community.getMember().getId() != memberId && !member.getRole().equals("Administrator")){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 커뮤니티 비활성화 및 저장
        community.disable();
        communityRepository.save(community);

        // 커뮤니티 삭제 결과 DTO변환
        CommunityDeleteResponseDto delete = CommunityDeleteResponseDto.builder()
            .communityId(community.getId())
            .postType(PostType.COMMUNITY.getName())
            .build();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 삭제가 완료되었습니다.", delete);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(Long memberId, Long communityId, HttpServletRequest request) {
        Optional<Community> optionalCommunity;
        // JWT 회원과 커뮤니티 게시글 작성자와 일치하는지
        boolean isWriter = communityRepository.existsByIdAndMemberId(communityId, memberId);
        // 일치하면 비활성화 커뮤니티 게시글도 조회가능
        if(isWriter){
            optionalCommunity = communityRepository.findById(communityId);
        }
        // 일치하지 않으면 활성화된 커뮤니티 게시글만 조회가능
        else{
            optionalCommunity = communityRepository.findByIdAndStatusTrue(communityId);
        }

        Community community = optionalCommunity.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));

        // 조회수 증가
        String communityViewLogHeader = request.getHeader("communityViewLog");
        // 헤더에 communityViewLog 값이 없으면
        if(communityViewLogHeader == null){
            community.plusViewCount();
            communityRepository.save(community);
            communityViewLogHeader =  "["+ communityId + "]";
        }
        // 헤더에 communityViewLog 값이 있으면
        else{
            // 조회한적이 없는 커뮤니티일 경우
            if(!communityViewLogHeader.contains("["+ communityId + "]")){
                community.plusViewCount();
                communityRepository.save(community);
                communityViewLogHeader +=  "_["+ communityId + "]";
            }
        }
        
        // 커뮤니티 디테일 DTO변환
        CommunityReadResponseDto read = CommunityReadResponseDto.builder()
            .id(community.getId())
            .postType(PostType.COMMUNITY.getName())
            .category(community.getCategory().getName())
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

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 디테일 조회가 완료되었습니다.", read);
    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(Integer size, Integer page, String category, String hashTag, String orderby) {
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Community> pageable;
        CommunityCategory communityCategory;

        // 정렬 조건
        switch (orderby) {
            case "latest-activity":
                // 카테고리와 해시태그 둘다 존재할 경우
                if(category != null && hashTag != null){
                        communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                        Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                        pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagId(communityCategory.getName(), hashTagId, pageRequest);
                }
                // 해시태그만 존재할 경우
                else if(category == null && hashTag != null){
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagId(hashTagId, pageRequest);
                }
                // 카테고리만 존재할 경우
                else if(hashTag == null && category != null){
                    communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                    pageable = communityRepository.findAllByStatusTrueAndCategory(communityCategory.getName(), pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else{
                    pageable = communityRepository.findAllByStatusTrue(pageRequest);
                }
                break;
        
            case "recommended":
                // 카테고리와 해시태그 둘다 존재할 경우
                if(category != null && hashTag != null){
                    communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagIdOrderByLikeCountDesc(communityCategory.getName(), hashTagId, pageRequest);
                }
                // 해시태그만 존재할 경우
                else if(category == null && hashTag != null){
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagIdOrderByLikeCountDesc(hashTagId, pageRequest);
                }
                // 카테고리만 존재할 경우
                else if(hashTag == null && category != null){
                    communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                    pageable = communityRepository.findAllByStatusTrueAndCategoryOrderByLikeCountDesc(communityCategory.getName(), pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else{
                    pageable = communityRepository.findAllByStatusTrueOrderByLikeCountDesc(pageRequest);
                }
                break;
        
            case "most-viewed":
                // 카테고리와 해시태그 둘다 존재할 경우
                if(category != null && hashTag != null){
                    communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndCategoryAndHashTagId(communityCategory.getName(), hashTagId, pageRequest);
                }
                // 해시태그만 존재할 경우
                else if(category == null && hashTag != null){
                    Long hashTagId = hashTagRepository.findByHashTag(hashTag).getId();
                    pageable = communityRepository.findAllByStatusTrueAndHashTagId(hashTagId, pageRequest);
                }
                // 카테고리만 존재할 경우
                else if(hashTag == null && category != null){
                    communityCategory = CustomValueOf.valueOf(CommunityCategory.class, category, GlobalErrorCode.NOT_FOUND_COMMUNITY_CATEGORY);
                    pageable = communityRepository.findAllByStatusTrueAndCategoryOrderByViewCountDesc(communityCategory.getName(), pageRequest);
                }
                // 카테고리,해시태그 둘다 null 일경우
                else{
                    pageable = communityRepository.findAllByStatusTrueOrderByViewCountDesc(pageRequest);
                }
                break;
        
            default:
                throw new CreaviCodeException(GlobalErrorCode.ORDERBY_NOT_FOUND);
        }

        // 조건에 맞는 커뮤니티 게시글이 존재하는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_COMMUNITY_CONTENT);
        List<Community> communities = pageable.getContent();

        // 조건에 맞는 게시글 리스트 DTO 변환
        List<CommunityResponseDto> reads = communities.stream()
            .map(community -> CommunityResponseDto.builder()
                .id(community.getId())
                .postType(PostType.COMMUNITY.getName())
                .category(community.getCategory().getName())
                .memberId(community.getMember().getId())
                .viewCount(community.getViewCount())
                .createdDate(community.getCreatedDate())
                .modifiedDate(community.getModifiedDate())
                .title(community.getTitle())
                .content(community.getContent())
                .hashTags(community.getCommunityHashTags().stream()
                    .map(communityHashTag -> CommunityHashTagDto.builder()
                        .hashTagId(communityHashTag.getHashTag().getId())
                        .hashTag(communityHashTag.getHashTag().getHashTag())
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 리스트 조회가 완료되었습니다.", reads);
                
    }
    
}
