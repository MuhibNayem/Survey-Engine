package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.questionbank.dto.CategoryQuestionMappingRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryResponse;
import com.bracits.surveyengine.questionbank.entity.CategoryVersion;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for managing categories, their versions, and question
 * mappings.
 * SRS §4.2
 */
public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse getById(UUID id);

    Page<CategoryResponse> getAllActive(Pageable pageable);

    CategoryResponse update(UUID id, CategoryRequest request);

    void deactivate(UUID id);

    CategoryVersion getLiveVersion(UUID categoryId);
}
