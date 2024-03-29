package com.creavispace.project.domain.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.creavispace.project.domain.feedback.entity.SelectedItem;

@Repository
public interface SelectedItemRepository extends JpaRepository<SelectedItem, Long> {
    public int countByChoiceItemId(Long choiceItemId);
}
