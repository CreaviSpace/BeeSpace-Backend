package com.creavispace.project.domain.alarm.service;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.common.dto.type.PostType;

import java.util.List;

public interface AlarmService {
    public void createAlarm(String memberId, String alarmType, PostType postType, Long postId);

    public SuccessResponseDto<List<AlarmResponseDto>> readAlarmList(String memberId);

    public SuccessResponseDto<Void> modifyAlarm(String memberId, Long alarmId);

    SuccessResponseDto<Void> modifyAllAlarm(String memberId);

    SuccessResponseDto<Void> deleteAlarm(String memberId, Long alarmId);

    SuccessResponseDto<Void> deleteAllAlarm(String memberId);

    SuccessResponseDto<Integer> countUnReadAlarm(String memberId);
}
