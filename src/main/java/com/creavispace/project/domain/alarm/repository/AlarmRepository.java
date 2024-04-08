package com.creavispace.project.domain.alarm.repository;

import com.creavispace.project.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    public List<Alarm> findByMemberId(String memberId);
}
