<script lang="ts">
	import Combobox, { type ComboboxOption } from './combobox.svelte';
	import api from '$lib/api';

	export interface TenantOption extends ComboboxOption {
		id: string;
		name: string;
		active: boolean;
		adminEmail?: string;
		subscriptionPlan?: string;
	}

	interface Props {
		value?: string | null;
		placeholder?: string;
		label?: string;
		required?: boolean;
		disabled?: boolean;
		onValueChange?: (value: string | null) => void;
	}

	let {
		value = $bindable(null),
		placeholder = 'Search and select tenant...',
		label = 'Tenant',
		required = false,
		disabled = false,
		onValueChange
	}: Props = $props();

	function mapTenantToOption(tenant: any): TenantOption {
		return {
			value: tenant.tenantId || tenant.id,
			label: tenant.name,
			description: !tenant.active ? 'Inactive' : undefined,
			disabled: !tenant.active,
			id: tenant.tenantId || tenant.id,
			name: tenant.name,
			active: tenant.active,
			adminEmail: tenant.primaryEmail,
			subscriptionPlan: tenant.plan
		};
	}

	function optionRenderer(option: ComboboxOption): string {
		const tenantOpt = option as TenantOption;
		return tenantOpt.name;
	}

	async function handleSearch(query: string, page: number): Promise<{ options: TenantOption[]; hasMore: boolean }> {
		const params = new URLSearchParams();
		if (query) params.set('search', query);
		params.set('page', page.toString());
		params.set('size', '20');

		try {
			// Get ALL tenants from SUPER_ADMIN endpoint
			const response = await api.get(`/admin/superadmin/tenants?${params.toString()}`);
			const data = response.data;
			const tenants = data.content || data;
			
			if (!tenants || tenants.length === 0) {
				console.warn('No tenants found. Make sure you are logged in as SUPER_ADMIN.');
			}
			
			return {
				options: tenants.map(mapTenantToOption),
				hasMore: data.totalPages ? page < data.totalPages - 1 : false
			};
		} catch (error: any) {
			if (error.response?.status === 401) {
				console.error('Authentication required. Please log in as SUPER_ADMIN to manage tenants.');
			} else if (error.response?.status === 403) {
				console.error('Access denied. SUPER_ADMIN role required to view tenants.');
			} else {
				console.error('Failed to load tenants:', error);
			}
			return {
				options: [],
				hasMore: false
			};
		}
	}
</script>

<Combobox
	{value}
	{placeholder}
	{label}
	{required}
	{disabled}
	searchPlaceholder="Search tenants by name..."
	emptyMessage="No tenants found. Try a different search term."
	pageSize={20}
	minSearchLength={0}
	searchDebounce={300}
	{onValueChange}
	{optionRenderer}
	onSearch={handleSearch}
/>
