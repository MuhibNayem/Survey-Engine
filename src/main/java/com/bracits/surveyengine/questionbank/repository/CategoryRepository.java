package com.bracits.surveyengine.questionbank.repository;

import com.bracits.surveyengine.questionbank.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findByActiveTrueAndTenantId(String tenantId, Pageable pageable);

    Optional<Category> findByIdAndTenantId(UUID id, String tenantId);
}
