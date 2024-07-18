package com.creavispace.project.domain.project.repository;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findTop6ByStatusOrderByWeekViewCountDesc(Post.Status status);
    @Modifying
    @Query(value = "UPDATE Project p set p.weekViewCount = 0")
    public void resetWeekViewCount();
    public Page<Project> findByStatusAndCategory(Post.Status status, Project.Category category, Pageable pageable);
    public Page<Project> findByMemberId(String memberId, Pageable pageable);
    public Page<Project> findByMemberIdAndStatus(String memberId, Post.Status status, Pageable pageable);

    @Query(value = "SELECT p FROM Project p WHERE p.title LIKE %:text% OR p.content LIKE %:text% AND p.category =:category")
    public Page<Post> findBySearch(@Param("text") String text, @Param("category")Project.Category category, Pageable pageable);

}
