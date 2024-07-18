package com.creavispace.project.domain.hashTag.repository;

import com.creavispace.project.domain.hashTag.entity.CommunityHashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityHashTagRepository extends JpaRepository<CommunityHashTag, Long>{

    @Query(value = "SELECT hashTag"
    + " FROM CommunityHashTag"
    + " GROUP BY hashTag"
    + " ORDER BY COUNT(hashTag) DESC")
    public List<String> findTop3HashTagsByCount();
}
