package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.response.dto.ReopenRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;
import com.bracits.surveyengine.response.entity.ReopenAudit;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import com.bracits.surveyengine.response.repository.ReopenAuditRepository;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link ResponseLockingService}.
 * <p>
 * SRS §8: "Responses shall be locked on submit. Admin-only reopen
 * with reason, actor, and time window."
 */
@Service
@RequiredArgsConstructor
public class ResponseLockingServiceImpl implements ResponseLockingService {

    private final SurveyResponseRepository responseRepository;
    private final ReopenAuditRepository reopenAuditRepository;
    private final QuestionVersionRepository questionVersionRepository;

    @Override
    @Transactional
    public SurveyResponseResponse lock(UUID responseId) {
        SurveyResponse response = findOrThrow(responseId);
        if (response.getStatus() == ResponseStatus.LOCKED) {
            throw new BusinessException(ErrorCode.RESPONSE_LOCKED,
                    "Response is already locked");
        }
        response.setStatus(ResponseStatus.LOCKED);
        response.setLockedAt(Instant.now());
        response = responseRepository.save(response);
        return toResponse(response);
    }

    @Override
    @Transactional
    public SurveyResponseResponse reopen(UUID responseId, ReopenRequest request) {
        SurveyResponse response = findOrThrow(responseId);
        if (response.getStatus() != ResponseStatus.LOCKED) {
            throw new BusinessException(ErrorCode.RESPONSE_NOT_LOCKED,
                    "Only locked responses can be reopened");
        }

        // Record audit trail
        ReopenAudit audit = ReopenAudit.builder()
                .surveyResponseId(responseId)
                .reopenedBy("SYSTEM_ADMIN") // Would be from auth context
                .reason(request.getReason())
                .reopenWindowMinutes(request.getReopenWindowMinutes())
                .build();
        reopenAuditRepository.save(audit);

        response.setStatus(ResponseStatus.REOPENED);
        response.setLockedAt(null);
        response = responseRepository.save(response);
        return toResponse(response);
    }

    @Override
    @Transactional
    public int lockOpenResponsesForCampaignClosure(UUID campaignId, Instant lockedAt) {
        Instant effectiveLockedAt = lockedAt != null ? lockedAt : Instant.now();
        List<SurveyResponse> responses = responseRepository.findByCampaignIdAndStatusIn(
                campaignId,
                Set.of(ResponseStatus.IN_PROGRESS, ResponseStatus.REOPENED));
        if (responses.isEmpty()) {
            return 0;
        }

        for (SurveyResponse response : responses) {
            response.setStatus(ResponseStatus.LOCKED);
            response.setLockedAt(effectiveLockedAt);
        }
        responseRepository.saveAll(responses);
        return responses.size();
    }

    private SurveyResponse findOrThrow(UUID id) {
        return responseRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("SurveyResponse", id));
    }

    private SurveyResponseResponse toResponse(SurveyResponse r) {
        Map<UUID, QuestionVersion> questionVersions = new HashMap<>();
        List<SurveyResponseResponse.AnswerResponse> answers = r.getAnswers().stream()
                .map(a -> {
                    QuestionVersion questionVersion = null;
                    if (a.getQuestionVersionId() != null) {
                        questionVersion = questionVersions.computeIfAbsent(
                                a.getQuestionVersionId(),
                                id -> questionVersionRepository.findById(id).orElse(null));
                    }
                    return SurveyResponseResponse.AnswerResponse.builder()
                            .id(a.getId())
                            .questionId(a.getQuestionId())
                            .questionVersionId(a.getQuestionVersionId())
                            .questionVersionNumber(questionVersion != null ? questionVersion.getVersionNumber() : null)
                            .questionText(questionVersion != null ? questionVersion.getText() : null)
                            .questionType(questionVersion != null ? questionVersion.getType() : null)
                            .optionConfig(questionVersion != null ? questionVersion.getOptionConfig() : null)
                            .value(a.getValue())
                            .remark(a.getRemark())
                            .score(a.getScore())
                            .build();
                })
                .toList();

        return SurveyResponseResponse.builder()
                .id(r.getId())
                .campaignId(r.getCampaignId())
                .surveySnapshotId(r.getSurveySnapshotId())
                .respondentIdentifier(r.getRespondentIdentifier())
                .respondentMetadata(r.getRespondentMetadata())
                .status(r.getStatus())
                .startedAt(r.getStartedAt())
                .submittedAt(r.getSubmittedAt())
                .lockedAt(r.getLockedAt())
                .answers(answers)
                .build();
    }
}
