package com.creavispace.project.domain.alarm.service;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

import java.util.List;

public interface AlarmService {
    public SuccessResponseDto<AlarmResponseDto> createAlarm(Long memberId, String message);

    public SuccessResponseDto<List<AlarmResponseDto>> readAlarmList(Long memberId);

    public SuccessResponseDto<AlarmResponseDto> modifyAlarm(Long memberId, Long alarmId);
}
