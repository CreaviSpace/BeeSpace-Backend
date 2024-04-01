package com.creavispace.project.domain.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailResponseDto {

    private boolean success;
    private String message;
    private int statusCode;

}
