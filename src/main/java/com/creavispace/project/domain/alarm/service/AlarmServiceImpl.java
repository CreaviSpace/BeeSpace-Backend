package com.creavispace.project.domain.alarm.service;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.alarm.entity.Alarm;
import com.creavispace.project.domain.alarm.repository.AlarmRepository;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService{

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    @Override
    public SuccessResponseDto<AlarmResponseDto> createAlarm(String memberId, String alarmType, PostType postType, Long postId) {
        AlarmResponseDto data = null;
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND));

        String message = postType.name()+" "+postId+"번 게시글에 새로운 "+alarmType+"이 등록되었습니다.";
        Alarm alarm = Alarm.builder()
                .alarmMessage(message)
                .postType(postType)
                .postId(postId)
                .member(member)
                .readStatus(Alarm.readStatus.UNREAD)
                .build();

        alarmRepository.save(alarm);

        data = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmMessage(alarm.getAlarmMessage())
                .postType(postType.name())
                .postId(postId)
                .readStatus(alarm.getReadStatus())
                .build();
        log.info("/alarm/service : createAlarm success data ={}", data);
        return new SuccessResponseDto<>(true, "알림이 저장되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<List<AlarmResponseDto>> readAlarmList(String memberId) {
        List<AlarmResponseDto> data = null;
        if(!memberRepository.existsById(memberId)) throw new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND);

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

        log.info("/alarm/service : readAlarmList success data ={}", data);
        return new SuccessResponseDto<>(true, "알림 리스트 조회가 완료되었습니다.", data);
    }

    @Override
    public SuccessResponseDto<AlarmResponseDto> modifyAlarm(String memberId, Long alarmId) {
        AlarmResponseDto data = null;

        if(!memberRepository.existsById(memberId)) throw new CreaviCodeException(GlobalErrorCode.MEMBER_NOT_FOUND);

        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(()-> new CreaviCodeException(GlobalErrorCode.NOT_FOUND_ALARM));

        alarm.updateViewed();
        alarmRepository.save(alarm);

        data = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmMessage(alarm.getAlarmMessage())
                .postId(alarm.getPostId())
                .postType(alarm.getPostType().name())
                .readStatus(alarm.getReadStatus())
                .build();

        log.info("/alarm/service : modifyAlarm success data = {}", data);
        return new SuccessResponseDto<>(true, "알림 읽음 처리가 완료되었습니다.", data);
    }
}
