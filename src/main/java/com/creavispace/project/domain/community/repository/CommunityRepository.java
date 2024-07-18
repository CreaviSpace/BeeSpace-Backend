package com.creavispace.project.domain.community.repository;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.community.entity.Community;
import com.creavispace.project.domain.community.entity.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    public Page<Community> findByStatusAndCategory(Post.Status status, CommunityCategory category, Pageable pageable);
    public Page<Community> findByMemberId(String memberId, Pageable pageable);
    public Page<Community> findByMemberIdAndStatus(String memberId, Post.Status status, Pageable pageable);
    @Query(value = "SELECT c FROM Community c WHERE c.title LIKE %:text% OR c.content LIKE %:text% AND c.category =:category")
    public Page<Post> findBySearch(@Param("text") String text, @Param("category") CommunityCategory category, Pageable pageable);

}
