package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.scoring.dto.CategoryWeightRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileResponse;
import com.bracits.surveyengine.scoring.entity.CategoryWeight;
import com.bracits.surveyengine.scoring.entity.WeightProfile;
import com.bracits.surveyengine.scoring.repository.WeightProfileRepository;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.repository.SurveySnapshotRepository;
import com.bracits.surveyengine.tenant.service.TenantService;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link WeightProfileService}.
 * <p>
 * SRS §5.1, §5.3: Manages weight profiles with strict 100% sum validation.
 */
@Service
@RequiredArgsConstructor
public class WeightProfileServiceImpl implements WeightProfileService {

    private static final String DEFAULT_PROFILE_NAME = "Default";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    private final WeightProfileRepository weightProfileRepository;
    private final CampaignRepository campaignRepository;
    private final TenantService tenantService;
    private final SurveySnapshotRepository surveySnapshotRepository;
    private final ObjectMapper objectMapper;
    private final PlanQuotaService planQuotaService;

    @Override
    @Transactional
    public WeightProfileResponse create(WeightProfileRequest request) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        tenantService.ensureProvisioned(tenantId);
        planQuotaService.enforceWeightProfileAccess(tenantId);
        campaignRepository.findByIdAndTenantId(request.getCampaignId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", request.getCampaignId()));
        WeightProfile profile = WeightProfile.builder()
                .name(request.getName())
                .campaignId(request.getCampaignId())
                .tenantId(tenantId)
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
        validateWeightSum(profile.getId());
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
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));
        return weightProfileRepository.findByCampaignIdAndActiveTrueAndTenantId(campaignId, tenantId).stream()
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
        validateWeightSum(profile.getId());
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

    @Override
    @Transactional
    public UUID upsertDefaultProfileFromSurveySnapshot(UUID campaignId, UUID surveySnapshotId) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        Map<UUID, BigDecimal> categoryWeights = extractCategoryWeights(surveySnapshotId);
        if (categoryWeights.isEmpty()) {
            return null;
        }

        WeightProfile profile = weightProfileRepository
                .findByCampaignIdAndNameAndTenantId(campaignId, DEFAULT_PROFILE_NAME, tenantId)
                .orElseGet(() -> WeightProfile.builder()
                        .name(DEFAULT_PROFILE_NAME)
                        .campaignId(campaignId)
                        .tenantId(tenantId)
                        .active(true)
                        .build());

        profile.setActive(true);
        profile.getCategoryWeights().clear();
        for (Map.Entry<UUID, BigDecimal> entry : categoryWeights.entrySet()) {
            profile.getCategoryWeights().add(CategoryWeight.builder()
                    .weightProfile(profile)
                    .categoryId(entry.getKey())
                    .weightPercentage(entry.getValue())
                    .build());
        }

        profile = weightProfileRepository.save(profile);
        validateWeightSum(profile.getId());
        return profile.getId();
    }

    private WeightProfile findOrThrow(UUID id) {
        return weightProfileRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
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

    private Map<UUID, BigDecimal> extractCategoryWeights(UUID surveySnapshotId) {
        SurveySnapshot snapshot = surveySnapshotRepository.findById(surveySnapshotId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveySnapshot", surveySnapshotId));
        Map<UUID, BigDecimal> categoryWeights = new LinkedHashMap<>();
        boolean hasExplicitWeights = false;

        try {
            JsonNode root = objectMapper.readTree(snapshot.getSnapshotData());
            JsonNode pagesNode = root.path("pages");
            if (!pagesNode.isArray()) {
                return Map.of();
            }

            for (JsonNode pageNode : pagesNode) {
                JsonNode questionsNode = pageNode.path("questions");
                if (!questionsNode.isArray()) {
                    continue;
                }
                for (JsonNode questionNode : questionsNode) {
                    if (!questionNode.hasNonNull("categoryId")) {
                        continue;
                    }
                    UUID categoryId = UUID.fromString(questionNode.path("categoryId").asText());
                    BigDecimal weight = readOptionalDecimal(questionNode.get("categoryWeightPercentage"));
                    if (weight != null) {
                        hasExplicitWeights = true;
                        if (weight.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                    "categoryWeightPercentage must be > 0 in snapshot");
                        }
                    }

                    BigDecimal previous = categoryWeights.putIfAbsent(categoryId, weight);
                    if (previous != null) {
                        if (previous == null && weight != null) {
                            categoryWeights.put(categoryId, weight);
                            continue;
                        }
                        if (previous != null && weight != null && previous.compareTo(weight) != 0) {
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                    "Inconsistent categoryWeightPercentage for category " + categoryId);
                        }
                    }
                }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "Unable to parse survey snapshot category weights");
        }

        if (categoryWeights.isEmpty()) {
            return Map.of();
        }

        if (!hasExplicitWeights || categoryWeights.values().stream().anyMatch(v -> v == null)) {
            return distributeEvenly(categoryWeights.keySet().stream().toList());
        }

        Map<UUID, BigDecimal> normalized = new LinkedHashMap<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<UUID, BigDecimal> entry : categoryWeights.entrySet()) {
            BigDecimal weight = entry.getValue().setScale(2, RoundingMode.HALF_UP);
            normalized.put(entry.getKey(), weight);
            sum = sum.add(weight);
        }
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "Total category weight must be > 0");
        }
        if (sum.compareTo(ONE_HUNDRED) == 0) {
            return normalized;
        }

        return normalizeToHundred(normalized, sum);
    }

    private Map<UUID, BigDecimal> distributeEvenly(List<UUID> categoryIds) {
        Map<UUID, BigDecimal> weights = new LinkedHashMap<>();
        if (categoryIds.isEmpty()) {
            return weights;
        }
        BigDecimal divisor = BigDecimal.valueOf(categoryIds.size());
        BigDecimal base = ONE_HUNDRED.divide(divisor, 2, RoundingMode.DOWN);
        BigDecimal consumed = base.multiply(divisor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remainder = ONE_HUNDRED.subtract(consumed).setScale(2, RoundingMode.HALF_UP);
        for (int i = 0; i < categoryIds.size(); i++) {
            BigDecimal value = i == 0 ? base.add(remainder) : base;
            weights.put(categoryIds.get(i), value);
        }
        return weights;
    }

    private Map<UUID, BigDecimal> normalizeToHundred(Map<UUID, BigDecimal> source, BigDecimal total) {
        Map<UUID, BigDecimal> normalized = new LinkedHashMap<>();
        BigDecimal factor = ONE_HUNDRED.divide(total, 8, RoundingMode.HALF_UP);
        BigDecimal running = BigDecimal.ZERO;
        UUID firstKey = null;

        for (Map.Entry<UUID, BigDecimal> entry : source.entrySet()) {
            if (firstKey == null) {
                firstKey = entry.getKey();
            }
            BigDecimal value = entry.getValue()
                    .multiply(factor)
                    .setScale(2, RoundingMode.HALF_UP);
            normalized.put(entry.getKey(), value);
            running = running.add(value);
        }

        BigDecimal diff = ONE_HUNDRED.subtract(running).setScale(2, RoundingMode.HALF_UP);
        if (firstKey != null && diff.compareTo(BigDecimal.ZERO) != 0) {
            normalized.put(firstKey, normalized.get(firstKey).add(diff).setScale(2, RoundingMode.HALF_UP));
        }
        return normalized;
    }

    private BigDecimal readOptionalDecimal(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        if (node.isTextual()) {
            String raw = node.asText().trim();
            if (raw.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(raw);
            } catch (NumberFormatException ex) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "categoryWeightPercentage must be numeric");
            }
        }
        throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                "categoryWeightPercentage has invalid type");
    }
}
