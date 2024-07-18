package com.creavispace.project.domain.alarm.repository;

import com.creavispace.project.domain.alarm.entity.Alarm;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    public List<Alarm> findByMemberIdOrderByIdDesc(String memberId);

    List<Alarm> findByMemberId(String memberId);

    @Transactional
    void deleteByMemberId(String memberId);

    @Transactional
    void deleteByIdAndMemberId(Long alarmId, String memberId);

    int countByMemberIdAndReadStatus(String memberId, Alarm.readStatus readStatus);
}
