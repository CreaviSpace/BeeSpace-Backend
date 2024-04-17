package com.creavispace.project.domain.hashTag.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.hashTag.entity.HashTag;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long>{
    public Boolean existsByHashTag(String hashTag);
    public HashTag findByHashTag(String hashTag);

    List<HashTag> findByIdIn(List<Long> collect);
}
