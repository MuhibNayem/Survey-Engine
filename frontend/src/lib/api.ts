// ============================================================
// Survey Engine — API Client (Axios + Cookie Auth)
// ============================================================

import axios from 'axios';

const BASE_URL = '/api/v1';

// --- Axios Instance ---
// withCredentials ensures cookies are sent/received on every request
const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
    withCredentials: true
});

let csrfBootstrapPromise: Promise<void> | null = null;

function hasXsrfCookie(): boolean {
    if (typeof document === 'undefined') return true;
    return document.cookie
        .split(';')
        .map((cookie) => cookie.trim())
        .some((cookie) => cookie.startsWith('XSRF-TOKEN='));
}

async function ensureCsrfCookie() {
    if (typeof window === 'undefined' || hasXsrfCookie()) return;

    if (!csrfBootstrapPromise) {
        csrfBootstrapPromise = axios
            .get(`${BASE_URL}/admin/auth/csrf`, { withCredentials: true })
            .then(() => undefined)
            .finally(() => {
                csrfBootstrapPromise = null;
            });
    }

    await csrfBootstrapPromise;
}


api.interceptors.request.use(async (config) => {
    const method = (config.method || 'get').toLowerCase();
    const isMutation = method === 'post' || method === 'put' || method === 'patch' || method === 'delete';
    const isAuthEndpoint = config.url?.includes('/admin/auth/');

    if (isMutation && !isAuthEndpoint) {
        await ensureCsrfCookie();
        // Axios native xsrfCookieName/xsrfHeaderName handles the token mapping automatically 
        // as long as the cookie has been rendered by the server.
    }

    return config;
});

// --- Response Interceptor: Auto-refresh on 401 ---
let isRefreshing = false;
let failedQueue: { resolve: () => void; reject: (err: unknown) => void }[] = [];

function processQueue(error: unknown) {
    failedQueue.forEach((prom) => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve();
        }
    });
    failedQueue = [];
}

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Skip refresh for auth endpoints themselves
        if (
            error.response?.status === 401 &&
            !originalRequest._retry &&
            !originalRequest.url?.includes('/admin/auth/')
        ) {
            if (isRefreshing) {
                // Queue the request until refresh completes
                return new Promise((resolve, reject) => {
                    failedQueue.push({
                        resolve: () => resolve(api(originalRequest)),
                        reject
                    });
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                // Refresh token is sent automatically as a cookie
                await axios.post(`${BASE_URL}/admin/auth/refresh`, null, {
                    withCredentials: true
                });
                processQueue(null);
                // Retry original request — new access_token cookie is already set
                return api(originalRequest);
            } catch (refreshError) {
                processQueue(refreshError);
                // Clear user data and redirect to login
                if (typeof window !== 'undefined') {
                    sessionStorage.removeItem('user');
                    window.location.href = '/login';
                }
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        return Promise.reject(error);
    }
);

export default api;
