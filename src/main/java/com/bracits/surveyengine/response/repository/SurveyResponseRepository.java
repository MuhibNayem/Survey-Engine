package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID>, JpaSpecificationExecutor<SurveyResponse> {
    List<SurveyResponse> findByCampaignId(UUID campaignId);
    Page<SurveyResponse> findByCampaignIdAndTenantId(UUID campaignId, String tenantId, Pageable pageable);

    List<SurveyResponse> findByCampaignIdAndStatus(UUID campaignId, ResponseStatus status);
    List<SurveyResponse> findByCampaignIdAndStatusAndTenantId(UUID campaignId, ResponseStatus status, String tenantId);
    List<SurveyResponse> findByCampaignIdAndStatusIn(UUID campaignId, Collection<ResponseStatus> statuses);

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
    Optional<SurveyResponse> findByIdAndCampaignId(UUID id, UUID campaignId);
    Optional<SurveyResponse> findFirstByCampaignIdAndTenantIdAndRespondentIdentifierAndStatusInOrderByStartedAtDesc(
            UUID campaignId, String tenantId, String respondentIdentifier, Collection<ResponseStatus> statuses);

    boolean existsByCampaignIdAndRespondentIpAndTenantIdAndIdNot(UUID campaignId, String respondentIp, String tenantId, UUID id);
    boolean existsByCampaignIdAndRespondentDeviceFingerprintAndTenantIdAndIdNot(UUID campaignId, String fingerprint, String tenantId, UUID id);
    boolean existsByCampaignIdAndRespondentIdentifierAndTenantIdAndIdNot(UUID campaignId, String identifier, String tenantId, UUID id);
}
