package com.creavispace.project.config.auth;

import com.creavispace.project.config.auth.dto.LoginResponseDto;
import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;

public interface LoginService {
    public SuccessResponseDto<LoginResponseDto> naverLogin(String code, String state);
}
