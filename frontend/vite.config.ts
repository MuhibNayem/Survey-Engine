import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vite';

const usePolling = (globalThis as { process?: { env?: Record<string, string | undefined> } }).process?.env
	?.CHOKIDAR_USEPOLLING === '1';

export default defineConfig({
	plugins: [tailwindcss(), sveltekit()],
	server: {
		watch: usePolling
			? {
					usePolling: true,
					interval: 200
				}
			: undefined,
		proxy: {
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true
			}
		}
	}
});
