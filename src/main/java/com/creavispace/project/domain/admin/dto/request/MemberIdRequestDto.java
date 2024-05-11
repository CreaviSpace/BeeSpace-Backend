package com.creavispace.project.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberIdRequestDto {
    @JsonProperty("memberId")
    private String memberId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty
    private Integer period;

    //제재 사유
    public MemberIdRequestDto(String memberId, String reason, Integer period) {
        this.memberId = memberId;
        this.reason = reason;
        this.period = period;
    }
}
