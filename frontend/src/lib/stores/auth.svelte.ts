// ============================================================
// Auth Store — Svelte 5 Runes (Cookie-Based Auth)
// ============================================================

import api from '$lib/api';
import type { AuthUserResponse, LoginRequest, RegisterRequest } from '$lib/types';
import { AxiosError } from 'axios';

interface User {
    userId?: string;
    email: string;
    fullName?: string;
    tenantId: string;
    role: string;
    firstLogin?: boolean;
}

function createAuthStore() {
    let user = $state<User | null>(null);
    let isLoading = $state(false);
    let error = $state<string | null>(null);
    let isImpersonating = $state(false);

    // Hydrate from sessionStorage on init
    if (typeof window !== 'undefined') {
        const stored = sessionStorage.getItem('user');
        if (stored) {
            try {
                user = JSON.parse(stored);
            } catch {
                user = null;
            }
        }
        isImpersonating = sessionStorage.getItem('is_impersonating') === 'true';
    }

    function persistUser(u: User | null) {
        user = u;
        if (typeof window !== 'undefined') {
            if (u) {
                sessionStorage.setItem('user', JSON.stringify(u));
            } else {
                sessionStorage.removeItem('user');
            }
        }
    }

    function handleAuthUserResponse(data: AuthUserResponse) {
        persistUser({
            userId: data.userId,
            email: data.email,
            fullName: data.fullName,
            tenantId: data.tenantId,
            role: data.role,
            firstLogin: data.firstLogin
        });
        error = null;
    }

    async function login(request: LoginRequest) {
        isLoading = true;
        error = null;
        try {
            const { data } = await api.post<AuthUserResponse>('/admin/auth/login', request);
            handleAuthUserResponse(data);
            return true;
        } catch (err) {
            if (err instanceof AxiosError) {
                error = err.response?.status === 401 ? 'Invalid email or password' : 'Login failed. Please try again.';
            } else {
                error = 'Network error. Please check your connection.';
            }
            return false;
        } finally {
            isLoading = false;
        }
    }

    async function register(request: RegisterRequest) {
        isLoading = true;
        error = null;
        try {
            const { data } = await api.post<AuthUserResponse>('/admin/auth/register', request);
            handleAuthUserResponse(data);
            return true;
        } catch (err) {
            if (err instanceof AxiosError) {
                const body = err.response?.data as { message?: string } | null;
                error = body?.message || 'Registration failed. Please try again.';
            } else {
                error = 'Network error. Please check your connection.';
            }
            return false;
        } finally {
            isLoading = false;
        }
    }

    async function logout() {
        try {
            await api.post('/admin/auth/logout');
        } catch {
            // Ignore
        }
        persistUser(null);
        if (typeof window !== 'undefined') {
            sessionStorage.removeItem('is_impersonating');
            isImpersonating = false;
            window.location.href = '/';
        }
    }

    function beginImpersonation(data: AuthUserResponse) {
        if (typeof window !== 'undefined') {
            sessionStorage.setItem('is_impersonating', 'true');
            isImpersonating = true;
        }
        handleAuthUserResponse(data);
    }

    async function revertImpersonation() {
        isLoading = true;
        try {
            await api.post('/admin/auth/revert-impersonation');
            if (typeof window !== 'undefined') {
                sessionStorage.removeItem('is_impersonating');
                isImpersonating = false;
            }
            await fetchCurrentUser();
            if (typeof window !== 'undefined') {
                window.location.href = '/admin/dashboard';
            }
        } catch (err) {
            console.error('Failed to revert impersonation', err);
            error = 'Could not revert impersonation. Please log out.';
        } finally {
            isLoading = false;
        }
    }

    async function fetchCurrentUser() {
        try {
            const { data } = await api.get<AuthUserResponse>('/admin/auth/me');
            handleAuthUserResponse(data);
            return true;
        } catch {
            persistUser(null);
            return false;
        }
    }

    return {
        get user() { return user; },
        get isAuthenticated() { return user !== null; },
        get isLoading() { return isLoading; },
        get error() { return error; },
        get isImpersonating() { return isImpersonating; },
        set error(v: string | null) { error = v; },
        login,
        register,
        logout,
        fetchCurrentUser,
        beginImpersonation,
        revertImpersonation
    };
}

export const auth = createAuthStore();
