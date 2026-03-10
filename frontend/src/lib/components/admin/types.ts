// ============================================================
// Feature Management Metadata Editor Types
// ============================================================

/**
 * Base interface for all feature metadata types
 */
export interface BaseFeatureMetadata {
	/** Optional schema version for migration support */
	$schema?: string;
	/** Last modified timestamp */
	lastModified?: string;
	/** Last modified by user ID */
	lastModifiedBy?: string;
}

// ============================================================
// Tour Step Types
// ============================================================

/**
 * Placement options for tour steps
 */
export type TourStepPlacement = 'top' | 'bottom' | 'left' | 'right' | 'auto';

/**
 * A single step in a product tour
 */
export interface TourStep {
	/** Unique identifier for the step (auto-generated if not provided) */
	id: string;
	/** Title displayed in the tour step */
	title: string;
	/** Description/content of the tour step */
	description: string;
	/** CSS selector for the target element */
	targetSelector: string;
	/** Placement of the tooltip relative to target */
	placement: TourStepPlacement;
	/** Whether clicking outside closes the step */
	dismissible?: boolean;
	/** Whether to show a skip button */
	showSkip?: boolean;
	/** Custom CSS class for styling */
	customClass?: string;
	/** Optional callback/action on step show */
	onShow?: string;
	/** Whether this step is required (validation) */
	required?: boolean;
}

/**
 * Metadata for TOUR feature type
 */
export interface TourMetadata extends BaseFeatureMetadata {
	featureType: 'TOUR';
	/** Ordered list of tour steps */
	steps: TourStep[];
	/** Tour configuration */
	config: {
		/** Auto-start the tour on page load */
		autoStart: boolean;
		/** Show progress indicator (e.g., "Step 1 of 5") */
		showProgress: boolean;
		/** Allow keyboard navigation */
		keyboardNavigation: boolean;
		/** Scroll to element before showing step */
		scrollToElement: boolean;
		/** Overlay color with opacity */
		overlayColor: string;
		/** Whether clicking overlay closes tour */
		clickOverlayCloses: boolean;
		/** Maximum tour duration in seconds (0 = no limit) */
		maxDuration: number;
	};
}

// ============================================================
// Tooltip Types
// ============================================================

/**
 * Trigger types for tooltips
 */
export type TooltipTrigger = 'hover' | 'click' | 'focus' | 'manual';

/**
 * Metadata for TOOLTIP feature type
 */
export interface TooltipMetadata extends BaseFeatureMetadata {
	featureType: 'TOOLTIP';
	/** CSS selector for the target element */
	targetSelector: string;
	/** Tooltip content (supports markdown/HTML) */
	content: string;
	/** Optional title for the tooltip */
	title?: string;
	/** How the tooltip is triggered */
	trigger: TooltipTrigger;
	/** Placement relative to target */
	placement: TourStepPlacement;
	/** Delay before showing (ms) */
	showDelay: number;
	/** Delay before hiding (ms) */
	hideDelay: number;
	/** Whether tooltip can be dismissed */
	dismissible: boolean;
	/** Maximum width in pixels */
	maxWidth?: number;
	/** Custom CSS class */
	customClass?: string;
}

// ============================================================
// Banner Types
// ============================================================

/**
 * Banner priority levels
 */
export type BannerPriority = 'low' | 'medium' | 'high' | 'urgent';

/**
 * Banner style variants
 */
export type BannerVariant = 'default' | 'info' | 'success' | 'warning' | 'error';

/**
 * Metadata for BANNER feature type
 */
export interface BannerMetadata extends BaseFeatureMetadata {
	featureType: 'BANNER';
	/** Banner title */
	title: string;
	/** Banner content (supports HTML) */
	content: string;
	/** Call-to-action button text */
	ctaText?: string;
	/** Call-to-action button link/URL */
	ctaUrl?: string;
	/** Whether CTA opens in new tab */
	ctaNewTab?: boolean;
	/** Secondary action text */
	secondaryActionText?: string;
	/** Secondary action callback/handler */
	secondaryAction?: string;
	/** Banner priority */
	priority: BannerPriority;
	/** Visual variant */
	variant: BannerVariant;
	/** Start date (ISO string) */
	startDate?: string;
	/** End date (ISO string) */
	endDate?: string;
	/** Whether banner can be dismissed */
	dismissible: boolean;
	/** Dismiss key for localStorage */
	dismissKey?: string;
	/** Target pages/routes (empty = all) */
	targetPages?: string[];
	/** Target user roles (empty = all) */
	targetRoles?: string[];
	/** Display frequency */
	displayFrequency: 'once' | 'daily' | 'always';
}

