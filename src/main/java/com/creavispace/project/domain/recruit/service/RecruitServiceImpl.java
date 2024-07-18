package com.creavispace.project.domain.recruit.service;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.file.entity.RecruitImage;
import com.creavispace.project.domain.file.service.ImageManager;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.entity.Role;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.recruit.dto.request.RecruitPositionRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitTechStackRequestDto;
import com.creavispace.project.domain.recruit.dto.response.*;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.entity.RecruitPosition;
import com.creavispace.project.domain.recruit.entity.RecruitTechStack;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final MemberRepository memberRepository;
    private final RecruitRepository recruitRepository;
    private final TechStackRepository techStackRepository;
    private final ImageManager imageManager;

    @Override
    @Transactional
    public SuccessResponseDto<Long> createRecruit(String memberId, RecruitRequestDto dto) {
        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));
        // todo : 시간이 지나면 이미지 필터를 사용해야하는걸 모를것같은데 좀더 괜찮은 방법이 있는지 확인
        // 모집 이미지 필터(게시글에 사용되지 않는 이미지 삭제)
        List<String> filteredImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());
        // 모집 이미지 생성
        List<RecruitImage> recruitImages = filteredImages.stream()
                .map(RecruitImage::new)
                .toList();
        // 모집 기술스택 조회
        List<TechStack> techStacks = techStackRepository.findByTechStackIn(dto.getTechStacks().stream().map(RecruitTechStackRequestDto::getTechStack).toList());
        // 모집 기술스택 생성
        List<RecruitTechStack> recruitTechStacks = techStacks.stream()
                .map(techStack -> RecruitTechStack.builder().techStack(techStack).build())
                .toList();
        // 모집 포지션 생성
        List<RecruitPosition> recruitPositions = dto.getPositions().stream()
                .map(positionDto -> RecruitPosition.builder()
                        .position(positionDto.getPosition())
                        .amount(positionDto.getAmount())
                        .now(positionDto.getNow())
                        .status(RecruitPosition.Status.RECRUITING)
                        .build())
                .toList();
        // 모집 생성
        Recruit recruit = Recruit.createRecruit(dto, member, recruitImages, recruitTechStacks, recruitPositions);

        // 모집 저장
        recruitRepository.save(recruit);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 생성이 완료되었습니다.", recruit.getId());
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> modifyRecruit(String memberId, Long recruitId,
                                                  RecruitRequestDto dto) {
        // 모집 엔티티 조회
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new NoSuchElementException("모집 id(" + recruitId + ")가 존재하지 않습니다."));

        // 수정 권한 조회
        if(!recruit.getMember().getId().equals(memberId))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 모집 업데이트
        recruit.update(dto);

        // 모집 이미지 필터(게시글에 사용되지 않는 이미지 삭제)
        List<String> filteredNewImages = imageManager.deleteUnusedImagesAndFilterUsedImages(dto.getImages(), dto.getContent());
        // 모집 이미지 업데이트
        updateRecruitImages(recruit, filteredNewImages);

        // 모집 포지션 업데이트
        updateRecruitPositions(recruit, dto.getPositions());

        // 모집 기술스택 업데이트
        updateRecruitTechStacks(recruit, dto.getTechStacks());

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글의 수정이 완료되었습니다.", recruitId);

    }

    private void updateRecruitTechStacks(Recruit recruit, List<RecruitTechStackRequestDto> newTechStacks) {
        // 기존 모집 기술스택 삭제
        recruit.getRecruitTechStacks().clear();

        // new 모집 기술스택 조회
        List<String> techStackIds = newTechStacks.stream()
                .map(RecruitTechStackRequestDto::getTechStack)
                .toList();
        List<TechStack> techStacks = techStackRepository.findByTechStackIn(techStackIds);

        // new 모집 기술스택 생성
        List<RecruitTechStack> newRecruitTechStacks = techStacks.stream()
                .map(techStack -> RecruitTechStack.builder().techStack(techStack).build())
                .toList();

        // new 모집 기술스택 저장
        for (RecruitTechStack newRecruitTechStack : newRecruitTechStacks) {
            recruit.addRecruitTechStack(newRecruitTechStack);
        }

    }

    private void updateRecruitPositions(Recruit recruit, List<RecruitPositionRequestDto> newPositions) {
        // 기존 모집 포지션 삭제
        recruit.getRecruitPositions().clear();

        // new 모집 포지션 생성
        List<RecruitPosition> newRecruitPositions = newPositions.stream()
                .map(newPosition -> RecruitPosition.builder()
                        .position(newPosition.getPosition())
                        .amount(newPosition.getAmount())
                        .now(newPosition.getNow())
                        .build())
                .toList();

        // new 모집 포지션 저장
        for (RecruitPosition newRecruitPosition : newRecruitPositions) {
            recruit.addRecruitPosition(newRecruitPosition);
        }
    }

    private void updateRecruitImages(Recruit recruit, List<String> newImages) {
        // 기존 모집 이미지 삭제
        recruit.getRecruitImages().clear();

        // new 모집 이미지 생성
        List<RecruitImage> newRecruitImages = newImages.stream()
                .map(RecruitImage::new)
                .toList();

        // new 모집 이미지 저장
        for (RecruitImage newRecruitImage : newRecruitImages) {
            recruit.addRecruitImage(newRecruitImage);
        }
    }

    @Override
    @Transactional
    public SuccessResponseDto<Long> deleteRecruit(String memberId, Long recruitId) {
        // 모집 엔티티 조회
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new NoSuchElementException("모집 id(" + recruitId + ")가 존재하지 않습니다."));

        // 삭제 권한 조회
        Member member = recruit.getMember();
        if(!member.getId().equals(memberId) && !member.getRole().equals(Role.ADMIN))
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);

        // 모집 상태 변경(DELETE)
        recruit.changeStatus(Post.Status.DELETE);

        return new SuccessResponseDto<>(true, "모집 게시글 삭제가 완료되었습니다.", recruitId);
    }

    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitList(Pageable pageRequest,
                                                                                Recruit.Category category) {
        List<RecruitListReadResponseDto> data = null;

        // 모집 엔티티 조회
        Page<Recruit> pageable = recruitRepository.findByStatusAndCategory(Post.Status.PUBLIC, category, pageRequest);

        // 결과값이 없으면 204 반환
        if (!pageable.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        }

        // 모집 게시글 리스트 DTO // todo : N+1을 fetch join활용으로 해결하면서, DTO는 Setter를 활용해서 좀더 직관적으로 알수있도록 수정해볼것
        data = pageable.getContent().stream()
                        .map(recruit -> RecruitListReadResponseDto.builder()
                                .id(recruit.getId())
                                .postType(PostType.RECRUIT.name())
                                .category(recruit.getCategory().name())
                                .title(recruit.getTitle())
                                .content(recruit.getContent())
                                .amount(recruit.getAmount())
                                .now(recruit.getRecruitPositions().stream().mapToInt(RecruitPosition::getNow).sum())
                                .createdDate(recruit.getCreatedDate())
                                .memberNickname(recruit.getMember().getMemberNickname())
                                .memberProfile(recruit.getMember().getProfileUrl())
                                .techStacks(recruit.getRecruitTechStacks().stream()
                                        .map(techStack -> RecruitTechStackResponseDto.builder() // todo : 모집기술스택에서 기술스택을 조회하게되면 N+1 쿼리가 발생한다.
                                                .techStack(techStack.getTechStack().getTechStack())
                                                .iconUrl(techStack.getTechStack().getIconUrl())
                                                .build())
                                        .toList())
                                .build())
                        .toList();


        log.info("/recruit/service : readRecruitList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<RecruitReadResponseDto> readRecruit(String memberId, Long recruitId) {
        RecruitReadResponseDto data = null;

        // 맴버 엔티티 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("로그인 회원 아이디가 존재하지 않습니다."));

        // 모집 엔티티 조회
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new NoSuchElementException("모집 id(" + recruitId + ")가 존재하지 않습니다."));

        // 권한 조회
        if(!recruit.getStatus().equals(Post.Status.PUBLIC) && !recruit.getMember().getId().equals(memberId) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 모집 엔티티 toDto
        data = RecruitReadResponseDto.builder()
                .id(recruit.getId())
                .postType(PostType.RECRUIT.name())
                .memberId(recruit.getMember().getId())
                .memberNickName(recruit.getMember().getMemberNickname())
                .memberProfile(recruit.getMember().getProfileUrl())
                .viewCount(recruit.getViewCount())
                .category(recruit.getCategory().name())
                .contactWay(recruit.getContactWay().name())
                .contact(recruit.getContact())
                .amount(recruit.getAmount())
                .proceedWay(recruit.getProceedWay().name())
                .workDay(recruit.getWorkDay())
                .title(recruit.getTitle())
                .content(recruit.getContent())
                .createdDate(recruit.getCreatedDate())
                .modifiedDate(recruit.getModifiedDate())
                .images(recruit.getRecruitImages().stream().map(RecruitImage::getUrl).toList())
                .positions(recruit.getRecruitPositions().stream()
                        .map(recruitPosition -> RecruitPositionResponseDto.builder()
                                .id(recruitPosition.getId())
                                .position(recruitPosition.getPosition())
                                .amount(recruitPosition.getAmount())
                                .now(recruitPosition.getNow())
                                .build())
                        .toList())
                .techStacks(recruit.getRecruitTechStacks().stream()
                        .map(techStack -> RecruitTechStackResponseDto.builder() // todo : 모집기술스택에서 기술스택을 조회하게되면 N+1 쿼리가 발생한다.
                                .techStack(techStack.getTechStack().getTechStack())
                                .iconUrl(techStack.getTechStack().getIconUrl())
                                .build())
                        .toList())
                .build();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 디테일 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>> readDeadlineRecruitList() {
        List<DeadLineRecruitListReadResponseDto> data = null;

        // 마감일에 가까운 Top3 모집 게시글 가져오기
        List<Recruit> recruits = recruitRepository.findTop3ByStatusAndRecruitmentStatusOrderByEndAsc(Post.Status.PUBLIC, Recruit.RecruitmentStatus.RECRUITING);

        // 모집 마감 게시글 DTO
        data = recruits.stream()
                .map(recruit -> DeadLineRecruitListReadResponseDto.builder()
                        .id(recruit.getId())
                        .postType(PostType.RECRUIT.name())
                        .title(recruit.getTitle())
                        .content(recruit.getContent())
                        .end(recruit.getEnd())
                        .createdDate(recruit.getCreatedDate())
                        .modifiedDate(recruit.getModifiedDate())
                        .techStacks(recruit.getRecruitTechStacks().stream()
                                .map(techStack -> RecruitTechStackResponseDto.builder()
                                        .techStack(techStack.getTechStack().getTechStack())
                                        .iconUrl(techStack.getTechStack().getIconUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        log.info("/recruit/service : readDeadlineRecruitList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "마감 모집 리스트 조회가 완료되었습니다.", data);

    }

    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitListByMemberId(Pageable pageable, Recruit.Category category, String memberId, String memberId_param) {
        List<RecruitListReadResponseDto> data = null;

        Page<Recruit> recruitPage;
        // 맴버 엔티티 조회
        memberRepository.findById(memberId_param).orElseThrow(() -> new NoSuchElementException("회원 id("+memberId_param+")가 존재하지 않습니다."));

        // 로그인 사용자의 마이페이지면
        if(memberId.equals(memberId_param)) {
            // 모집 엔티티 조회 (비공개 포함)
            recruitPage = recruitRepository.findByMemberId(memberId_param, pageable);
        }else{
            // 모집 엔티티 조회 (공개글만)
            recruitPage = recruitRepository.findByMemberIdAndStatus(memberId_param, Post.Status.PUBLIC, pageable);
        }

        // 결과값이 없으면 204 반환
        if (!recruitPage.hasContent()) {
            throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        }

        // 모집 게시글 리스트 DTO // todo : N+1을 fetch join활용으로 해결하면서, DTO는 Setter를 활용해서 좀더 직관적으로 알수있도록 수정해볼것
        data = recruitPage.getContent().stream()
                .map(recruit -> RecruitListReadResponseDto.builder()
                        .id(recruit.getId())
                        .postType(PostType.RECRUIT.name())
                        .category(recruit.getCategory().name())
                        .title(recruit.getTitle())
                        .content(recruit.getContent())
                        .amount(recruit.getAmount())
                        .now(recruit.getRecruitPositions().stream().mapToInt(RecruitPosition::getNow).sum())
                        .createdDate(recruit.getCreatedDate())
                        .memberNickname(recruit.getMember().getMemberNickname())
                        .memberProfile(recruit.getMember().getProfileUrl())
                        .techStacks(recruit.getRecruitTechStacks().stream()
                                .map(techStack -> RecruitTechStackResponseDto.builder() // todo : 모집기술스택에서 기술스택을 조회하게되면 N+1 쿼리가 발생한다.
                                        .techStack(techStack.getTechStack().getTechStack())
                                        .iconUrl(techStack.getTechStack().getIconUrl())
                                        .build())
                                .toList())
                        .build())
                .toList();

        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "마이페이지 모집 게시글 리스트 조회가 완료되었습니다.", data);
    }
}
