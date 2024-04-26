package com.creavispace.project.domain.alarm.service;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.alarm.entity.Alarm;
import com.creavispace.project.domain.alarm.repository.AlarmRepository;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AlarmServiceImplTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AlarmServiceImpl alarmService;

    @Test
    @DisplayName("알림 생성 행위 검증")
    void createAlarm() {
        // given
        String memberId = "member123";
        String alarmType = "댓글";
        PostType postType = PostType.PROJECT;
        Long postId = 1L;
        String alarmMessage = postType.name()+" "+postId+"번 게시글에 새로운 "+alarmType+"이 등록되었습니다.";

        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").build();
        Alarm alarm = Alarm.builder().alarmMessage(alarmMessage).postType(postType).postId(postId).member(member).readStatus(Alarm.readStatus.UNREAD).build();

        // when
        Mockito.when(memberRepository.findById(memberId)).thenReturn(Optional.ofNullable(member));
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        alarmService.createAlarm(memberId, alarmType, PostType.PROJECT, postId);

        // then
        Mockito.verify(memberRepository, times(1)).findById(memberId);
        Mockito.verify(alarmRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("알림 리스트 조회")
    void readAlarmList() {
        // given
        String memberId = "member123";
        List<Alarm> alarms = new ArrayList<>();
        for(long i=1; i<=3; i++){
           alarms.add(Alarm.builder().id(i).alarmMessage("message").postType(PostType.PROJECT).postId(i).readStatus(Alarm.readStatus.UNREAD).build());
        }

        // when
        Mockito.when(alarmRepository.findByMemberId(memberId)).thenReturn(alarms);

        SuccessResponseDto<List<AlarmResponseDto>> response = alarmService.readAlarmList(memberId);

        // then
        Mockito.verify(alarmRepository,times(1)).findByMemberId(memberId);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("알림 리스트 조회가 완료되었습니다.");
        assertThat(response.getData().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void modifyAlarm() {
        // given
        String memberId = "member123";
        Long alarmId = 1L;

        // when
        Mockito.when(alarmRepository.updateReadStatusToReadByIdAndMemberId(alarmId,memberId)).thenReturn(1);

        SuccessResponseDto<Void> response = alarmService.modifyAlarm(memberId,alarmId);

        // then
        Mockito.verify(alarmRepository,times(1)).updateReadStatusToReadByIdAndMemberId(alarmId, memberId);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("알림 읽음 처리가 완료되었습니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("알림 전체 읽음 처리")
    void modifyAllAlarm() {
        // given
        String memberId = "member123";

        // when
        Mockito.when(alarmRepository.updateReadStatusToReadByMemberId(memberId)).thenReturn(3);

        SuccessResponseDto<Void> response = alarmService.modifyAllAlarm(memberId);

        // then
        Mockito.verify(alarmRepository,times(1)).updateReadStatusToReadByMemberId(memberId);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("알림 전체 읽음 처리가 완료되었습니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("알림 삭제")
    void deleteAlarm() {
        // given
        String memberId = "member123";
        Long alarmId = 1L;

        // when
        SuccessResponseDto<Void> response = alarmService.deleteAlarm(memberId, alarmId);

        // then
        Mockito.verify(alarmRepository,times(1)).deleteByIdAndMemberId(alarmId, memberId);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("알림 삭제가 완료되었습니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("알림 전체 삭제")
    void deleteAllAlarm() {
        // given
        String memberId = "member123";

        // when
        SuccessResponseDto<Void> response = alarmService.deleteAllAlarm(memberId);

        // then
        Mockito.verify(alarmRepository,times(1)).deleteByMemberId(memberId);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("알림 전체 삭제가 완료되었습니다.");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("읽지 않은 알림수 조회")
    void countUnReadAlarm() {
        // given
        String memberId = "member123";

        // when
        Mockito.when(alarmRepository.countByMemberIdAndReadStatus(memberId, Alarm.readStatus.UNREAD)).thenReturn(4);

        SuccessResponseDto<Integer> response = alarmService.countUnReadAlarm(memberId);

        // then
        Mockito.verify(alarmRepository, times(1)).countByMemberIdAndReadStatus(memberId, Alarm.readStatus.UNREAD);
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getMessage()).isEqualTo("읽지 않은 알림 수 조회가 완료되었습니다.");
        assertThat(response.getData()).isEqualTo(4);
    }
}