// ============================================================
// Feature Flag Types
// ============================================================

/**
 * Rule operator types
 */
export type RuleOperator =
	| 'equals'
	| 'not_equals'
	| 'contains'
	| 'not_contains'
	| 'greater_than'
	| 'less_than'
	| 'greater_than_or_equal'
	| 'less_than_or_equal'
	| 'in'
	| 'not_in'
	| 'regex'
	| 'is_null'
	| 'is_not_null';

/**
 * Logical operators for combining rules
 */
export type LogicalOperator = 'AND' | 'OR';

/**
 * A single rule in a feature flag
 */
export interface FeatureFlagRule {
	/** Unique rule ID */
	id: string;
	/** User/property attribute to check */
	attribute: string;
	/** Operator for comparison */
	operator: RuleOperator;
	/** Value to compare against */
	value: string | number | boolean | string[];
}

/**
 * A group of rules with logical operator
 */
export interface FeatureFlagRuleGroup {
	/** Unique group ID */
	id: string;
	/** Logical operator for combining rules */
	logicalOperator: LogicalOperator;
	/** Rules in this group */
	rules: FeatureFlagRule[];
	/** Nested groups (for complex conditions) */
	groups?: FeatureFlagRuleGroup[];
}

/**
 * Rollout strategy types
 */
export type RolloutStrategy = 'percentage' | 'gradual' | 'scheduled' | 'canary';

/**
 * Gradual rollout configuration
 */
export interface GradualRolloutConfig {
	/** Starting percentage */
	startPercentage: number;
	/** Target percentage */
	targetPercentage: number;
	/** Increment step */
	incrementStep: number;
	/** Interval between increments (hours) */
	intervalHours: number;
}

/**
 * Scheduled rollout configuration
 */
export interface ScheduledRolloutConfig {
	/** Schedule entries with date and percentage */
	schedule: { date: string; percentage: number }[];
}

/**
 * Canary rollout configuration
 */
export interface CanaryRolloutConfig {
	/** Initial canary percentage */
	canaryPercentage: number;
	/** Success criteria (error rate threshold) */
	errorRateThreshold: number;
	/** Evaluation period (hours) */
	evaluationPeriodHours: number;
	/** Stages */
	stages: { percentage: number; durationHours: number }[];
}

/**
 * Metadata for FEATURE_FLAG feature type
 */
export interface FeatureFlagMetadata extends BaseFeatureMetadata {
	featureType: 'FEATURE_FLAG';
	/** Default value when no rules match */
	defaultValue: boolean;
	/** Rule groups (OR between groups, AND/OR within) */
	ruleGroups: FeatureFlagRuleGroup[];
	/** Rollout percentage (0-100) */
	rolloutPercentage: number;
	/** Rollout strategy */
	rolloutStrategy: RolloutStrategy;
	/** Gradual rollout config */
	gradualRollout?: GradualRolloutConfig;
	/** Scheduled rollout config */
	scheduledRollout?: ScheduledRolloutConfig;
	/** Canary rollout config */
	canaryRollout?: CanaryRolloutConfig;
	/** User segments that always get the feature */
	forceEnabledSegments?: string[];
	/** User segments that never get the feature */
	forceDisabledSegments?: string[];
	/** Kill switch - disable for everyone */
	killSwitch: boolean;
	/** Environment-specific overrides */
	environmentOverrides?: Record<string, boolean>;
}

// ============================================================
// Announcement Types
// ============================================================

/**
 * Announcement type categories
 */
export type AnnouncementType = 'info' | 'feature' | 'update' | 'maintenance' | 'promotion';

/**
 * Announcement targeting options
 */
export interface AnnouncementTargeting {
	/** Target user roles */
	roles?: string[];
	/** Target plans */
	plans?: string[];
	/** Target tenant IDs */
	tenantIds?: string[];
	/** Target user IDs */
	userIds?: string[];
	/** Target pages/routes */
	pages?: string[];
}

/**
 * Metadata for ANNOUNCEMENT feature type
 */
