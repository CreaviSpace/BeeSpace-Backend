package com.creavispace.project.domain.alarm.repository;

import com.creavispace.project.domain.alarm.entity.Alarm;
import com.creavispace.project.common.dto.type.PostType;
import com.creavispace.project.domain.member.Role;
import com.creavispace.project.domain.member.entity.Member;
import com.creavispace.project.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlarmRepositoryTest {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 유저의 알림 리스트 조회")
    void findByMemberId() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        List<Alarm> alarms = List.of(
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build()
        );

        alarmRepository.saveAll(alarms);

        // when
        List<Alarm> response = alarmRepository.findByMemberId(memberId);

        // then
        assertThat(response.size()).isEqualTo(3);
        assertThat(response).isNotNull();

    }

    @Test
    @DisplayName("해당 알림 ID의 알림 읽음 처리")
    void updateReadStatusToReadByIdAndMemberId() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        Alarm alarm = Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build();
        alarmRepository.save(alarm);

        // when
        int count = alarmRepository.updateReadStatusToReadByIdAndMemberId(alarm.getId(),memberId);
        Alarm response = alarmRepository.findById(alarm.getId()).orElse(null);

        // then
        assertThat(count).isEqualTo(1);
        assertThat(response).isNotNull();
        assertThat(response.getReadStatus()).isEqualTo(Alarm.readStatus.READ);

    }

    @Test
    @DisplayName("해당 MEMBER ID의 알림 전체 읽음 처리")
    void updateReadStatusToReadByMemberId() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        List<Alarm> alarms = List.of(
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build()
        );
        alarmRepository.saveAll(alarms);

        // when
        int count = alarmRepository.updateReadStatusToReadByMemberId(memberId);
        List<Alarm> response = alarmRepository.findByMemberId(memberId);

        // then
        assertThat(count).isEqualTo(3);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(3);
        for(Alarm r : response){
            assertThat(r.getReadStatus()).isEqualTo(Alarm.readStatus.READ);
        }
    }

    @Test
    @DisplayName("해당 알림 ID의 알림 삭제")
    void deleteByMemberId() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        Alarm alarm = Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build();
        alarmRepository.save(alarm);

        // when
        alarmRepository.deleteByIdAndMemberId(alarm.getId(), memberId);
        Alarm response = alarmRepository.findById(alarm.getId()).orElse(null);

        // then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("해당 MEMBER ID의 알림 전체 삭제")
    void deleteByIdAndMemberId() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        List<Alarm> alarms = List.of(
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build()
        );
        alarmRepository.saveAll(alarms);

        // when
        alarmRepository.deleteByMemberId(memberId);
        List<Alarm> response = alarmRepository.findByMemberId(memberId);

        // then
        assertThat(response).isEmpty();
    }

    @Test
    void countByMemberIdAndReadStatus() {
        // given
        String memberId = "member123";
        Member member = Member.builder().id(memberId).loginId("loginId").memberName("이름").loginType("네이버").role(Role.MEMBER).build();
        memberRepository.save(member);
        List<Alarm> alarms = List.of(
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build(),
                Alarm.builder().member(member).alarmMessage("message").readStatus(Alarm.readStatus.UNREAD).postType(PostType.PROJECT).postId(1L).build()
        );
        alarmRepository.saveAll(alarms);

        // when
        int count = alarmRepository.countByMemberIdAndReadStatus(memberId, Alarm.readStatus.UNREAD);

        // then
        assertThat(count).isEqualTo(3);
    }
}