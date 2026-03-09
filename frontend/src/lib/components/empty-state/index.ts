import Root from './EmptyState.svelte';

export {
	Root,
	//
	Root as EmptyState
};

export interface EmptyStateProps {
	icon?: any;
	title: string;
	description?: string;
	actionLabel?: string;
	onAction?: () => void;
	secondaryActionLabel?: string;
	onSecondaryAction?: () => void;
	illustration?: 'campaign' | 'survey' | 'response' | 'question' | 'default';
}