export interface AnnouncementMetadata extends BaseFeatureMetadata {
	featureType: 'ANNOUNCEMENT';
	/** Announcement title */
	title: string;
	/** Announcement content (supports HTML/markdown) */
	content: string;
	/** Announcement type */
	announcementType: AnnouncementType;
	/** Optional icon name */
	icon?: string;
	/** Optional image URL */
	imageUrl?: string;
	/** CTA button text */
	ctaText?: string;
	/** CTA button URL */
	ctaUrl?: string;
	/** Secondary action text */
	secondaryActionText?: string;
	/** Start date/time (ISO string) */
	scheduledStart?: string;
	/** End date/time (ISO string) */
	scheduledEnd?: string;
	/** Whether announcement can be dismissed */
	dismissible: boolean;
	/** Dismiss persistence (session/local/never) */
	dismissPersistence: 'session' | 'local' | 'never';
	/** Display style */
	displayStyle: 'modal' | 'banner' | 'toast' | 'inline';
	/** Targeting configuration */
	targeting: AnnouncementTargeting;
	/** Priority for ordering */
	priority: number;
	/** Whether to show on first login only */
	firstLoginOnly: boolean;
	/** Required acknowledgment */
	requireAcknowledgment: boolean;
}

// ============================================================
// Union Type for All Metadata Types
// ============================================================

/**
 * Union type for all feature metadata types
 */
export type FeatureMetadata =
	| TourMetadata
	| TooltipMetadata
	| BannerMetadata
	| FeatureFlagMetadata
	| AnnouncementMetadata;

/**
 * Feature type literal union
 */
export type FeatureType = 'TOUR' | 'TOOLTIP' | 'BANNER' | 'FEATURE_FLAG' | 'ANNOUNCEMENT';

/**
 * Mapping from feature type to metadata type
 */
export type MetadataForType<T extends FeatureType> = T extends 'TOUR'
	? TourMetadata
	: T extends 'TOOLTIP'
		? TooltipMetadata
		: T extends 'BANNER'
			? BannerMetadata
			: T extends 'FEATURE_FLAG'
				? FeatureFlagMetadata
				: T extends 'ANNOUNCEMENT'
					? AnnouncementMetadata
					: never;

// ============================================================
// Editor State Types
// ============================================================

/**
 * Validation error structure
 */
export interface ValidationError {
	/** Field path (dot notation for nested) */
	field: string;
	/** Error message */
	message: string;
	/** Error severity */
	severity: 'error' | 'warning' | 'info';
}

/**
 * Editor state for tracking changes
 */
export interface EditorState<T> {
	/** Original data (for dirty checking) */
	original: T | null;
	/** Current working data */
	current: T;
	/** Whether there are unsaved changes */
	isDirty: boolean;
	/** Whether data is being saved */
	isSaving: boolean;
	/** Validation errors */
	errors: ValidationError[];
	/** Whether editor is in read-only mode */
	readOnly: boolean;
	/** History for undo/redo */
	history: T[];
	/** History index */
	historyIndex: number;
}

/**
 * Callback types for editor components
 */
export interface EditorCallbacks<T> {
	/** Called when data changes */
	onChange?: (data: T) => void;
	/** Called when save is requested */
	onSave?: (data: T) => Promise<void>;
	/** Called when cancel is requested */
	onCancel?: () => void;
	/** Called when validation status changes */
	onValidationChange?: (isValid: boolean, errors: ValidationError[]) => void;
}

/**
 * Props for all editor components
 */
export interface EditorProps<T> {
	/** Initial/Current data */
	value: T;
	/** Whether the editor is disabled */
	disabled?: boolean;
	/** Whether to show advanced/JSON mode toggle */
	showJsonMode?: boolean;
	/** Custom class names */
	className?: string;
	/** Editor callbacks */
	callbacks?: EditorCallbacks<T>;
}

// ============================================================
// Helper Types
// ============================================================

/**
 * Step validation result
 */
export interface StepValidationResult {
	/** Whether the step is valid */
	isValid: boolean;
	/** List of validation errors */
	errors: string[];
	/** Whether target selector is valid */
	targetValid: boolean;
	/** Target element info (if found) */
	targetInfo?: {
		tagName: string;
		id?: string;
		classes?: string[];
		text?: string;
	};
}

/**
 * Tour step template for creating new steps
 */
export interface TourStepTemplate {
	/** Template name */
	name: string;
	/** Template description */
	description: string;
	/** Pre-configured step */
	step: Omit<TourStep, 'id'>;
}
