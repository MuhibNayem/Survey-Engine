/**
 * Hook for managing feature flag state
 * Provides reactive feature availability checking
 */

import { onMount } from 'svelte';
import { goto } from '$app/navigation';
import api from '$lib/api';

export interface FeatureFlagStatus {
  shouldShow: boolean;
  isCompleted: boolean;
}

export interface FeatureFlagState {
  available: boolean;
  completed: boolean;
  show: boolean;
  loading: boolean;
  error: string | null;
  status: FeatureFlagStatus | null;
  isCompleted: boolean;
  shouldShow: boolean;
  complete: () => Promise<void>;
  reset: () => Promise<void>;
}

export function useFeatureFlag(featureKey: string | (() => string), options?: {
  autoCheck?: boolean;
  autoRecordAccess?: boolean;
  onComplete?: () => void;
}): FeatureFlagState {
  let available = $state(false);
  let completed = $state(false);
  let loading = $state(false);
  let error = $state<string | null>(null);

  const show = $derived(available && !completed);
  const status = $derived({ shouldShow: show, isCompleted: completed });
  const isCompleted = $derived(completed);
  const shouldShow = $derived(show);

  // Use $effect.pre to properly handle reactive featureKey
  $effect.pre(() => {
    const currentFeatureKey = typeof featureKey === 'function' ? featureKey() : featureKey;
    const shouldAutoCheck = options?.autoCheck !== false;
    
    if (shouldAutoCheck && currentFeatureKey) {
      checkFeature(currentFeatureKey);
    }
  });

  async function checkFeature(key: string): Promise<void> {
    loading = true;
    error = null;

    try {
      const [availableRes, statusRes] = await Promise.allSettled([
        api.get(`/features/available`),
        api.get(`/features/${key}/status`)
      ]);

      if (availableRes.status === 'fulfilled') {
        const responseData = availableRes.value.data;
        const features = responseData?.content || responseData || [];
        available = features.some((f: any) => f.featureKey === key);
      }

      if (statusRes.status === 'fulfilled') {
        completed = statusRes.value.data?.completed || false;
      }

      if (options?.autoRecordAccess && show) {
        recordAccess(key);
      }
    } catch (err) {
      error = 'Failed to check feature status';
      console.error('Feature flag check failed:', err);
    } finally {
      loading = false;
    }
  }

  async function recordAccess(key: string): Promise<void> {
    try {
      await api.post(`/features/${key}/access`);
    } catch (err) {
      console.warn('Failed to record feature access:', err);
    }
  }

  async function markComplete(): Promise<void> {
    try {
      const currentFeatureKey = typeof featureKey === 'function' ? featureKey() : featureKey;
      await api.post(`/features/${currentFeatureKey}/complete`, { completed: true });
      completed = true;
      options?.onComplete?.();
    } catch (err) {
      console.error('Failed to mark feature complete:', err);
    }
  }

  async function resetState(): Promise<void> {
    try {
      const currentFeatureKey = typeof featureKey === 'function' ? featureKey() : featureKey;
      await api.post(`/features/${currentFeatureKey}/complete`, { completed: false });
      completed = false;
    } catch (err) {
      console.error('Failed to reset feature state:', err);
    }
  }

  return {
    get available() { return available; },
    get completed() { return completed; },
    get show() { return show; },
    get loading() { return loading; },
    get error() { return error; },
    get status() { return status; },
    get isCompleted() { return isCompleted; },
    get shouldShow() { return shouldShow; },
    complete: markComplete,
    reset: resetState
  };
}
