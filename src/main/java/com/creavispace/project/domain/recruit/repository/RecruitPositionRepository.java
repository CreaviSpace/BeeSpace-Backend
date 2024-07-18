package com.creavispace.project.domain.recruit.repository;

import com.creavispace.project.domain.recruit.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitPositionRepository extends JpaRepository<Position, Long> {
    public List<Position> findByRecruitId(Long recruitId);
    public void deleteByRecruitId(Long recruitId);
    @Query(value = "SELECT SUM(now) FROM recruit_position WHERE recruit_id = :recruitId", nativeQuery = true)
    public Integer countByNow(@Param("recruitId") Long recruitId);

    List<Position> findByRecruitIdIn(List<Long> recruitIds);
}
