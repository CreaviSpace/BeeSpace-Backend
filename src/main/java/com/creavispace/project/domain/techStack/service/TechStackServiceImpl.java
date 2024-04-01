package com.creavispace.project.domain.techStack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.techStack.dto.response.TechStackListReadResponseDto;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechStackServiceImpl implements TechStackService{

    private final TechStackRepository techStackRepository;

    @Override
    public SuccessResponseDto<List<TechStackListReadResponseDto>> readTechStackList(String text) {

        List<TechStack> techStacks;

        if(text == null){
            techStacks = techStackRepository.findAll();
        }else{
            // techStacks = techStackRepository.findByTechStackContains(text);
            techStacks = techStackRepository.findByTechStackStartingWith(text);
        }

        if(techStacks.isEmpty()){ // 등록된 기술스택이 없을때
            techStacks.add(techStackRepository.findById(1L).get()); // 기타 etc
        }

        List<TechStackListReadResponseDto> reads = techStacks.stream()
            .map(techStack-> TechStackListReadResponseDto.builder()
                .techStackId(techStack.getId())
                .techStack(techStack.getTechStack())
                .techStackIcon(techStack.getIconUrl())
                .build())
            .collect(Collectors.toList());

        return new SuccessResponseDto<>(true, "기술스택 조회가 완료되었습니다.", reads);
    }
    
}
