package com.creavispace.project.domain.admin.entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@Table(name = "fired_member")
@ToString
@NoArgsConstructor
public class FiredMember extends BaseTimeEntity implements Persistable<String> {
    @Id
    @Column(nullable = false, name = "id")
    private String id;
    @Column(nullable = false)
    private String reason;
    @Column(nullable = false)
    private LocalDateTime deadLine;

    public FiredMember(String id, String reason, LocalDateTime deadLine) {
        System.out.println("id = " + id);
        this.id = id;
        this.reason = reason;
        this.deadLine = deadLine;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}
