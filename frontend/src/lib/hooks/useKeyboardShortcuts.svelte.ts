/**
 * Keyboard shortcuts hook for Svelte 5
 * Provides global keyboard shortcut functionality with proper cleanup
 */

import { goto } from '$app/navigation';

export interface Shortcut {
	/** Key to listen for (e.g., 's', 'k', 'enter') */
	key: string;
	/** Require Ctrl/Cmd key */
	ctrl?: boolean;
	/** Require Shift key */
	shift?: boolean;
	/** Require Alt/Option key */
	alt?: boolean;
	/** Action to execute when shortcut is triggered */
	action: () => void;
	/** Description for help documentation */
	description: string;
	/** Prevent default browser behavior when triggered */
	preventDefault?: boolean;
	/** Only trigger when this element or its children are focused */
	scope?: HTMLElement | null;
}

/**
 * Register keyboard shortcuts with automatic cleanup
 * @param shortcuts - Array of shortcut configurations
 * @returns Cleanup function to unregister shortcuts
 *
 * @example
 * ```svelte
 * <script lang="ts">
 *   import { onMount } from 'svelte';
 *   import { registerKeyboardShortcuts } from '$lib/hooks/useKeyboardShortcuts';
 *
 *   onMount(() => {
 *     return registerKeyboardShortcuts([
 *       {
 *         key: 's',
 *         ctrl: true,
 *         action: () => handleSave(),
 *         description: 'Save current form'
 *       },
 *       {
 *         key: 'n',
 *         ctrl: true,
 *         action: () => openCreateDialog(),
 *         description: 'Create new item'
 *       }
 *     ]);
 *   });
 * </script>
 * ```
 */
export function registerKeyboardShortcuts(shortcuts: Shortcut[]): () => void {
	function handleKeydown(e: KeyboardEvent): void {
		// Ignore if typing in input/textarea/contenteditable
		const target = e.target as HTMLElement;
		if (
			target.tagName === 'INPUT' ||
			target.tagName === 'TEXTAREA' ||
			target.isContentEditable
		) {
			// Allow Ctrl/Cmd shortcuts even in inputs for specific keys
			if (!e.ctrlKey && !e.metaKey) {
				return;
			}
		}

		// Check scope if provided
		for (const shortcut of shortcuts) {
			if (shortcut.scope && !shortcut.scope.contains(target)) {
				continue;
			}

			const matches =
				e.key.toLowerCase() === shortcut.key.toLowerCase() &&
				(shortcut.ctrl === undefined ||
					(shortcut.ctrl && (e.ctrlKey || e.metaKey))) &&
				(shortcut.shift === undefined || shortcut.shift === e.shiftKey) &&
				(shortcut.alt === undefined || shortcut.alt === e.altKey);

			if (matches) {
				if (shortcut.preventDefault !== false) {
					e.preventDefault();
				}
				shortcut.action();
				break;
			}
		}
	}

	window.addEventListener('keydown', handleKeydown);

	return () => {
		window.removeEventListener('keydown', handleKeydown);
	};
}

/**
 * Common keyboard shortcuts for the application
 */
export const commonShortcuts = {
	save: (action: () => void): Shortcut => ({
		key: 's',
		ctrl: true,
		action,
		description: 'Save',
		preventDefault: true
	}),

	createNew: (action: () => void): Shortcut => ({
		key: 'n',
		ctrl: true,
		action,
		description: 'Create new',
		preventDefault: true
	}),

	search: (action: () => void): Shortcut => ({
		key: 'k',
		ctrl: true,
		action,
		description: 'Search',
		preventDefault: true
	}),

	delete: (action: () => void): Shortcut => ({
		key: 'Delete',
		shift: true,
		action,
		description: 'Delete selected',
		preventDefault: true
	}),

	help: (action: () => void): Shortcut => ({
		key: '?',
		shift: true,
		action,
		description: 'Show keyboard shortcuts',
		preventDefault: true
	}),

	goToDashboard: (): Shortcut => ({
		key: 'g',
		shift: true,
		action: () => goto('/dashboard'),
		description: 'Go to Dashboard',
		preventDefault: true
	}),

	goToCampaigns: (): Shortcut => ({
		key: 'c',
		shift: true,
		action: () => goto('/campaigns'),
		description: 'Go to Campaigns',
		preventDefault: true
	}),

	goToSurveys: (): Shortcut => ({
		key: 's',
		shift: true,
		action: () => goto('/surveys'),
		description: 'Go to Surveys',
		preventDefault: true
	})
} as const;

/**
 * Hook for managing keyboard shortcuts in Svelte 5 components
 * Automatically handles registration and cleanup
 *
 * @example
 * ```svelte
 * <script lang="ts">
 *   import { useKeyboardShortcuts } from '$lib/hooks/useKeyboardShortcuts';
 *
 *   let saveLoading = $state(false);
 *
 *   useKeyboardShortcuts([
 *     {
 *       key: 's',
 *       ctrl: true,
 *       action: () => {
 *         if (!saveLoading) handleSave();
 *       },
 *       description: 'Save form'
 *     }
 *   ]);
 * </script>
 * ```
 */
export function useKeyboardShortcuts(shortcuts: Shortcut[]): void {
	$effect(() => {
		const cleanup = registerKeyboardShortcuts(shortcuts);
		return cleanup;
	});
}
