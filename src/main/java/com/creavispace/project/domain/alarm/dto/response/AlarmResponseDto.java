package com.creavispace.project.domain.alarm.dto.response;

import com.creavispace.project.domain.alarm.entity.Alarm;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmResponseDto {
    private Long id;
    private String content;
    private Alarm.readStatus readStatus;
}
