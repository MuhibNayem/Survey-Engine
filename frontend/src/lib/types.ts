// ============================================================
// Survey Engine Frontend — Shared Types
// ============================================================

// --- Auth ---
export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    fullName: string;
    email: string;
    password: string;
    tenantId: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    email: string;
    tenantId: string;
    role: string;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

/** Safe response DTO — no tokens exposed (cookie-based auth) */
export interface AuthUserResponse {
    userId?: string;
    email: string;
    fullName?: string;
    tenantId: string;
    role: string;
}

// --- Questions ---
export type QuestionType = 'RANK' | 'RATING_SCALE' | 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE';

export interface QuestionRequest {
    text: string;
    type: QuestionType;
    maxScore: number;
    optionConfig?: string;
}

export interface QuestionResponse {
    id: string;
    text: string;
    type: QuestionType;
    maxScore: number;
    optionConfig?: string;
    currentVersion: number;
    active: boolean;
    createdBy: string;
    createdAt: string;
    updatedBy: string;
    updatedAt: string;
}

// --- Categories ---
export interface CategoryQuestionMappingRequest {
    questionId: string;
    sortOrder: number;
    weight: number;
}

export interface CategoryRequest {
    name: string;
    description: string;
    questionMappings: CategoryQuestionMappingRequest[];
}

export interface CategoryResponse {
    id: string;
    name: string;
    description: string;
    currentVersion: number;
    active: boolean;
    questionMappings: {
        questionId: string;
        questionVersionId: string;
        sortOrder: number;
        weight: number;
    }[];
    createdBy: string;
    createdAt: string;
    updatedBy: string;
    updatedAt: string;
}

// --- Surveys ---
export type SurveyLifecycleState = 'DRAFT' | 'PUBLISHED' | 'CLOSED' | 'RESULTS_PUBLISHED' | 'ARCHIVED';

export interface SurveyQuestionRequest {
    questionId: string;
    categoryId?: string;
    sortOrder: number;
    mandatory: boolean;
    answerConfig?: string;
}

export interface SurveyPageRequest {
    title: string;
    sortOrder: number;
    questions: SurveyQuestionRequest[];
}

export interface SurveyRequest {
    title: string;
    description: string;
    pages: SurveyPageRequest[];
}

export interface SurveyResponse {
    id: string;
    title: string;
    description: string;
    lifecycleState: SurveyLifecycleState;
    currentVersion: number;
    active: boolean;
    pages: {
        id: string;
        title: string;
        sortOrder: number;
        questions: {
            id: string;
            questionId: string;
            questionVersionId: string;
            categoryId?: string;
            categoryVersionId?: string;
            sortOrder: number;
            mandatory: boolean;
            answerConfig?: string;
        }[];
    }[];
    createdBy: string;
    createdAt: string;
    updatedBy: string;
    updatedAt: string;
}

export interface LifecycleTransitionRequest {
    targetState: SurveyLifecycleState;
    reason?: string;
}

// --- Campaigns ---
export type CampaignStatus = 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'ARCHIVED';
export type AuthMode = 'PUBLIC' | 'PRIVATE';

export interface CampaignRequest {
    name: string;
    description: string;
    surveyId: string;
    authMode: AuthMode;
}

export interface CampaignResponse {
    id: string;
    name: string;
    description: string;
    surveyId: string;
    surveySnapshotId: string;
    authMode: AuthMode;
    status: CampaignStatus;
    active: boolean;
    createdBy: string;
    createdAt: string;
    updatedBy: string;
    updatedAt: string;
}

export interface CampaignSettingsRequest {
    password?: string;
    captchaEnabled: boolean;
    oneResponsePerDevice: boolean;
    ipRestrictionEnabled: boolean;
    emailRestrictionEnabled: boolean;
    responseQuota?: number;
    closeDate?: string;
    sessionTimeoutMinutes: number;
    showQuestionNumbers: boolean;
    showProgressIndicator: boolean;
    allowBackButton: boolean;
    startMessage?: string;
    finishMessage?: string;
    headerHtml?: string;
    footerHtml?: string;
    collectName: boolean;
    collectEmail: boolean;
    collectPhone: boolean;
    collectAddress: boolean;
}

export interface CampaignSettingsResponse {
    campaignId: string;
    password?: string;
    captchaEnabled: boolean;
    oneResponsePerDevice: boolean;
    ipRestrictionEnabled: boolean;
    emailRestrictionEnabled: boolean;
    responseQuota?: number;
    closeDate?: string;
    sessionTimeoutMinutes: number;
    showQuestionNumbers: boolean;
    showProgressIndicator: boolean;
    allowBackButton: boolean;
    startMessage?: string;
    finishMessage?: string;
    headerHtml?: string;
    footerHtml?: string;
    collectName: boolean;
    collectEmail: boolean;
    collectPhone: boolean;
    collectAddress: boolean;
}

