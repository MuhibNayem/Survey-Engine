package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.SurveyTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SurveyThemeRepository extends JpaRepository<SurveyTheme, UUID> {
    Optional<SurveyTheme> findByCampaignId(UUID campaignId);
}
