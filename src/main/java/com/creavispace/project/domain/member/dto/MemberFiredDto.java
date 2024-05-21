package com.creavispace.project.domain.member.dto;

import com.creavispace.project.common.Dto;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberFiredDto implements Dto {
    private String message;
    private String reason;
    private LocalDateTime since;
    private LocalDateTime deadLine;

    public MemberFiredDto(String message, String reason, LocalDateTime since, LocalDateTime deadLine) {
        this.message = message;
        this.reason = reason;
        this.since = since;
        this.deadLine = deadLine;
    }
}
