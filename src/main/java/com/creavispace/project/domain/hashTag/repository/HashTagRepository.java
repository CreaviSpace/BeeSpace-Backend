package com.creavispace.project.domain.hashTag.repository;

import com.creavispace.project.domain.hashTag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long>{

    @Query(value = "SELECT h"
    + " FROM HashTag h"
    + " GROUP BY h.hashTag"
    + " ORDER BY COUNT(h.hashTag) DESC")
    public List<String> findTop3HashTagsByCount();
}
