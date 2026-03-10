import Root from './ErrorBanner.svelte';

export {
	Root,
	//
	Root as ErrorBanner
};

export interface ErrorBannerProps {
	show?: boolean;
	title?: string;
	message?: string;
	type?: 'error' | 'failure' | 'warning' | 'info';
	duration?: number;
	onDismiss?: () => void;
	showRetry?: boolean;
	onRetry?: () => void;
	actionLabel?: string;
	onAction?: () => void;
}
