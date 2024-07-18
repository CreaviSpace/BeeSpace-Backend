package com.creavispace.project.domain.alarm.controller;

import com.creavispace.project.domain.alarm.dto.response.AlarmResponseDto;
import com.creavispace.project.domain.alarm.service.AlarmService;
import com.creavispace.project.common.dto.response.SuccessResponseDto;
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
    private static final String MODIFY_ALL_ALARM= "";
    private static final String DELETE_ALARM = "/{alarmId}";
    private static final String DELETE_ALL_ALARM = "";
    private static final String COUNT_ALARM = "/count";

    private final AlarmService alarmService;

    @GetMapping(READ_ALARM_LIST)
    @Operation(summary = "알림 리스트 조회")
    public ResponseEntity<SuccessResponseDto<List<AlarmResponseDto>>> readAlarmList(
            @AuthenticationPrincipal String memberId
    ){
        log.info("/alarm/controller : 알림 리스트 조회");
        return ResponseEntity.ok().body(alarmService.readAlarmList(memberId));
    }

    @PutMapping(MODIFY_ALARM)
    @Operation(summary = "알림 읽음")
    public ResponseEntity<SuccessResponseDto<Long>> modifyAlarm(
        @AuthenticationPrincipal String memberId,
        @PathVariable("alarmId") Long alarmId
    ){
        log.info("/alarm/controller : 알림 읽음");
        return ResponseEntity.ok().body(alarmService.modifyAlarm(memberId, alarmId));
    }

    @PutMapping(MODIFY_ALL_ALARM)
    @Operation(summary = "알림 전체 읽음")
    public ResponseEntity<SuccessResponseDto<Integer>> modifyAllAlarm(
            @AuthenticationPrincipal String memberId
    ){
        log.info("/alarm/controller : 알림 전체 읽음");
        return ResponseEntity.ok().body(alarmService.modifyAllAlarm(memberId));
    }

    @DeleteMapping(DELETE_ALARM)
    @Operation(summary = "알림 삭제")
    public ResponseEntity<SuccessResponseDto<Void>> deleteAlarm(
        @AuthenticationPrincipal String memberId,
        @PathVariable("alarmId") Long alarmId
    ){
        log.info("/alarm/controller : 알림 삭제");
        return ResponseEntity.ok().body(alarmService.deleteAlarm(memberId, alarmId));
    }

    @DeleteMapping(DELETE_ALL_ALARM)
    @Operation(summary = "알림 전체 삭제")
    public ResponseEntity<SuccessResponseDto<Void>> deleteAllAlarm(
            @AuthenticationPrincipal String memberId
    ){
        log.info("/alarm/controller : 알림 전체 삭제");
        return ResponseEntity.ok().body(alarmService.deleteAllAlarm(memberId));
    }
    
    @GetMapping(COUNT_ALARM)
    @Operation(summary = "읽지 않은 알림 수 조회")
    public ResponseEntity<SuccessResponseDto<Integer>> countUnReadAlarm(
            @AuthenticationPrincipal String memberId
    ){
        log.info("/alarm/controller : 읽지 않은 알림 수 조회");
        return ResponseEntity.ok().body(alarmService.countUnReadAlarm(memberId));
    }
}
