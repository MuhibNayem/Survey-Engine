import api from '$lib/api';
import { auth } from '$lib/stores/auth.svelte';
import { mode, setMode, userPrefersMode } from 'mode-watcher';

export type ThemeMode = 'light' | 'dark' | 'system';

type PreferenceResponse = {
    preferences?: Record<string, string>;
};

const THEME_PREFERENCE_KEY = 'ui.theme.mode';

function isValidThemeMode(value: unknown): value is ThemeMode {
    return value === 'light' || value === 'dark' || value === 'system';
}

function createThemeStore() {
    let backendHydratedForUser = $state<string | null>(null);
    let backendSyncInFlight = $state(false);

    function currentPreference(): ThemeMode {
        return isValidThemeMode(userPrefersMode.current)
            ? userPrefersMode.current
            : 'system';
    }

    function currentResolvedMode(): 'light' | 'dark' {
        return mode.current === 'dark' ? 'dark' : 'light';
    }

    async function persistPreference(nextMode: ThemeMode) {
        if (!auth.isAuthenticated || !auth.user?.email || backendSyncInFlight) {
            return;
        }

        backendSyncInFlight = true;
        try {
            await api.patch('/admin/preferences', {
                [THEME_PREFERENCE_KEY]: nextMode,
            });
            backendHydratedForUser = auth.user.email;
        } catch {
            // Keep the local persisted preference even if backend sync fails.
        } finally {
            backendSyncInFlight = false;
        }
    }

    async function hydrateFromBackend(force = false) {
        if (!auth.isAuthenticated || !auth.user?.email) {
            backendHydratedForUser = null;
            return;
        }

        if (!force && backendHydratedForUser === auth.user.email) {
            return;
        }

        try {
            const { data } = await api.get<PreferenceResponse>('/admin/preferences');
            const persistedMode = data?.preferences?.[THEME_PREFERENCE_KEY];
            if (isValidThemeMode(persistedMode) && persistedMode !== currentPreference()) {
                setMode(persistedMode);
            }
        } catch {
            // Fall back to localStorage persistence handled by mode-watcher.
        } finally {
            backendHydratedForUser = auth.user.email;
        }
    }

    async function setThemeMode(nextMode: ThemeMode, syncBackend = true) {
        setMode(nextMode);
        if (syncBackend) {
            await persistPreference(nextMode);
        }
    }

    async function toggleDarkMode() {
        const nextMode = currentResolvedMode() === 'dark' ? 'light' : 'dark';
        await setThemeMode(nextMode);
    }

    return {
        get modePreference() {
            return currentPreference();
        },
        get resolvedMode() {
            return currentResolvedMode();
        },
        get isDark() {
            return currentResolvedMode() === 'dark';
        },
        get backendSyncInFlight() {
            return backendSyncInFlight;
        },
        hydrateFromBackend,
        setThemeMode,
        toggleDarkMode,
    };
}

export const themePreferences = createThemeStore();
