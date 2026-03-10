<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { toast } from "svelte-sonner";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Confetti } from "$lib/components/confetti";
    import { ErrorBanner } from "$lib/components/error-banner";
    import {
        ArrowLeft,
        Settings,
        Palette,
        Share2,
        Info,
        Play,
        Send,
        Eye,
        BarChart3,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        DistributionChannelResponse,
        CampaignStatus,
    } from "$lib/types";

    let { children } = $props();

    // --- State ---
    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);

    // Confetti celebration
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');

    // API Error Banner - only for 500-level errors
    type ApiErrorState = {
        show: boolean;
        type: 'error';
        title: string;
        message: string;
    };
    let apiError = $state<ApiErrorState>({ show: false, type: 'error', title: '', message: '' });

    // Distribute
    let distributeLoading = $state(false);

    // Activate
    let activateLoading = $state(false);

    const campaignId = $derived(page.params.id);

    function statusBadgeVariant(status: CampaignStatus) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "DRAFT":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }

    // --- API ---
    async function loadCampaign() {
        loading = true;
        try {
            const { data } = await api.get<CampaignResponse>(
                `/campaigns/${campaignId}`,
            );
            campaign = data;

            // Load channels if active
            if (data.status !== "DRAFT") {
                try {
                    const chRes = await api.get<DistributionChannelResponse[]>(
                        `/campaigns/${campaignId}/channels`,
                    );
                    channels = chRes.data;
                } catch {
                    channels = [];
                }
            }
        } catch {
            goto("/campaigns");
        } finally {
            loading = false;
        }
    }

    async function activate() {
        activateLoading = true;
        apiError = { show: false, type: 'error', title: '', message: '' };
        try {
            await api.post(`/campaigns/${campaignId}/activate`);
            // 🎉 Celebrate campaign activation
            showConfetti = true;
            confettiTitle = '🚀 Campaign Activated!';
            confettiMessage = 'Your campaign is now live and collecting responses.';
            setTimeout(() => (showConfetti = false), 4500);
            await loadCampaign();
        } catch (err: any) {
            const status = err?.response?.status;
            const message = err?.response?.data?.message || 'Failed to activate campaign.';
            
            // Show banner only for 500-level errors
            if (status && status >= 500) {
                apiError = {
                    show: true,
                    type: 'error',
                    title: '🔴 Server Error',
                    message: 'Our servers are experiencing issues. Please try again later.'
                };
            }
        } finally {
            activateLoading = false;
        }
    }

    async function distribute() {
        distributeLoading = true;
        try {
            const { data } = await api.post<DistributionChannelResponse[]>(
                `/campaigns/${campaignId}/distribute`,
            );
            channels = data;
            toast.success("Channels generated successfully");
            goto(`/campaigns/${campaignId}/distribution`);
        } catch (err: any) {
            toast.error(
                err.response?.data?.message ||
                    "Failed to generate distribution channels.",
            );
        } finally {
            distributeLoading = false;
        }
    }

    const tabs = $derived([
        { href: `/campaigns/${campaignId}`, label: "Overview", icon: Info, exact: true },
        { href: `/campaigns/${campaignId}/settings`, label: "Settings", icon: Settings, exact: false },
        { href: `/campaigns/${campaignId}/theme`, label: "Theme Studio", icon: Palette, exact: false },
        { href: `/campaigns/${campaignId}/distribution`, label: "Distribution", icon: Share2, exact: false },
    ]);

    onMount(loadCampaign);
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} — Survey Engine</title>
</svelte:head>

{#if loading}
    <div class="space-y-6">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div class="flex items-center gap-3">
                <Skeleton class="h-9 w-9" />
                <div class="space-y-2">
                    <Skeleton class="h-8 w-[250px]" />
                    <Skeleton class="h-4 w-[180px]" />
                </div>
            </div>
            <div class="flex gap-2">
                <Skeleton class="h-9 w-[100px]" />
                <Skeleton class="h-9 w-[100px]" />
            </div>
        </div>
        <div class="flex gap-1 border-b border-border">
            {#each Array(3) as _}
                <Skeleton class="h-10 w-[120px]" />
            {/each}
        </div>
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {#each Array(6) as _}
                <div class="rounded-xl border bg-card text-card-foreground shadow-sm max-w-sm">
                    <div class="flex flex-col space-y-1.5 p-6 pb-2">
                        <Skeleton class="h-4 w-[100px]" />
                    </div>
                    <div class="p-6 pt-0">
                        <Skeleton class="h-6 w-[80px]" />
                    </div>
                </div>
            {/each}
        </div>
    </div>
{:else if campaign}
    <div class="space-y-6">
        <ErrorBanner
            show={apiError.show}
            type="failure"
            title={apiError.title}
            message={apiError.message}
            showRetry={true}
            onRetry={activate}
            onDismiss={() => (apiError = { show: false, type: 'error', title: '', message: '' })}
        />

        <!-- Header -->
        <div
            class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
        >
            <div class="flex items-center gap-3">
                <Button
                    variant="ghost"
                    size="sm"
                    onclick={() => goto("/campaigns")}
                >
                    <ArrowLeft class="h-4 w-4" />
                </Button>
                <div>
                    <div class="flex items-center gap-2">
                        <h1
                            class="text-2xl font-bold tracking-tight text-foreground"
                        >
                            {campaign.name}
                        </h1>
                        <Badge variant={statusBadgeVariant(campaign.status)}>
                            {campaign.status}
                        </Badge>
                    </div>
                    {#if campaign.description}
                        <p class="mt-0.5 text-sm text-muted-foreground">
                            {campaign.description}
                        </p>
                    {/if}
                </div>
            </div>
            <div class="flex gap-2">
                <Button
                    variant="outline"
                    onclick={() => goto(`/campaigns/${campaignId}/preview`)}
                >
                    <Eye class="mr-2 h-4 w-4" />
                    Preview
                </Button>
                {#if campaign.status === "DRAFT"}
                    <Button onclick={activate} disabled={activateLoading}>
                        {#if activateLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        <Play class="mr-2 h-4 w-4" />
                        Activate
                    </Button>
                {/if}
                {#if campaign.status === "ACTIVE" && channels.length === 0}
                    <Button onclick={distribute} disabled={distributeLoading}>
                        {#if distributeLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        <Send class="mr-2 h-4 w-4" />
                        Generate Channels
                    </Button>
                {/if}
            </div>
        </div>

        <!-- Tabs -->
        <div class="flex gap-1 border-b border-border overflow-x-auto whitespace-nowrap pb-px no-scrollbar">
            {#each tabs as tab}
                {@const isActive = tab.exact ? page.url.pathname === tab.href : page.url.pathname.startsWith(tab.href)}
                <a
                    href={tab.href}
                    class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] {isActive
                        ? 'border-primary text-primary'
                        : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'}"
                >
                    <tab.icon class="h-4 w-4" />
                    {tab.label}
                </a>
            {/each}
            <a
                href="/campaigns/{campaignId}/analytics"
                class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] {page.url.pathname.startsWith(`/campaigns/${campaignId}/analytics`) ? 'border-primary text-primary' : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'}"
            >
                <BarChart3 class="h-4 w-4" />
                Analytics
            </a>
        </div>

        <!-- Tab Content via slot/children -->
        <div class="pt-4">
            {@render children()}
        </div>
    </div>
{/if}

<!-- 🎉 Confetti Celebration -->
{#if showConfetti}
    <Confetti
        fire={showConfetti}
        showBanner={true}
        title={confettiTitle}
        message={confettiMessage}
        particleCount={150}
        spread={80}
        startVelocity={50}
        duration={3500}
        colors={['#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
