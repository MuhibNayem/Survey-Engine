package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
    List<SurveyResponse> findByCampaignId(UUID campaignId);

    List<SurveyResponse> findByCampaignIdAndStatus(UUID campaignId, ResponseStatus status);

    long countByCampaignId(UUID campaignId);

    long countByCampaignIdAndStatus(UUID campaignId, ResponseStatus status);

    boolean existsByCampaignIdAndRespondentIp(UUID campaignId, String respondentIp);

    boolean existsByCampaignIdAndRespondentDeviceFingerprint(UUID campaignId, String fingerprint);

    boolean existsByCampaignIdAndRespondentIdentifier(UUID campaignId, String identifier);
}
