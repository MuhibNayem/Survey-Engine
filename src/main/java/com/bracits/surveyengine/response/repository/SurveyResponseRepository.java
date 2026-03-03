package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
    List<SurveyResponse> findByCampaignId(UUID campaignId);
    List<SurveyResponse> findByCampaignIdAndTenantId(UUID campaignId, String tenantId);

    List<SurveyResponse> findByCampaignIdAndStatus(UUID campaignId, ResponseStatus status);
    List<SurveyResponse> findByCampaignIdAndStatusAndTenantId(UUID campaignId, ResponseStatus status, String tenantId);

    long countByCampaignId(UUID campaignId);
    long countByCampaignIdAndTenantId(UUID campaignId, String tenantId);

    long countByCampaignIdAndStatus(UUID campaignId, ResponseStatus status);
    long countByCampaignIdAndStatusAndTenantId(UUID campaignId, ResponseStatus status, String tenantId);

    boolean existsByCampaignIdAndRespondentIp(UUID campaignId, String respondentIp);
    boolean existsByCampaignIdAndRespondentIpAndTenantId(UUID campaignId, String respondentIp, String tenantId);

    boolean existsByCampaignIdAndRespondentDeviceFingerprint(UUID campaignId, String fingerprint);
    boolean existsByCampaignIdAndRespondentDeviceFingerprintAndTenantId(UUID campaignId, String fingerprint, String tenantId);

    boolean existsByCampaignIdAndRespondentIdentifier(UUID campaignId, String identifier);
    boolean existsByCampaignIdAndRespondentIdentifierAndTenantId(UUID campaignId, String identifier, String tenantId);

    Optional<SurveyResponse> findByIdAndTenantId(UUID id, String tenantId);
}
