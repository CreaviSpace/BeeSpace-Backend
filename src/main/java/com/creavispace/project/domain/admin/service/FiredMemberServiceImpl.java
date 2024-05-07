package com.creavispace.project.domain.admin.service;

import com.creavispace.project.domain.admin.entity.FiredMember;
import com.creavispace.project.domain.admin.repository.FiredMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Service
public class FiredMemberServiceImpl implements FiredMemberService {
    private final FiredMemberRepository firedMemberRepository;

    @Override
    public FiredMember findMember(String id) {
        return firedMemberRepository.findById(id).orElse(null);
    }
}
