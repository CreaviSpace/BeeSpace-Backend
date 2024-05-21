package com.creavispace.project.domain.jwt.Entity;

import com.creavispace.project.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@Table(name = "jwt")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Jwt extends BaseTimeEntity implements Persistable<String> {
    @Id
    @Column
    private String id;

    @Column(length = 1000)
    private String refreshToken;

    @Column
    private String memberId;

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}