export interface CampaignPreviewResponse {
    campaignId: string;
    tenantId: string;
    campaignName: string;
    campaignStatus: CampaignStatus;
    authMode: AuthMode;
    surveyId: string;
    surveyTitle: string;
    surveyDescription: string;
    showQuestionNumbers: boolean;
    showProgressIndicator: boolean;
    allowBackButton: boolean;
    startMessage?: string;
    finishMessage?: string;
    headerHtml?: string;
    footerHtml?: string;
    collectName: boolean;
    collectEmail: boolean;
    collectPhone: boolean;
    collectAddress: boolean;
    pages: {
        id: string;
        title: string;
        sortOrder: number;
        questions: {
            id: string;
            questionId: string;
            questionVersionId: string;
            categoryVersionId?: string;
            text: string;
            type: QuestionType;
            maxScore: number;
            mandatory: boolean;
            sortOrder: number;
            optionConfig?: string;
            answerConfig?: string;
        }[];
    }[];
}

export type DistributionChannelType =
    | 'PUBLIC_LINK'
    | 'PRIVATE_LINK'
    | 'HTML_EMBED'
    | 'WORDPRESS_EMBED'
    | 'JS_EMBED'
    | 'EMAIL';

export interface DistributionChannelResponse {
    id: string;
    campaignId: string;
    channelType: DistributionChannelType;
    channelValue: string;
    createdAt: string;
}

// --- Responses ---
export type ResponseStatus = 'IN_PROGRESS' | 'SUBMITTED' | 'LOCKED' | 'REOPENED';

export interface SurveyResponseResponse {
    id: string;
    campaignId: string;
    surveySnapshotId: string;
    respondentIdentifier: string;
    status: ResponseStatus;
    startedAt: string;
    submittedAt: string;
    lockedAt: string;
    answers: {
        id: string;
        questionId: string;
        questionVersionId: string;
        value: string;
        score: number;
    }[];
}

export interface AnalyticsResponse {
    campaignId: string;
    totalResponses: number;
    submittedCount: number;
    lockedCount: number;
    inProgressCount: number;
    completionRate: number;
}

// --- Scoring ---
export interface CategoryWeightRequest {
    categoryId: string;
    weightPercentage: number;
}

export interface WeightProfileRequest {
    name: string;
    campaignId: string;
    categoryWeights: CategoryWeightRequest[];
}

export interface WeightProfileResponse {
    id: string;
    name: string;
    campaignId: string;
    active: boolean;
    categoryWeights: {
        categoryId: string;
        weightPercentage: number;
    }[];
}

export interface ScoreResult {
    campaignId: string;
    weightProfileId: string;
    totalScore: number;
    categoryScores: {
        categoryId: string;
        rawScore: number;
        maxScore: number;
        normalizedScore: number;
        weightPercentage: number;
        weightedScore: number;
    }[];
}

// --- Subscriptions ---
export type SubscriptionPlan = 'BASIC' | 'PRO' | 'ENTERPRISE';
export type SubscriptionStatus = 'TRIAL' | 'ACTIVE' | 'CANCELED' | 'EXPIRED';

export interface SubscriptionResponse {
    id: string;
    tenantId: string;
    plan: SubscriptionPlan;
    status: SubscriptionStatus;
    currentPeriodStart: string;
    currentPeriodEnd: string;
    lastPaymentGatewayReference: string;
    planPrice: number;
    currency: string;
    maxCampaigns: number;
    maxResponsesPerCampaign: number;
    maxAdminUsers: number;
}

export interface PlanDefinitionResponse {
    id: string;
    planCode: SubscriptionPlan;
    displayName: string;
    price: number;
    currency: string;
    billingCycleDays: number;
    trialDays: number;
    maxCampaigns: number;
    maxResponsesPerCampaign: number;
    maxAdminUsers: number;
    active: boolean;
}

// --- Tenant Auth Config ---
export type AuthenticationMode = 'PUBLIC_ANONYMOUS' | 'SIGNED_LAUNCH_TOKEN' | 'EXTERNAL_SSO_TRUST';
export type FallbackPolicy = 'SSO_REQUIRED' | 'ANONYMOUS_FALLBACK' | 'DISABLE_ON_FAILURE';

export interface ClaimMappingResponse {
    id?: string;
    externalClaim: string;
    internalField: string;
    required: boolean;
}

export interface AuthProfileResponse {
    id: string;
    tenantId: string;
    authMode: AuthenticationMode;
    issuer: string;
    audience: string;
    jwksEndpoint: string;
    oidcDiscoveryUrl: string;
    oidcClientId: string;
    oidcRedirectUri: string;
    oidcScopes: string;
    clockSkewSeconds: number;
    tokenTtlSeconds: number;
    activeKeyVersion: number;
    fallbackPolicy: FallbackPolicy;
    claimMappings: ClaimMappingResponse[];
}

export interface ClaimMappingRequest {
    externalClaim: string;
    internalField: string;
    required: boolean;
}

export interface AuthProfileRequest {
    tenantId: string;
    authMode: AuthenticationMode;
    issuer?: string;
    audience?: string;
    jwksEndpoint?: string;
    oidcDiscoveryUrl?: string;
    oidcClientId?: string;
    oidcClientSecret?: string;
    oidcRedirectUri?: string;
    oidcScopes?: string;
    clockSkewSeconds?: number;
    tokenTtlSeconds?: number;
    signingSecret?: string;
    fallbackPolicy: FallbackPolicy;
    claimMappings: ClaimMappingRequest[];
}

export interface ProviderTemplateResponse {
    providerCode: string;
    displayName: string;
    description: string;
    defaultScopes: string[];
    defaultClaimMappings: ClaimMappingRequest[];
    requiredConfigFields: string[];
}
