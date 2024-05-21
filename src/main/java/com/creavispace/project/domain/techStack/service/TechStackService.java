package com.creavispace.project.domain.techStack.service;

import java.util.List;

import com.creavispace.project.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.techStack.dto.response.TechStackListReadResponseDto;

public interface TechStackService {
    public SuccessResponseDto<List<TechStackListReadResponseDto>> readTechStackList(String text);
}
