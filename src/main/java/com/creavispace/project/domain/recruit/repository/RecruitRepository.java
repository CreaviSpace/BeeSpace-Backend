package com.creavispace.project.domain.recruit.repository;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.recruit.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    public Page<Recruit> findByStatusAndCategory(Post.Status status, Recruit.Category category, Pageable pageable);
    public List<Recruit> findTop3ByStatusAndRecruitmentStatusOrderByEndAsc(Post.Status status, Recruit.RecruitmentStatus recruitmentStatus);
    public Page<Recruit> findByMemberId(String memberId, Pageable pageable);
    public Page<Recruit> findByMemberIdAndStatus(String memberId, Post.Status status, Pageable pageable);
    @Query(value = "SELECT r FROM Recruit r WHERE r.title LIKE %:text% OR r.content LIKE %:text% AND r.category =:category")
    public Page<Post> findBySearch(@Param("text") String text, @Param("category") Recruit.Category category, Pageable pageable);

}
