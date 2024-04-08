package com.creavispace.project.domain.alarm.controller;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.alarm.service.AlarmService;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {

    private static final String READ_ALARM_LIST = "";
    private static final String MODIFY_ALARM = "/{alarmId}";

    private final AlarmService alarmService;

    @GetMapping(READ_ALARM_LIST)
    @Operation(summary = "알림 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<AlarmResponseDto>>> readAlarmList(
            @AuthenticationPrincipal Long memberId
    ){
        log.info("/alarm/controller : 알림 리스트 조회");
        return ResponseEntity.ok().body(alarmService.readAlarmList(memberId));
    }

    @PutMapping(MODIFY_ALARM)
    @Operation(summary = "알림 읽음 처리")
    public ResponseEntity<SuccessResponseDto<AlarmResponseDto>> modifyAlarm(
        @AuthenticationPrincipal Long memberId,
        @PathVariable("alarmId") Long alarmId
    ){
        log.info("/alarm/controller : 알림 읽음 처리");
        return ResponseEntity.ok().body(alarmService.modifyAlarm(memberId, alarmId));
    }
}
