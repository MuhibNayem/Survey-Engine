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
    confirmPassword: string;
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

export interface PageResponse<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: { empty: boolean; sorted: boolean; unsorted: boolean };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    last: boolean;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    sort: { empty: boolean; sorted: boolean; unsorted: boolean };
    first: boolean;
    numberOfElements: number;
    empty: boolean;
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
    categoryWeightPercentage?: number;
    sortOrder: number;
    mandatory: boolean;
    answerConfig?: string;
    pinnedQuestionText?: string;
    pinnedQuestionMaxScore?: number;
    pinnedQuestionOptionConfig?: string;
    pinnedCategoryName?: string;
    pinnedCategoryDescription?: string;
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
            categoryWeightPercentage?: number;
            sortOrder: number;
            mandatory: boolean;
            answerConfig?: string;
            pinnedQuestionText?: string;
            pinnedQuestionType?: QuestionType;
            pinnedQuestionMaxScore?: number;
            pinnedQuestionOptionConfig?: string;
            pinnedCategoryName?: string;
            pinnedCategoryDescription?: string;
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
    defaultWeightProfileId?: string;
    authMode: AuthMode;
    status: CampaignStatus;
    active: boolean;
    createdBy: string;
    createdAt: string;
    updatedBy: string;
    updatedAt: string;
    dataCollectionFields?: DataCollectionFieldResponse[];
}

export type DataCollectionFieldType = 'TEXT' | 'EMAIL' | 'PHONE' | 'NUMBER' | 'TEXTAREA';

export interface DataCollectionFieldRequest {
    fieldKey: string;
    label: string;
    fieldType: DataCollectionFieldType;
    required: boolean;
    sortOrder: number;
    enabled: boolean;
}

export interface DataCollectionFieldResponse {
    id: string;
    fieldKey: string;
    label: string;
    fieldType: DataCollectionFieldType;
    required: boolean;
    sortOrder: number;
    enabled: boolean;
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
    dataCollectionFields?: DataCollectionFieldRequest[];
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
    dataCollectionFields?: DataCollectionFieldResponse[];
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
    dataCollectionFields?: DataCollectionFieldResponse[];
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
    respondentMetadata?: string;
    status: ResponseStatus;
    startedAt: string;
    submittedAt: string;
    lockedAt: string;
    weightProfileId?: string;
    weightedTotalScore?: number;
    scoredAt?: string;
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
    weightProfilesEnabled: boolean;
    signedTokenEnabled: boolean;
    ssoEnabled: boolean;
    customBrandingEnabled: boolean;
    deviceFingerprintEnabled: boolean;
    apiAccessEnabled: boolean;
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

// --- Super Admin Tenant Management ---
export interface TenantOverviewResponse {
    tenantId: string;
    name: string;
    primaryEmail: string;
    plan: SubscriptionPlan | null;
    subscriptionStatus: SubscriptionStatus | null;
    active: boolean;
    createdAt: string;
}

export interface SuperAdminMetricsResponse {
    totalTenants: number;
    activeSubscriptions: number;
    totalPlatformUsage: number;
}

export interface OverrideSubscriptionRequest {
    plan: SubscriptionPlan;
}

// --- Audit Logs ---
export interface AuditLogEntry {
    id: string;
    tenantId: string | null;
    timestamp: string;
    entityType: string;
    entityId: string;
    action: string;
    actor: string;
    reason: string | null;
    ipAddress: string | null;
    beforeValue: string | null;
    afterValue: string | null;
}

export interface PaginatedResponse<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            empty: boolean;
            sorted: boolean;
            unsorted: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    last: boolean;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    sort: {
        empty: boolean;
        sorted: boolean;
        unsorted: boolean;
    };
    first: boolean;
    numberOfElements: number;
    empty: boolean;
}
