package com.creavispace.project.domain.recruit.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.common.dto.type.RecruitCategory;
import com.creavispace.project.domain.common.dto.type.RecruitContactWay;
import com.creavispace.project.domain.common.dto.type.RecruitProceedWay;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.recruit.dto.request.RecruitPositionRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitRequestDto;
import com.creavispace.project.domain.recruit.dto.request.RecruitTechStackRequestDto;
import com.creavispace.project.domain.recruit.dto.response.*;
import com.creavispace.project.domain.recruit.entity.Recruit;
import com.creavispace.project.domain.recruit.entity.RecruitPosition;
import com.creavispace.project.domain.recruit.entity.RecruitTechStack;
import com.creavispace.project.domain.recruit.repository.RecruitPositionRepository;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.domain.recruit.repository.RecruitTechStackRepository;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import com.creavispace.project.global.util.CustomValueOf;
import com.creavispace.project.global.util.TimeUtil;
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
public class RecruitServiceImpl implements RecruitService {

    private final MemberRepository memberRepository;
    private final RecruitRepository recruitRepository;
    private final RecruitPositionRepository recruitPositionRepository;
    private final TechStackRepository techStackRepository;
    private final RecruitTechStackRepository recruitTechStackRepository;

    @Override
    @Transactional
    public SuccessResponseDto<RecruitResponseDto> createRecruit(String memberId, RecruitRequestDto dto) {
        RecruitResponseDto data = null;
        RecruitCategory category = CustomValueOf.valueOf(RecruitCategory.class, dto.getCategory(), GlobalErrorCode.NOT_FOUND_RECRUIT_CATEGORY);
        RecruitContactWay contactWay = CustomValueOf.valueOf(RecruitContactWay.class, dto.getContactWay(), GlobalErrorCode.NOT_FOUND_RECRUIT_CONTACTWAY);
        RecruitProceedWay proceedWay = CustomValueOf.valueOf(RecruitProceedWay.class, dto.getProceedWay(), GlobalErrorCode.NOT_FOUND_RECRUIT_PROCEEDWAY);

        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 모집 게시글 생성
        Recruit recruit = Recruit.builder()
            .member(member)
            .category(category)
            .amount(dto.getAmount())
            .proceedWay(proceedWay)
            .workDay(dto.getWorkDay())
            .end(TimeUtil.getRecruitEnd(dto.getEnd(),dto.getEndFormat()))
            .contactWay(contactWay)
            .contact(dto.getContact())
            .title(dto.getTitle())
            .content(dto.getContent())
            .status(Boolean.TRUE)
            .viewCount(0)
            .build();

        // 모집 게시글 저장
        recruitRepository.save(recruit);

        List<RecruitPositionRequestDto> positionDtos = dto.getPositions();
        // 모집 포지션이 있다면
        if(positionDtos != null && !positionDtos.isEmpty()){
            List<RecruitPosition> positions = positionDtos.stream()
                .map(positionDto -> RecruitPosition.builder()
                    .position(positionDto.getPosition())
                    .amount(positionDto.getAmount())
                    .now(positionDto.getNow())
                    .status(Boolean.TRUE)
                    .recruit(recruit)
                    .build())
                .collect(Collectors.toList());
            // 모집 포지션 저장
            recruitPositionRepository.saveAll(positions);
        }

        List<RecruitTechStackRequestDto> techStackDtos = dto.getTechStacks();
        // 모집 기술스택이 있다면
        if(techStackDtos != null && !techStackDtos.isEmpty()){
            List<RecruitTechStack> techStacks = techStackDtos.stream()
                .map(techStackDto -> {
                    TechStack techStack = techStackRepository.findById(techStackDto.getTechStackId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.TECHSTACK_NOT_FOUND));

                    return RecruitTechStack.builder()
                        .techStack(techStack)
                        .recruit(recruit)
                        .build();
                })
                .collect(Collectors.toList());
            // 모집 기술스택 저장
            recruitTechStackRepository.saveAll(techStacks);
        }

        // 저장된 포지션, 기술스택 가져오기
        Long recruitId = recruit.getId();
        List<RecruitPosition> recruitPositions = recruitPositionRepository.findByRecruitId(recruitId);
        List<RecruitTechStack> recruitTechStacks = recruitTechStackRepository.findByRecruitId(recruitId);

        // 가져온 포지션 정보 DTO 변환
        List<RecruitPositionResponseDto> positions = recruitPositions.stream()
            .map(recruitPosition -> RecruitPositionResponseDto.builder()
                .id(recruitPosition.getId())
                .position(recruitPosition.getPosition())
                .amount(recruitPosition.getAmount())
                .now(recruitPosition.getNow())
                .build())
            .collect(Collectors.toList());

