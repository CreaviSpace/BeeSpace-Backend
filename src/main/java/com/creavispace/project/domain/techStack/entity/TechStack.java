package com.creavispace.project.domain.techStack.entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechStack extends BaseTimeEntity {
    @Id
    @Column(name = "tech_stack_id")
    private String techStack;

    private String iconUrl;

}
