package com.creavispace.project.config.auth.dto;

import com.creavispace.project.global.dto.Dto;
import java.time.LocalDateTime;
import lombok.Getter;

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
