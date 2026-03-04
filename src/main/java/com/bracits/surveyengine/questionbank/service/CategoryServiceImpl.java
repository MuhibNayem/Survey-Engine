package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.dto.CategoryQuestionMappingRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryResponse;
import com.bracits.surveyengine.questionbank.entity.Category;
import com.bracits.surveyengine.questionbank.entity.CategoryQuestionMapping;
import com.bracits.surveyengine.questionbank.entity.CategoryVersion;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.CategoryRepository;
import com.bracits.surveyengine.questionbank.repository.CategoryVersionRepository;
import com.bracits.surveyengine.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Category bank keeps a mutable live definition (version=1).
 * Survey authoring creates pinned category versions separate from this flow.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final int LIVE_VERSION_NUMBER = 1;

    private final CategoryRepository categoryRepository;
    private final CategoryVersionRepository categoryVersionRepository;
    private final QuestionService questionService;
    private final TenantService tenantService;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        tenantService.ensureProvisioned(tenantId);

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .tenantId(tenantId)
                .currentVersion(LIVE_VERSION_NUMBER)
                .build();
        category = categoryRepository.save(category);

        CategoryVersion liveVersion = upsertLiveVersion(category, request.getQuestionMappings());
        return toResponse(category, liveVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        Category category = findOrThrow(id);
        CategoryVersion liveVersion = getLiveVersion(id);
        return toResponse(category, liveVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActive() {
        return categoryRepository.findByActiveTrueAndTenantId(TenantSupport.currentTenantOrDefault()).stream()
                .map(category -> toResponse(category, getLiveVersion(category.getId())))
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setCurrentVersion(LIVE_VERSION_NUMBER);
        category = categoryRepository.save(category);

        CategoryVersion liveVersion = upsertLiveVersion(category, request.getQuestionMappings());
        return toResponse(category, liveVersion);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Category category = findOrThrow(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryVersion getLiveVersion(UUID categoryId) {
        findOrThrow(categoryId);
        return categoryVersionRepository
                .findByCategoryIdAndVersionNumber(categoryId, LIVE_VERSION_NUMBER)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryVersion", categoryId));
    }

    private CategoryVersion upsertLiveVersion(Category category, List<CategoryQuestionMappingRequest> mappingRequests) {
        CategoryVersion liveVersion = categoryVersionRepository
                .findByCategoryIdAndVersionNumber(category.getId(), LIVE_VERSION_NUMBER)
                .orElse(null);

        if (liveVersion == null) {
            liveVersion = CategoryVersion.builder()
                    .categoryId(category.getId())
                    .versionNumber(LIVE_VERSION_NUMBER)
                    .name(category.getName())
                    .description(category.getDescription())
                    .questionMappings(new ArrayList<>())
                    .build();
        } else {
            liveVersion.setName(category.getName());
            liveVersion.setDescription(category.getDescription());
            liveVersion.getQuestionMappings().clear();
        }

        if (mappingRequests != null) {
            for (CategoryQuestionMappingRequest mr : mappingRequests) {
                QuestionVersion liveQuestionVersion = questionService.getLiveVersion(mr.getQuestionId());
                CategoryQuestionMapping mapping = CategoryQuestionMapping.builder()
                        .categoryVersion(liveVersion)
                        .questionId(mr.getQuestionId())
                        .questionVersionId(liveQuestionVersion.getId())
                        .sortOrder(mr.getSortOrder())
                        .weight(mr.getWeight())
                        .build();
                liveVersion.getQuestionMappings().add(mapping);
            }
        }

        return categoryVersionRepository.save(liveVersion);
    }

    private Category findOrThrow(UUID id) {
        return categoryRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private CategoryResponse toResponse(Category cat, CategoryVersion version) {
        List<CategoryResponse.QuestionMappingResponse> mappings = List.of();
        if (version != null && version.getQuestionMappings() != null) {
            mappings = version.getQuestionMappings().stream()
                    .map(m -> CategoryResponse.QuestionMappingResponse.builder()
                            .questionId(m.getQuestionId())
                            .questionVersionId(m.getQuestionVersionId())
                            .sortOrder(m.getSortOrder())
                            .weight(m.getWeight())
                            .build())
                    .toList();
        }

        return CategoryResponse.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .currentVersion(cat.getCurrentVersion())
                .active(cat.isActive())
                .questionMappings(mappings)
                .createdBy(cat.getCreatedBy())
                .createdAt(cat.getCreatedAt())
                .updatedBy(cat.getUpdatedBy())
                .updatedAt(cat.getUpdatedAt())
                .build();
    }
}
