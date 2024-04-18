package com.creavispace.project.domain.techStack.service;

import com.creavispace.project.domain.common.dto.response.SuccessResponseDto;
import com.creavispace.project.domain.techStack.dto.response.TechStackListReadResponseDto;
import com.creavispace.project.domain.techStack.entity.TechStack;
import com.creavispace.project.domain.techStack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechStackServiceImpl implements TechStackService{

    private final TechStackRepository techStackRepository;

    @Override
    public SuccessResponseDto<List<TechStackListReadResponseDto>> readTechStackList(String text) {
        List<TechStackListReadResponseDto> data = null;
        List<TechStack> techStacks;

        if(text == null){
            techStacks = techStackRepository.findAll();
        }else{
            techStacks = techStackRepository.findByTechStackStartingWith(text);
        }

        if(techStacks.isEmpty()){ // 등록된 기술스택이 없을때
            techStacks.add(techStackRepository.findById("기타").get()); // 기타 etc
        }

        data = techStacks.stream()
            .map(techStack-> TechStackListReadResponseDto.builder()
                .techStack(techStack.getTechStack())
                .techStackIcon(techStack.getIconUrl())
                .build())
            .collect(Collectors.toList());

        log.info("/techStack/service : readTechStackList success data = {}", data);
        // 성공 응답 반환
        return new SuccessResponseDto<>(true, "기술스택 조회가 완료되었습니다.", data);
    }
    
}