        // 가져온 기술스택 정보 DTO 변환
        List<RecruitTechStackResponseDto> techStacks = recruitTechStacks.stream()
            .map(recruitTechStack -> RecruitTechStackResponseDto.builder()
                .techStackId(recruitTechStack.getTechStack().getId())
                .techStack(recruitTechStack.getTechStack().getTechStack())
                .iconUrl(recruitTechStack.getTechStack().getIconUrl())
                .build())
            .collect(Collectors.toList());

        // 모집 게시글 생성 DTO
        data = RecruitResponseDto.builder()
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
            .end(recruit.getEnd())
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .createdDate(recruit.getCreatedDate())
            .modifiedDate(recruit.getModifiedDate())
            .positions(positions)
            .techStacks(techStacks)
            .build();

        log.info("/recruit/service : createRecruit success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 생성이 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<RecruitResponseDto> modifyRecruit(String memberId, Long recruitId, RecruitRequestDto dto) {
        RecruitResponseDto data = null;
        List<RecruitPositionRequestDto> positionDtos = dto.getPositions();
        List<RecruitTechStackRequestDto> techStackDtos = dto.getTechStacks();

        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 수정할 모집 게시글이 존재하는지
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        // 수정 권한이 있는지
        if(!memberId.equals(recruit.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 모집 게시글 수정 및 저장
        recruit.modify(dto);
        recruitRepository.save(recruit);

        // 기존 포지션 정보 삭제
        recruitPositionRepository.deleteByRecruitId(recruitId);

        // DTO에 포지션 정보가 존재하는지
        if(positionDtos != null && !positionDtos.isEmpty()){
            List<RecruitPosition> recruitPositions = positionDtos.stream()
                .map(positionDto -> RecruitPosition.builder()
                    .position(positionDto.getPosition())
                    .amount(positionDto.getAmount())
                    .now(positionDto.getNow())
                    .status(true)
                    .recruit(recruit)
                    .build())
                .collect(Collectors.toList());
            // 수정된 포지션 정보 저장
            recruitPositionRepository.saveAll(recruitPositions);
        }

        // 기존 기술스택 정보 삭제
        recruitTechStackRepository.deleteByRecruitId(recruitId);

        // DTO에 기술스택 정보가 존재하는지
        if(techStackDtos != null && !techStackDtos.isEmpty()){
            List<RecruitTechStack> recruitTechStacks = techStackDtos.stream()
                .map(techStackDto -> {
                    TechStack recruitTechStack = techStackRepository.findById(techStackDto.getTechStackId()).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

                    return RecruitTechStack.builder()
                        .techStack(recruitTechStack)
                        .recruit(recruit)
                        .build();
                })
                .collect(Collectors.toList());
            // 수정된 기술스택 정보 저장
            recruitTechStackRepository.saveAll(recruitTechStacks);
        }

        // 저장된 포지션, 기술스택 정보 가져오기
        List<RecruitPosition> recruitPositions = recruitPositionRepository.findByRecruitId(recruitId);
        List<RecruitTechStack> recruitTechStacks = recruitTechStackRepository.findByRecruitId(recruitId);

        // 가져온 포지션 정보 DTO로 변환
        List<RecruitPositionResponseDto> positions = recruitPositions.stream()
            .map(recruitPosition -> RecruitPositionResponseDto.builder()
                .id(recruitPosition.getId())
                .position(recruitPosition.getPosition())
                .amount(recruitPosition.getAmount())
                .now(recruitPosition.getNow())
                .build())
            .collect(Collectors.toList());

        // 가져온 기술스택 정보 DTO로 변환
        List<RecruitTechStackResponseDto> techStacks = recruitTechStacks.stream()
            .map(recruitTechStack -> RecruitTechStackResponseDto.builder()
                .techStackId(recruitTechStack.getTechStack().getId())
                .techStack(recruitTechStack.getTechStack().getTechStack())
                .iconUrl(recruitTechStack.getTechStack().getIconUrl())
                .build())
            .collect(Collectors.toList());

        // 모집 게시글 수정 DTO
        data = RecruitResponseDto.builder()
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
            .end(recruit.getEnd())
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .createdDate(recruit.getCreatedDate())
            .modifiedDate(recruit.getModifiedDate())
            .positions(positions)
            .techStacks(techStacks)
            .build();

        log.info("/recruit/service : modifyRecruit success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글의 수정이 완료되었습니다.", data);

    }

    @Override
    @Transactional
    public SuccessResponseDto<RecruitDeleteResponseDto> deleteRecruit(String memberId, Long recruitId) {
        RecruitDeleteResponseDto data = null;

        // JWT에 저장된 회원이 존재하는지
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        // 삭제할 모집 게시글이 존재하는지
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        // 삭제할 권한이 있는지
        if(!memberId.equals(recruit.getMember().getId()) && !member.getRole().equals(Role.ADMIN)){
            throw new CreaviCodeException(GlobalErrorCode.NOT_PERMISSMISSION);
        }

        // 모집 게시글 비활성화 및 저장
        recruit.disable();
        recruitRepository.save(recruit);

        // 모집 게시글 삭제 DTO
        data = RecruitDeleteResponseDto.builder()
            .recruitId(recruit.getId())
            .postType(PostType.RECRUIT.name())
            .build();

        log.info("/recruit/service : deleteRecruit success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 삭제가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitList(Integer size, Integer page, RecruitCategory category) {
        List<RecruitListReadResponseDto> data = null;
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Recruit> pageable;

        // 카테고리가 존재한다면
        if(category != null){
            pageable = recruitRepository.findAllByStatusTrueAndCategory(category, pageRequest);
        }
        // 카테고리가 없다면
        else{
            pageable = recruitRepository.findAllByStatusTrue(pageRequest);
        }

        // 조회된 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        List<Recruit> recruits = pageable.getContent();

        // 모집 게시글 리스트 DTO
        data = recruits.stream()
            .map(recruit -> RecruitListReadResponseDto.builder()
                    .id(recruit.getId())
                    .postType(PostType.RECRUIT.name())
                    .category(recruit.getCategory().name())
                    .title(recruit.getTitle())
                    .content(recruit.getContent())
                    .amount(recruit.getAmount())
                    .now(recruit.getPositions().stream()
                        .mapToInt(position -> position.getNow())
                        .sum())
                    .techStacks(recruit.getTechStacks().stream()
                        .map(techStack -> RecruitTechStackResponseDto.builder()
                            .techStackId(techStack.getTechStack().getId())
                            .techStack(techStack.getTechStack().getTechStack())
                            .iconUrl(techStack.getTechStack().getIconUrl())
                            .build())
                        .collect(Collectors.toList()))
                    .build())
            .collect(Collectors.toList());

        log.info("/recruit/service : readRecruitList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true,"모집 게시글 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    @Transactional
    public SuccessResponseDto<RecruitReadResponseDto> readRecruit(String memberId, Long recruitId, HttpServletRequest request) {
        RecruitReadResponseDto data = null;
        Optional<Recruit> optionalRecruit;

        // JWT 회원과 모집 게시글 작성자와 일치하는지
        boolean isWriter = recruitRepository.existsByIdAndMemberId(recruitId, memberId);
        // 일치하면 비활성화 모집 게시글도 조회가능
        if(isWriter){
            optionalRecruit = recruitRepository.findById(recruitId);
        }
        // 일치하지 않으면 활성화된 모집 게시글만 조회가능
        else{
            optionalRecruit = recruitRepository.findByIdAndStatusTrue(recruitId);
        }

        Recruit recruit = optionalRecruit.orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));

        // 조회수 증가
        String recruitViewLogHeader = request.getHeader("recruitView");
        // 헤더에 recruitView 값이 없으면
        if(recruitViewLogHeader == null){
            // 조회수 +1
            recruit.plusViewCount();
            recruitRepository.save(recruit);
            recruitViewLogHeader = "[" + recruitId + "]";
        }
        // 헤더에 recruitView 값이 있으면
        else{
            // 조회한적 없는 모집게시글일 경우
            if(!recruitViewLogHeader.contains("[" + recruitId + "]")){
                // 조회수 +1
            recruit.plusViewCount();
            recruitRepository.save(recruit);
            recruitViewLogHeader += "_[" + recruitId + "]";
            }
        }

        // 모집게시글 디테일 DTO
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
            .end(recruit.getEnd())
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .createdDate(recruit.getCreatedDate())
            .modifiedDate(recruit.getModifiedDate())
            .positions(recruit.getPositions().stream()
                .map(position -> RecruitPositionResponseDto.builder()
                    .id(position.getId())
                    .position(position.getPosition())
                    .amount(position.getAmount())
                    .now(position.getNow())
                    .build())
                .collect(Collectors.toList()))
            .techStacks(recruit.getTechStacks().stream()
                .map(techStack -> RecruitTechStackResponseDto.builder()
                    .techStackId(techStack.getTechStack().getId())
                    .techStack(techStack.getTechStack().getTechStack())
                    .iconUrl(techStack.getTechStack().getIconUrl())
                    .build())
                .collect(Collectors.toList()))
            .recruitViewLog(recruitViewLogHeader)
            .build();

        log.info("/recruit/service : readRecruit success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "모집 게시글 디테일 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<DeadLineRecruitListReadResponseDto>> readDeadlineRecruitList() {
        List<DeadLineRecruitListReadResponseDto> data = null;

        // 마감일에 가까운 Top3 모집 게시글 가져오기
        List<Recruit> recruits = recruitRepository.findTop3ByStatusTrueOrderByEndAsc();

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
                .techStacks(recruit.getTechStacks().stream()
                    .map(techStack -> RecruitTechStackResponseDto.builder()
                        .techStackId(techStack.getTechStack().getId())
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

    //ky

    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readMyRecruitList(Member member, Integer size,
                                                                                  Integer page, RecruitCategory category,
                                                                                  String sortType) {
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Recruit> pageable;

        // 카테고리가 존재한다면
        if(category != null){
            pageable = recruitRepository.findAllByStatusTrueAndCategory(category, pageRequest);
        }
        // 카테고리가 없다면
        else{
            pageable = recruitRepository.findAllByStatusTrue(pageRequest);
        }

        // 조회된 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        List<Recruit> recruits = pageable.getContent();

        // 모집 게시글 리스트 DTO
        List<RecruitListReadResponseDto> reads = recruits.stream()
                .map(recruit -> RecruitListReadResponseDto.builder()
                        .id(recruit.getId())
                        .postType(PostType.RECRUIT.name())
                        .category(recruit.getCategory().name())
                        .title(recruit.getTitle())
                        .content(recruit.getContent())
                        .amount(recruit.getAmount())
                        .now(recruit.getPositions().stream()
                                .mapToInt(position -> position.getNow())
                                .sum())
                        .techStacks(recruit.getTechStacks().stream()
                                .map(techStack -> RecruitTechStackResponseDto.builder()
                                        .techStackId(techStack.getTechStack().getId())
                                        .techStack(techStack.getTechStack().getTechStack())
                                        .iconUrl(techStack.getTechStack().getIconUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // 성공 응답 반환
        return new SuccessResponseDto<>(true,"모집 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readMyRecruitList(String memberId, Integer size,
                                                                                  Integer page, String sortType) {
        Pageable pageRequest = PageRequest.of(page-1, size);
        Page<Recruit> pageable;

            pageable = getRecruit(memberId, sortType, pageRequest);

        // 조회된 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        List<Recruit> recruits = pageable.getContent();

        // 모집 게시글 리스트 DTO
        List<RecruitListReadResponseDto> reads = recruits.stream()
                .map(recruit -> RecruitListReadResponseDto.builder()
                        .id(recruit.getId())
                        .postType(PostType.RECRUIT.name())
                        .category(recruit.getCategory().name())
                        .title(recruit.getTitle())
                        .content(recruit.getContent())
                        .amount(recruit.getAmount())
                        .now(recruit.getPositions().stream()
                                .mapToInt(position -> position.getNow())
                                .sum())
                        .techStacks(recruit.getTechStacks().stream()
                                .map(techStack -> RecruitTechStackResponseDto.builder()
                                        .techStackId(techStack.getTechStack().getId())
                                        .techStack(techStack.getTechStack().getTechStack())
                                        .iconUrl(techStack.getTechStack().getIconUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        // 성공 응답 반환
        return new SuccessResponseDto<>(true,"모집 게시글 리스트 조회가 완료되었습니다.", reads);
    }



    @Override
    public SuccessResponseDto<List<RecruitListReadResponseDto>> readRecruitListForAdmin(Integer size, Integer page,
                                                                                        String status, String sortType) {
        Pageable pageRequest = UsableConst.getPageRequest(size, page, sortType);
        Page<Recruit> pageable = findProject(status, pageRequest);

        // 조회된 게시글이 있는지
        if(!pageable.hasContent()) throw new CreaviCodeException(GlobalErrorCode.NOT_RECRUIT_CONTENT);
        List<Recruit> recruits = pageable.getContent();
        System.out.println("RecruitServiceImpl.readRecruitListForAdmin");

        // 모집 게시글 리스트 DTO
        List<RecruitListReadResponseDto> reads = getRecruitListReadResponseDtos(recruits);

        // 성공 응답 반환
        return new SuccessResponseDto<>(true,"모집 게시글 리스트 조회가 완료되었습니다.", reads);
    }

    private List<RecruitListReadResponseDto> getRecruitListReadResponseDtos(List<Recruit> recruits) {
        List<Long> recruitIds = recruits.stream().map(Recruit::getId).toList();
        List<RecruitTechStack> recruitTechStacks = recruitTechStackRepository.findByRecruitIdIn(recruitIds);
        List<RecruitPosition> recruitPositions = recruitPositionRepository.findByRecruitIdIn(recruitIds);

        Map<Long, List<RecruitTechStackResponseDto>> techStacks = techStacks(recruitTechStacks);
        Map<Long, Integer> sumOfPositions = sumOfPositions(recruitPositions);

        return recruits.stream()
                .map(recruit -> buildDto(recruit, techStacks, sumOfPositions))
                .collect(Collectors.toList());
    }


    private Page<Recruit> findProject(String status, Pageable pageRequest) {
        if (status.equalsIgnoreCase("all")) {
            return recruitRepository.findAll(pageRequest);
        }
        if (status.equalsIgnoreCase("false")) {
            return recruitRepository.findByStatusFalse(pageRequest);
        }
        if (status.equalsIgnoreCase("true")) {
            return recruitRepository.findAllByStatusTrue(pageRequest);
        }
        return recruitRepository.findAll(pageRequest);
    }


    private Page<Recruit> getRecruit(String memberId, String sortType, Pageable pageRequest) {
        Page<Recruit> pageable = recruitRepository.findByMemberIdAndStatusTrueOrderByCreatedDateAsc(memberId, pageRequest);
        if (sortType.toLowerCase().equals("asc")) {
            pageable = recruitRepository.findByMemberIdAndStatusTrueOrderByCreatedDateAsc(memberId, pageRequest);
        }
        if (sortType.toLowerCase().equals("desc")) {
            pageable = recruitRepository.findByMemberIdAndStatusTrueOrderByCreatedDateDesc(memberId, pageRequest);
        }
        return pageable;
    }

    private Map<Long, List<RecruitTechStackResponseDto>> techStacks(List<RecruitTechStack> recruitTechStacks) {
        Map<Long, List<RecruitTechStackResponseDto>> techStackMap = new HashMap<>();
            List<Long> collect = recruitTechStacks.stream().map(techStack -> techStack.getTechStack().getId())
                    .distinct().toList();
            techStackRepository.findByIdIn(collect);
        for (int i = 0; i < recruitTechStacks.size(); i++) {
            RecruitTechStack recruitTechStack = recruitTechStacks.get(i);
            List<RecruitTechStackResponseDto> links = techStackMap.getOrDefault(recruitTechStack.getRecruit().getId(),
                    new ArrayList<>());
            RecruitTechStackResponseDto recruitTechStackResponseDto = RecruitTechStackResponseDto.builder()
                    .techStackId(recruitTechStack.getTechStack().getId())
                    .techStack(recruitTechStack.getTechStack().getTechStack())
                    .iconUrl(recruitTechStack.getTechStack().getIconUrl())
                    .build();
            links.add(recruitTechStackResponseDto);
            techStackMap.put(recruitTechStack.getRecruit().getId(), links);
        }
        return techStackMap;
    }

    private Map<Long, Integer> sumOfPositions(List<RecruitPosition> recruitPositions) {
        Map<Long, Integer> sumMap = new HashMap<>();

        for (int i = 0; i < recruitPositions.size(); i++) {
            RecruitPosition recruitPosition = recruitPositions.get(i);
            Integer sum = sumMap.getOrDefault(recruitPosition.getRecruit().getId(), 0);
            sum += recruitPosition.getNow();
            sumMap.put(recruitPosition.getRecruit().getId(), sum);
        }
        return sumMap;
    }

    private RecruitListReadResponseDto buildDto(Recruit recruit,
                                                Map<Long, List<RecruitTechStackResponseDto>> techStacks,
                                                Map<Long, Integer> sumOfPositions) {
       return RecruitListReadResponseDto.builder()
                .id(recruit.getId())
                .postType(PostType.RECRUIT.name())
                .category(recruit.getCategory().name())
                .title(recruit.getTitle())
                .content(recruit.getContent())
                .amount(recruit.getAmount())
                .now(sumOfPositions.get(recruit.getId()))
                .techStacks(techStacks.get(recruit.getId()))
                .build();
    }
}
