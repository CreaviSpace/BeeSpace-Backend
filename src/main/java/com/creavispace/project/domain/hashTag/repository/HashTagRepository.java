package com.creavispace.project.domain.hashTag.repository;

import com.creavispace.project.domain.hashTag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, String>{
    public Optional<HashTag> findByHashTag(String hashTag);
    List<HashTag> findByHashTagIn(List<String> collect);
}
