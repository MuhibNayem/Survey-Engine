package com.bracits.surveyengine.survey.service;

import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for survey CRUD, lifecycle management, and snapshot
 * creation.
 * SRS §4.1, §4.6
 */
public interface SurveyService {

    SurveyResponse create(SurveyRequest request);

    SurveyResponse getById(UUID id);

    List<SurveyResponse> getAllActive();

    SurveyResponse update(UUID id, SurveyRequest request);

    void deactivate(UUID id);

    /**
     * Transitions survey lifecycle state.
     * SRS §4.6: Draft → Published → Closed → Results_Published → Archived
     * Special case: Closed → Published (reopen) requires audited reason.
     */
    SurveyResponse transitionLifecycle(UUID id, LifecycleTransitionRequest request);

    /**
     * Returns the latest published snapshot for a survey.
     */
    SurveySnapshot getLatestSnapshot(UUID surveyId);
}
