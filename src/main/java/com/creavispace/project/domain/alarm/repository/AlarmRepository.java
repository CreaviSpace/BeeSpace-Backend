package com.creavispace.project.domain.alarm.repository;

import com.creavispace.project.domain.alarm.entity.Alarm;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    public List<Alarm> findByMemberIdOrderByIdDesc(String memberId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update alarm a SET a.read_status = 'READ' where a.id = :alarmId and a.member_id = :memberId and a.read_status = 'UNREAD'",nativeQuery = true)
    int updateReadStatusToReadByIdAndMemberId(Long alarmId, String memberId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update alarm a SET a.read_status = 'READ' WHERE a.member_id = :memberId AND a.read_status = 'UNREAD'", nativeQuery = true)
    int updateReadStatusToReadByMemberId(String memberId);

    @Modifying
    @Transactional
    @Query(value = "delete from alarm where member_id = :memberId", nativeQuery = true)
    void deleteByMemberId(String memberId);

    @Transactional
    void deleteByIdAndMemberId(Long alarmId, String memberId);

    int countByMemberIdAndReadStatus(String memberId, Alarm.readStatus readStatus);
}
