package com.creavispace.project.domain.admin.dto.request;

import lombok.Getter;

@Getter
public class DeleteRequestDto {
    private Long id;
    private String category;

    public DeleteRequestDto(Long id, String category) {
        this.id = id;
        this.category = category;
    }
}
