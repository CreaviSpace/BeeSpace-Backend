package com.creavispace.project.common.post.repository;

import com.creavispace.project.common.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:text% OR p.content LIKE %:text%")
    Page<Post> findBySearch(String text, Pageable pageable);
}
