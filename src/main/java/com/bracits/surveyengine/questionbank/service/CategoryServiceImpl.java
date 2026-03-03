package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.dto.*;
import com.bracits.surveyengine.questionbank.entity.*;
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
 * Implementation of {@link CategoryService}.
 * <p>
 * SRS §4.2: "A survey/campaign shall use categories, not ad-hoc unmanaged
 * question lists."
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

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
                .build();
        category = categoryRepository.save(category);

        CategoryVersion version = createVersionSnapshot(category, request.getQuestionMappings());

        return toResponse(category, version);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        Category category = findOrThrow(id);
        CategoryVersion latestVersion = getLatestVersion(id);
        return toResponse(category, latestVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActive() {
        return categoryRepository.findByActiveTrueAndTenantId(TenantSupport.currentTenantOrDefault()).stream()
                .map(cat -> {
                    CategoryVersion version = categoryVersionRepository
                            .findTopByCategoryIdOrderByVersionNumberDesc(cat.getId())
                            .orElse(null);
                    return toResponse(cat, version);
                })
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setCurrentVersion(category.getCurrentVersion() + 1);
        category = categoryRepository.save(category);

        CategoryVersion version = createVersionSnapshot(category, request.getQuestionMappings());

        return toResponse(category, version);
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
    public CategoryVersion getLatestVersion(UUID categoryId) {
        return categoryVersionRepository
                .findTopByCategoryIdOrderByVersionNumberDesc(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryVersion", categoryId));
    }

    private CategoryVersion createVersionSnapshot(Category category,
            List<CategoryQuestionMappingRequest> mappingRequests) {
        CategoryVersion version = CategoryVersion.builder()
                .categoryId(category.getId())
                .versionNumber(category.getCurrentVersion())
                .name(category.getName())
                .description(category.getDescription())
                .questionMappings(new ArrayList<>())
                .build();

        if (mappingRequests != null) {
            for (CategoryQuestionMappingRequest mr : mappingRequests) {
                QuestionVersion qv = questionService.getLatestVersion(mr.getQuestionId());
                CategoryQuestionMapping mapping = CategoryQuestionMapping.builder()
                        .categoryVersion(version)
                        .questionId(mr.getQuestionId())
                        .questionVersionId(qv.getId())
                        .sortOrder(mr.getSortOrder())
                        .weight(mr.getWeight())
                        .build();
                version.getQuestionMappings().add(mapping);
            }
        }

        return categoryVersionRepository.save(version);
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
