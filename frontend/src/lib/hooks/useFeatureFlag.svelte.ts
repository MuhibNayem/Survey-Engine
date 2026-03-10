/**
 * Hook for managing feature flag state
 * Provides reactive feature availability checking
 */

import { onMount } from 'svelte';
import { goto } from '$app/navigation';
import api from '$lib/api';

export interface FeatureFlagState {
  available: boolean;
  completed: boolean;
  show: boolean;
  loading: boolean;
  error: string | null;
}

export function useFeatureFlag(featureKey: string, options?: {
  autoCheck?: boolean;
  onComplete?: () => void;
}): FeatureFlagState {
  let available = $state(false);
  let completed = $state(false);
  let loading = $state(false);
  let error = $state<string | null>(null);

  const show = $derived(available && !completed);

  // Use $effect.pre to properly handle reactive featureKey
  $effect.pre(() => {
    const currentFeatureKey = featureKey;
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
    } catch (err) {
      error = 'Failed to check feature status';
      console.error('Feature flag check failed:', err);
    } finally {
      loading = false;
    }
  }

  async function markComplete(): Promise<void> {
    try {
      await api.post(`/features/${featureKey}/complete`, { completed: true });
      completed = true;
      options?.onComplete?.();
    } catch (err) {
      console.error('Failed to mark feature complete:', err);
    }
  }

  return {
    available,
    completed,
    show,
    loading,
    error
  };
}
