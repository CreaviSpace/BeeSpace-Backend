package com.creavispace.project.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DeleteRequestDto {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String category;

    public DeleteRequestDto(Long id, String category) {
        this.id = id;
        this.category = category;
    }
}
