package com.creavispace.project.domain.community.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.community.dto.request.CommunityRequestDto;
import com.creavispace.project.domain.community.dto.response.CommunityHashTagDto;
import com.creavispace.project.domain.community.dto.response.CommunityReadResponseDto;
import com.creavispace.project.domain.community.dto.response.CommunityResponseDto;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.file.entity.CommunityImage;
import com.creavispace.project.domain.file.entity.Image;
import com.creavispace.project.domain.file.service.ImageManager;
import com.creavispace.project.domain.hashTag.entity.CommunityHashTag;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final ImageManager imageManager;

    @Override
    @Transactional
    public SuccessResponseDto<Long> createCommunity(String memberId, CommunityRequestDto dto) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));
        // 커뮤니티 이미지 필터(게시글에 사용되지 않는 이미지 삭제)
        List<String> filteredImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());
        // 커뮤니티 이미지 생성
        List<CommunityImage> communityImages = filteredImages.stream()
                .map(CommunityImage::new)
                .toList();
        // 커뮤니티 해시태그 생성
        List<CommunityHashTag> communityHashTags = dto.getHashTags().stream()
                .map(hashTag -> CommunityHashTag.builder().hashTag(hashTag).build())
                .toList();
        // 커뮤니티 생성
        Community community = Community.createCommunity(dto, member, communityImages, communityHashTags);
        // 커뮤니티 게시글 저장
        communityRepository.save(community);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 생성이 완료되었습니다.", community.getId());
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> modifyCommunity(String memberId, Long communityId,
                                                    CommunityRequestDto dto) {
        // 커뮤니티 엔티티 조회
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new NoSuchElementException("커뮤니티 id("+communityId+")가 존재하지 않습니다."));

        // 수정 권한 조회
        if(!community.getMember().getId().equals(memberId))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 커뮤니티 업데이트
        community.update(dto);

        // 커뮤니티 해시태그 업데이트
        updateCommunityHashTag(community, dto.getHashTags());

        // 커뮤니티 이미지 필터(게시글에 사용되지 않는 이미지 삭제)
        List<String> filteredNewImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());

        // 커뮤니티 이미지 업데이트
        updateCommunityImages(community, filteredNewImages);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 수정이 완료되었습니다.", communityId);

    }

    private void updateCommunityImages(Community community, List<String> newImages) {
        // 기존 커뮤니티 이미지 삭제
        community.getCommunityImages().clear();

        // new 커뮤니티 이미지 생성
        List<CommunityImage> communityImages = newImages.stream()
                .map(CommunityImage::new)
                .toList();

        // new 커뮤니티 이미지 저장
        for (CommunityImage communityImage : communityImages) {
            community.addCommunityImages(communityImage);
        }
    }

    private void updateCommunityHashTag(Community community, List<String> hashTags) {
        // 기존 커뮤니티 해시태그 삭제
        community.getCommunityHashTags().clear();

        // new 커뮤니티 해시태그 생성
        List<CommunityHashTag> newCommunityHashTags = hashTags.stream()
                .map(newHashTag -> CommunityHashTag.builder().hashTag(newHashTag).build())
                .toList();

        // new 커뮤니티 해시태그 저장
        for(CommunityHashTag newHashTag : newCommunityHashTags){
            community.addCommunityHashTag(newHashTag);
        }
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> deleteCommunity(String memberId, Long communityId) {
        // 커뮤니티 엔티티 조회
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NoSuchElementException("커뮤니티 id(" + communityId + ")가 존재하지 않습니다."));

        // 삭제 권한 조회
        Member member = community.getMember();
        if(!member.getId().equals(memberId) && !member.getRole().equals(Role.ADMIN))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 커뮤니티 상태 변경(DELETE)
        community.changeStatus(Post.Status.DELETE);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 삭제가 완료되었습니다.", communityId);
    }

    @Override
    @Transactional
    public SuccessResponseDto<CommunityReadResponseDto> readCommunity(String memberId, Long communityId) {
        CommunityReadResponseDto data = null;

        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 커뮤니티 엔티티 조회
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new NoSuchElementException("커뮤니티 id(" + communityId + ")가 존재하지 않습니다."));

        // 권한 조회
        if (!community.getStatus().equals(Post.Status.PUBLIC) && !community.getMember().getId().equals(memberId) && !member.getRole().equals(Role.ADMIN)) {
            throw new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND);
        }

        // 커뮤니티 엔티티 toDto
        data = toCommunityReadResponseDto(community);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 디테일 조회가 완료되었습니다.", data);
    }

    private CommunityReadResponseDto toCommunityReadResponseDto(Community community) {
        return CommunityReadResponseDto.builder()
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
                                .hashTag(hashTag.getHashTag())
                                .build())
                        .toList())
                .images(community.getCommunityImages().stream().map(Image::getUrl).toList())
                .build();
    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> readCommunityList(CommunityCategory category, String hashTag, Pageable pageRequest) {
        List<CommunityResponseDto> data = null;

        // 커뮤니티 엔티티 조회
        Page<Community> pageable = communityRepository.findByStatusAndCategory(Post.Status.PUBLIC, category, pageRequest);

        // 결과값이 없으면 204 반환
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_COMMUNITY_CONTENT);

        // 커뮤니티 리스트 결과 toDto
        data = pageable.getContent().stream()
                .map(community -> CommunityResponseDto.builder()
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
                                .map(communityHashTag -> CommunityHashTagDto.builder().hashTag(communityHashTag.getHashTag()).build())
                                .toList())
                        .images(community.getCommunityImages().stream()
                                .map(Image::getUrl)
                                .toList())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "커뮤니티 게시글 리스트 조회가 완료되었습니다.", data);

    }

    @Override
    public SuccessResponseDto<List<CommunityResponseDto>> mypageCommunityList(Pageable pageable, CommunityCategory category, String memberId, String memberId_param) {
        List<CommunityResponseDto> data = null;

        Page<Community> communityPage;

        // 맴버 엔티티 조회
        memberRepository.findById(memberId_param).orElseThrow(() -> new NoSuchElementException("회원 id("+memberId_param+")가 존재하지 않습니다."));

        // 로그인 사용자의 마이페이지면
        if(memberId.equals(memberId_param)) {
            // 커뮤니티 엔티티 조회 (비공개 포함)
            communityPage = communityRepository.findByMemberId(memberId_param, pageable);
        }else{ // 로그인 사용자의 마이페이지가 아니면
            // 커뮤니티 엔티티 조회 (공개만)
            communityPage = communityRepository.findByMemberIdAndStatus(memberId_param, Post.Status.PUBLIC, pageable);
        }

        // 결과값이 없으면 204 반환
        if (!communityPage.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        }

        // 커뮤니티 리스트 결과 toDto
        data = communityPage.getContent().stream()
                .map(community -> CommunityResponseDto.builder()
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
                                .map(communityHashTag -> CommunityHashTagDto.builder().hashTag(communityHashTag.getHashTag()).build())
                                .toList())
                        .images(community.getCommunityImages().stream()
                                .map(Image::getUrl)
                                .toList())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "마이페이지 커뮤니티 게시글 리스트 조회가 완료되었습니다.", data);
    }
}
