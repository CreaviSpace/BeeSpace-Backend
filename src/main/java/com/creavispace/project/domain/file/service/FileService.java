package com.creavispace.project.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.file.dto.response.UploadFileResponseDto;

public interface FileService {
    public SuccessResponseDto<UploadFileResponseDto> fileUpload(MultipartFile multipartFile);
}
