package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.auth.entity.AuthProfile;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.campaign.dto.DistributionChannelResponse;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.DistributionChannel;
import com.bracits.surveyengine.campaign.entity.DistributionChannelType;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.DistributionChannelRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private final DistributionChannelRepository channelRepository;
    private final CampaignRepository campaignRepository;
    private final AuthProfileRepository authProfileRepository;

    @Value("${survey-engine.links.public-base-url:https://survey.example.com}")
    private String publicBaseUrl;

    @Value("${survey-engine.links.public-api-base-url:${survey-engine.links.public-base-url:https://survey.example.com}}")
    private String publicApiBaseUrl;

    @Override
    @Transactional
    public List<DistributionChannelResponse> generateChannels(UUID campaignId) {
        Campaign campaign = campaignRepository.findByIdAndTenantId(campaignId, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        String baseUrl = normalizeBaseUrl(publicBaseUrl);
        String apiBaseUrl = normalizeBaseUrl(publicApiBaseUrl);
        String publicSurveyPath = baseUrl + "/s/" + campaignId;
        String privateExchangePath = apiBaseUrl + "/api/v1/public/campaigns/" + campaignId + "/auth/exchange";
        AuthenticationMode authMode = resolveTenantAuthMode(campaign.getTenantId());

        List<DistributionChannel> channels = new ArrayList<>();
        if (campaign.getAuthMode() == AuthMode.PRIVATE && authMode == AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            channels.add(buildChannel(campaignId, DistributionChannelType.PRIVATE_LINK, privateExchangePath));
        } else {
            channels.add(buildChannel(campaignId, DistributionChannelType.PUBLIC_LINK, publicSurveyPath));
        }

        channelRepository.deleteByCampaignId(campaignId);
        List<DistributionChannel> saved = channelRepository.saveAll(channels);
        return saved.stream().map(dc -> toResponse(dc, campaign.getAuthMode(), authMode)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistributionChannelResponse> getChannels(UUID campaignId) {
        Campaign campaign = campaignRepository.findByIdAndTenantId(campaignId, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));
        AuthenticationMode authMode = resolveTenantAuthMode(campaign.getTenantId());
        return channelRepository.findByCampaignId(campaignId).stream()
                .map(dc -> toResponse(dc, campaign.getAuthMode(), authMode))
                .toList();
    }

    private AuthenticationMode resolveTenantAuthMode(String tenantId) {
        return authProfileRepository.findByTenantId(tenantId)
                .map(AuthProfile::getAuthMode)
                .orElse(AuthenticationMode.PUBLIC_ANONYMOUS);
    }

    private DistributionChannel buildChannel(UUID campaignId, DistributionChannelType type, String value) {
        return DistributionChannel.builder()
                .campaignId(campaignId)
                .channelType(type)
                .channelValue(value)
                .build();
    }

    private DistributionChannelResponse toResponse(
            DistributionChannel dc,
            AuthMode campaignAuthMode,
            AuthenticationMode tenantAuthMode) {
        return DistributionChannelResponse.builder()
                .id(dc.getId())
                .campaignId(dc.getCampaignId())
                .channelType(dc.getChannelType())
                .channelValue(dc.getChannelValue())
                .channelTitle(buildChannelTitle(campaignAuthMode, tenantAuthMode))
                .channelNote(buildChannelNote(campaignAuthMode, tenantAuthMode))
                .requestPayloadExample(buildPayloadExample(dc.getCampaignId(), campaignAuthMode, tenantAuthMode))
                .frontendExample(buildFrontendExample(dc.getCampaignId(), campaignAuthMode, tenantAuthMode))
                .backendExample(buildBackendExample(dc.getCampaignId(), campaignAuthMode, tenantAuthMode))
                .createdAt(dc.getCreatedAt())
                .build();
    }

    private String buildChannelTitle(AuthMode campaignAuthMode, AuthenticationMode tenantAuthMode) {
        if (campaignAuthMode != AuthMode.PRIVATE) {
            return "Public Survey Link";
        }
        if (tenantAuthMode == AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            return "Signed Token Launch Integration";
        }
        if (tenantAuthMode == AuthenticationMode.EXTERNAL_SSO_TRUST) {
            return "Private OIDC Survey Link";
        }
        return "Private Survey Link";
    }

    private String buildChannelNote(AuthMode campaignAuthMode, AuthenticationMode tenantAuthMode) {
        if (campaignAuthMode != AuthMode.PRIVATE) {
            return "Use this public survey URL directly.";
        }
        if (tenantAuthMode == AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            return "Call this endpoint with POST and include a fresh HS256 JWT in responderToken. Required claims: sub, iss, aud, exp, jti. Optional claims: email, name, campaign_id.";
        }
        if (tenantAuthMode == AuthenticationMode.EXTERNAL_SSO_TRUST) {
            return "Use this survey URL directly. Survey Engine will start the private OIDC sign-in flow when the user opens the page.";
        }
        return "Use this survey URL according to your tenant authentication configuration.";
    }

    private String buildPayloadExample(UUID campaignId, AuthMode campaignAuthMode, AuthenticationMode tenantAuthMode) {
        if (campaignAuthMode != AuthMode.PRIVATE || tenantAuthMode != AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            return null;
        }
        return ("{" + System.lineSeparator()
                + "  \"responderToken\": \"<jwt>\"," + System.lineSeparator()
                + "  \"jwtPayloadExample\": {" + System.lineSeparator()
                + "    \"sub\": \"user-001\"," + System.lineSeparator()
                + "    \"email\": \"user@example.com\"," + System.lineSeparator()
                + "    \"name\": \"Alice Johnson\"," + System.lineSeparator()
                + "    \"iss\": \"your-app\"," + System.lineSeparator()
                + "    \"aud\": \"survey-engine\"," + System.lineSeparator()
                + "    \"jti\": \"replace-with-unique-id\"," + System.lineSeparator()
                + "    \"campaign_id\": \"%s\"," + System.lineSeparator()
                + "    \"iat\": 1710000000," + System.lineSeparator()
                + "    \"exp\": 1710003600" + System.lineSeparator()
                + "  }" + System.lineSeparator()
                + "}").formatted(campaignId);
    }

    private String buildFrontendExample(UUID campaignId, AuthMode campaignAuthMode, AuthenticationMode tenantAuthMode) {
        String apiBaseUrl = normalizeBaseUrl(publicApiBaseUrl);
        if (campaignAuthMode != AuthMode.PRIVATE || tenantAuthMode != AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            return null;
        }
        return (("const response = await fetch('%s/api/v1/public/campaigns/%s/auth/exchange', {" + System.lineSeparator()
                + "  method: 'POST'," + System.lineSeparator()
                + "  headers: { 'Content-Type': 'application/json' }," + System.lineSeparator()
                + "  credentials: 'include'," + System.lineSeparator()
                + "  body: JSON.stringify({ responderToken: jwt })" + System.lineSeparator()
                + "});" + System.lineSeparator()
                + "if (response.ok) {" + System.lineSeparator()
                + "  window.location.href = '%s/s/%s';" + System.lineSeparator()
                + "}")).formatted(apiBaseUrl, campaignId, normalizeBaseUrl(publicBaseUrl), campaignId);
    }

    private String buildBackendExample(UUID campaignId, AuthMode campaignAuthMode, AuthenticationMode tenantAuthMode) {
        if (campaignAuthMode != AuthMode.PRIVATE || tenantAuthMode != AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            return null;
        }
        String apiBaseUrl = normalizeBaseUrl(publicApiBaseUrl);
        return (("POST %s/api/v1/public/campaigns/%s/auth/exchange" + System.lineSeparator()
                + "Content-Type: application/json" + System.lineSeparator()
                + System.lineSeparator()
                + "{" + System.lineSeparator()
                + "  \"responderToken\": \"<signed-hs256-jwt>\"" + System.lineSeparator()
                + "}"))
                .formatted(apiBaseUrl, campaignId);
    }

    private String normalizeBaseUrl(String rawBaseUrl) {
        String value = rawBaseUrl == null ? "" : rawBaseUrl.trim();
        if (value.isEmpty()) {
            value = "https://survey.example.com";
        }
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
