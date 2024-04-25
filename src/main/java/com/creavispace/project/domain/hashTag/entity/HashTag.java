package com.creavispace.project.domain.hashTag.entity;

import com.creavispace.project.domain.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HashTag extends BaseTimeEntity implements Persistable<String> {
    @Id
    private String hashTag;

    @Builder.Default
    private int usageCount = 0;

    @OneToMany(mappedBy = "hashTag")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CommunityHashTag> communityHashTags;

    public void plusUsageCount(){
        this.usageCount++;
    }

    @Override
    public String getId() {
        return this.hashTag;
    }

    @Override
    public boolean isNew() {
        return super.getCreatedDate() == null;
    }
}
