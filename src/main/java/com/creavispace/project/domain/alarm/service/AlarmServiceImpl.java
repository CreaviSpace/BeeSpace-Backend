package com.creavispace.project.domain.alarm.service;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.alarm.entity.Alarm;
import com.creavispace.project.domain.alarm.repository.AlarmRepository;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.common.Post;
import com.creavispace.project.domain.community.repository.CommunityRepository;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.domain.project.repository.ProjectRepository;
import com.creavispace.project.domain.recruit.repository.RecruitRepository;
import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final CommunityRepository communityRepository;
    private final RecruitRepository recruitRepository;

    @Override
    public void createAlarm(String memberId, String alarmType, PostType postType, Long postId) {
        AlarmResponseDto data = null;
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));
        Post post;
        switch(postType){
            case PROJECT -> {
                post = projectRepository.findById(postId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.PROJECT_NOT_FOUND));
            }
            case COMMUNITY -> {
                post = communityRepository.findById(postId).orElseThrow(() -> new CreaviCodeException(GlobalErrorCode.COMMUNITY_NOT_FOUND));
            }
            case RECRUIT -> {
                post = recruitRepository.findById(postId).orElseThrow(()->new CreaviCodeException(GlobalErrorCode.RECRUIT_NOT_FOUND));
            }
            default -> throw new CreaviCodeException(GlobalErrorCode.NOT_FOUND_POST_TYPE);
        }

        String message = post.getTitle()+"게시글에 새로운 "+alarmType+"이 등록되었습니다.";
        Alarm alarm = Alarm.builder()
                .alarmMessage(message)
                .postType(postType)
                .postId(postId)
                .member(member)
                .readStatus(Alarm.readStatus.UNREAD)
                .build();

        alarmRepository.save(alarm);

        log.info("/alarm/service : 알림 생성 Service - data ={}", alarm);
    }

    @Override
    public SuccessResponseDto<List<AlarmResponseDto>> readAlarmList(String memberId) {
        List<AlarmResponseDto> data = null;

        List<Alarm> alarms = alarmRepository.findByMemberId(memberId);

        data = alarms.stream()
                .map(alarm -> AlarmResponseDto.builder()
                        .id(alarm.getId())
                        .alarmMessage(alarm.getAlarmMessage())
                        .postType(alarm.getPostType().name())
                        .postId(alarm.getPostId())
                        .readStatus(alarm.getReadStatus())
                        .build())
                .collect(Collectors.toList());

        log.info("/alarm/service : 알림 리스트 조회 Service - data = {}", data);
        return new SuccessResponseDto<>(true, "알림 리스트 조회가 완료되었습니다.", data);
    }

    @Transactional
    @Override
    public SuccessResponseDto<Void> modifyAlarm(String memberId, Long alarmId) {

        int count = alarmRepository.updateReadStatusToReadByIdAndMemberId(alarmId,memberId);

        log.info("/alarm/service : 알림 읽음 Service - alarmId = {}", alarmId);
        return new SuccessResponseDto<>(true, "알림 읽음 처리가 완료되었습니다.", null);
    }

    @Transactional
    @Override
    public SuccessResponseDto<Void> modifyAllAlarm(String memberId) {

        int count = alarmRepository.updateReadStatusToReadByMemberId(memberId);

        log.info("/alarm/service : 알림 전체 읽음 Service - {}개의 알림 읽음 처리", count);
        return new SuccessResponseDto<>(true, "알림 전체 읽음 처리가 완료되었습니다.", null);
    }

    @Override
    public SuccessResponseDto<Void> deleteAlarm(String memberId, Long alarmId) {

        alarmRepository.deleteByIdAndMemberId(alarmId, memberId);

        log.info("/alarm/service : 알림 삭제 Service - alarmId = {}", alarmId);
        return new SuccessResponseDto<>(true, "알림 삭제가 완료되었습니다.",null);
    }

    @Override
    public SuccessResponseDto<Void> deleteAllAlarm(String memberId) {

        alarmRepository.deleteByMemberId(memberId);

        log.info("/alarm/service : 알림 전체 삭제 Service");
        return new SuccessResponseDto<>(true, "알림 전체 삭제가 완료되었습니다.", null);
    }

    @Override
    public SuccessResponseDto<Integer> countUnReadAlarm(String memberId) {

        int count = alarmRepository.countByMemberIdAndReadStatus(memberId, Alarm.readStatus.UNREAD);

        log.info("/alarm/srevice : 읽지 않은 알림 수 조회 Service - count ={}", count);
        return new SuccessResponseDto<>(true, "읽지 않은 알림 수 조회가 완료되었습니다.", count);
    }
}
