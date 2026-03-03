package com.bracits.surveyengine.auth;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthConfigAudit;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.service.AuthProfileService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import com.bracits.surveyengine.campaign.dto.CampaignRequest;
import com.bracits.surveyengine.campaign.dto.CampaignResponse;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuthIntegrationTest {

    @Autowired
    private AuthProfileService authProfileService;
    @Autowired
    private TokenValidationService tokenValidationService;
    @Autowired
    private AuthConfigAuditRepository auditRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private QuestionService questionService;

    private UUID campaignId;

    @BeforeEach
    void setUp() {
        QuestionResponse q = questionService.create(QuestionRequest.builder()
                .text("Auth Q").type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00")).build());
        SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                .title("Auth Survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("P1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(q.getId()).sortOrder(1).build()))
                        .build()))
                .build());
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Auth Campaign").surveyId(survey.getId())
                .authMode(AuthMode.PUBLIC).build());
        campaignId = campaign.getId();
    }

    @Test
    void shouldCreatePublicAnonymousProfile() {
        AuthProfileResponse profile = authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.PUBLIC_ANONYMOUS)
                .build());

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getAuthMode()).isEqualTo(AuthenticationMode.PUBLIC_ANONYMOUS);
    }

    @Test
    void shouldValidatePublicAnonymousToken() {
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.PUBLIC_ANONYMOUS)
                .build());

        TokenValidationResult result = tokenValidationService.validateToken(campaignId, null);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getRespondentId()).startsWith("anonymous-");
    }

    @Test
    void shouldValidateSignedLaunchToken() {
        String secret = "test-secret-key-256-bits-long!!!";
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .issuer("test-issuer")
                .audience("test-audience")
                .signingSecret(secret)
                .clockSkewSeconds(60)
                .claimMappings(List.of(
                        AuthProfileRequest.ClaimMappingRequest.builder()
                                .externalClaim("email")
                                .internalField("email")
                                .required(true)
                                .build()))
                .build());

        // Build valid token
        String payload = "{\"sub\":\"user-123\",\"exp\":%d,\"iss\":\"test-issuer\",\"aud\":\"test-audience\",\"email\":\"user@example.com\"}"
                .formatted(Instant.now().getEpochSecond() + 3600);
        String payloadBase64 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha256(payloadBase64, secret);
        String token = payloadBase64 + "." + signature;

        TokenValidationResult result = tokenValidationService.validateToken(campaignId, token);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getRespondentId()).isEqualTo("user-123");
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void shouldRejectExpiredToken() {
        String secret = "test-secret-for-expired-token!!!";
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret(secret)
                .clockSkewSeconds(0)
                .build());

        // Expired 1 hour ago
        String payload = "{\"sub\":\"user-123\",\"exp\":%d}"
                .formatted(Instant.now().getEpochSecond() - 3600);
        String payloadBase64 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha256(payloadBase64, secret);
        String token = payloadBase64 + "." + signature;

        TokenValidationResult result = tokenValidationService.validateToken(campaignId, token);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
    }

    @Test
    void shouldRejectInvalidSignature() {
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret("correct-secret-key-256-bits!!!!!")
                .build());

        String payload = "{\"sub\":\"user-123\",\"exp\":%d}"
                .formatted(Instant.now().getEpochSecond() + 3600);
        String payloadBase64 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String wrongSignature = hmacSha256(payloadBase64, "wrong-secret-key-256-bits!!!!!!!!");
        String token = payloadBase64 + "." + wrongSignature;

        TokenValidationResult result = tokenValidationService.validateToken(campaignId, token);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
    }

    @Test
    void shouldRotateKeyAndAuditLog() {
        AuthProfileResponse profile = authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret("rotate-me-key")
                .build());

        assertThat(profile.getActiveKeyVersion()).isEqualTo(1);

        AuthProfileResponse rotated = authProfileService.rotateKey(profile.getId());
        assertThat(rotated.getActiveKeyVersion()).isEqualTo(2);

        // Verify audit trail
        List<AuthConfigAudit> audits = auditRepository
                .findByAuthProfileIdOrderByChangedAtDesc(profile.getId());
        assertThat(audits).hasSizeGreaterThanOrEqualTo(2); // CREATE + KEY_ROTATION
        assertThat(audits.get(0).getAction()).isEqualTo("KEY_ROTATION");
    }

    @Test
    void shouldFallbackToAnonymousOnError() {
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret("some-secret-key-that-is-long!!!")
                .fallbackPolicy(FallbackPolicy.ANONYMOUS_FALLBACK)
                .build());

        // Send malformed token — should fallback to anonymous
        TokenValidationResult result = tokenValidationService.validateToken(campaignId, "bad-token");
        assertThat(result.isValid()).isTrue();
        assertThat(result.getRespondentId()).startsWith("anonymous-");
    }

    @Test
    void shouldRejectWithSsoRequiredOnError() {
        authProfileService.create(AuthProfileRequest.builder()
                .campaignId(campaignId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret("some-secret-key-that-is-long!!!")
                .fallbackPolicy(FallbackPolicy.SSO_REQUIRED)
                .build());

        TokenValidationResult result = tokenValidationService.validateToken(campaignId, "bad-token");
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
