package com.creavispace.project.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.file.dto.response.UploadFileResponseDto;

import java.util.List;

public interface FileService {
    public SuccessResponseDto<UploadFileResponseDto> fileUpload(MultipartFile multipartFile);

    public void deleteImages(List<String> deletedImg);
}
