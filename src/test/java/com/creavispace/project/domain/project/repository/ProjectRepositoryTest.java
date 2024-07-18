package com.creavispace.project.domain.project.repository;

import com.creavispace.project.common.post.entity.Post;
import com.creavispace.project.domain.project.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProjectRepositoryTest {
    @Autowired private ProjectRepository projectRepository;

    @Test
    void 인기게시글조회테스트(){
        List<Project> top6ByStatusOrderByWeekViewCountDesc = projectRepository.findTop6ByStatusOrderByWeekViewCountDesc(Post.Status.PUBLIC);
        System.out.println(top6ByStatusOrderByWeekViewCountDesc);
    }
}