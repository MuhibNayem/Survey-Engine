package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.scoring.dto.CategoryWeightRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileResponse;
import com.bracits.surveyengine.scoring.entity.CategoryWeight;
import com.bracits.surveyengine.scoring.entity.WeightProfile;
import com.bracits.surveyengine.scoring.repository.WeightProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link WeightProfileService}.
 * <p>
 * SRS §5.1, §5.3: Manages weight profiles with strict 100% sum validation.
 */
@Service
@RequiredArgsConstructor
public class WeightProfileServiceImpl implements WeightProfileService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    private final WeightProfileRepository weightProfileRepository;

    @Override
    @Transactional
    public WeightProfileResponse create(WeightProfileRequest request) {
        WeightProfile profile = WeightProfile.builder()
                .name(request.getName())
                .campaignId(request.getCampaignId())
                .build();

        if (request.getCategoryWeights() != null) {
            for (CategoryWeightRequest cwReq : request.getCategoryWeights()) {
                CategoryWeight cw = CategoryWeight.builder()
                        .weightProfile(profile)
                        .categoryId(cwReq.getCategoryId())
                        .weightPercentage(cwReq.getWeightPercentage())
                        .build();
                profile.getCategoryWeights().add(cw);
            }
        }

        profile = weightProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public WeightProfileResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeightProfileResponse> getByCampaignId(UUID campaignId) {
        return weightProfileRepository.findByCampaignIdAndActiveTrue(campaignId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WeightProfileResponse update(UUID id, WeightProfileRequest request) {
        WeightProfile profile = findOrThrow(id);
        profile.setName(request.getName());

        profile.getCategoryWeights().clear();
        if (request.getCategoryWeights() != null) {
            for (CategoryWeightRequest cwReq : request.getCategoryWeights()) {
                CategoryWeight cw = CategoryWeight.builder()
                        .weightProfile(profile)
                        .categoryId(cwReq.getCategoryId())
                        .weightPercentage(cwReq.getWeightPercentage())
                        .build();
                profile.getCategoryWeights().add(cw);
            }
        }

        profile = weightProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        WeightProfile profile = findOrThrow(id);
        profile.setActive(false);
        weightProfileRepository.save(profile);
    }

    /**
     * SRS §5.3: Validates that category weights sum to exactly 100%.
     * Throws INVALID_WEIGHT_SUM if not.
     */
    @Override
    @Transactional(readOnly = true)
    public void validateWeightSum(UUID profileId) {
        WeightProfile profile = findOrThrow(profileId);
        BigDecimal totalWeight = profile.getCategoryWeights().stream()
                .map(CategoryWeight::getWeightPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(ONE_HUNDRED) != 0) {
            throw new BusinessException(ErrorCode.INVALID_WEIGHT_SUM,
                    "Category weights sum to %s%%, must be exactly 100%%".formatted(totalWeight));
        }
    }

    private WeightProfile findOrThrow(UUID id) {
        return weightProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeightProfile", id));
    }

    private WeightProfileResponse toResponse(WeightProfile profile) {
        List<WeightProfileResponse.CategoryWeightResponse> weights = profile.getCategoryWeights().stream()
                .map(cw -> WeightProfileResponse.CategoryWeightResponse.builder()
                        .id(cw.getId())
                        .categoryId(cw.getCategoryId())
                        .weightPercentage(cw.getWeightPercentage())
                        .build())
                .toList();

        return WeightProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .campaignId(profile.getCampaignId())
                .active(profile.isActive())
                .categoryWeights(weights)
                .createdBy(profile.getCreatedBy())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
