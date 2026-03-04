<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import { Select } from "$lib/components/ui/select";
    import {
        ArrowLeft,
        Settings,
        Share2,
        Info,
        Play,
        Copy,
        Check,
        Send,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        CampaignSettingsRequest,
        DistributionChannelResponse,
        CampaignStatus,
    } from "$lib/types";

    // --- State ---
    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);
    let activeTab = $state<"overview" | "settings" | "distribution">(
        "overview",
    );
    let copiedId = $state<string | null>(null);

    // Settings form
    let settings = $state<CampaignSettingsRequest>({
        captchaEnabled: false,
        oneResponsePerDevice: false,
        ipRestrictionEnabled: false,
        emailRestrictionEnabled: false,
        sessionTimeoutMinutes: 30,
        showQuestionNumbers: true,
        showProgressIndicator: true,
        allowBackButton: true,
        collectName: false,
        collectEmail: false,
        collectPhone: false,
        collectAddress: false,
    });
    let settingsLoading = $state(false);
    let settingsSaved = $state(false);

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

    async function saveSettings() {
        settingsLoading = true;
        settingsSaved = false;
        try {
            await api.put(`/campaigns/${campaignId}/settings`, settings);
            settingsSaved = true;
            setTimeout(() => (settingsSaved = false), 3000);
        } catch {
            // silent
        } finally {
            settingsLoading = false;
        }
    }

    async function activate() {
        activateLoading = true;
        try {
            await api.post(`/campaigns/${campaignId}/activate`);
            await loadCampaign();
        } catch {
            // silent
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
            activeTab = "distribution";
        } catch {
            // silent
        } finally {
            distributeLoading = false;
        }
    }

    async function copyToClipboard(text: string, id: string) {
        await navigator.clipboard.writeText(text);
        copiedId = id;
        setTimeout(() => (copiedId = null), 2000);
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    }

    function channelLabel(type: string): string {
        return type.replace(/_/g, " ");
    }

    const tabs = [
        { id: "overview" as const, label: "Overview", icon: Info },
        { id: "settings" as const, label: "Settings", icon: Settings },
        {
            id: "distribution" as const,
            label: "Distribution",
            icon: Share2,
        },
    ];

    onMount(loadCampaign);
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} — Survey Engine</title>
</svelte:head>

{#if loading}
    <div class="flex items-center justify-center py-16">
        <span
            class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
        ></span>
    </div>
{:else if campaign}
    <div class="space-y-6">
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
        <div class="flex gap-1 border-b border-border">
            {#each tabs as tab}
                <button
                    class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-px {activeTab ===
                    tab.id
                        ? 'border-primary text-primary'
                        : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'}"
                    onclick={() => (activeTab = tab.id)}
                >
                    <tab.icon class="h-4 w-4" />
                    {tab.label}
                </button>
            {/each}
        </div>

        <!-- Tab Content -->
        {#if activeTab === "overview"}
            <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Status</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <Badge
                            variant={statusBadgeVariant(campaign.status)}
                            class="text-base"
                        >
                            {campaign.status}
                        </Badge>
                    </Card.Content>
                </Card.Root>
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Auth Mode</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <Badge
                            variant={campaign.authMode === "PUBLIC"
                                ? "secondary"
                                : "outline"}
                            class="text-base"
                        >
                            {campaign.authMode}
                        </Badge>
                    </Card.Content>
                </Card.Root>
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Created</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <p class="text-sm font-medium text-foreground">
                            {formatDate(campaign.createdAt)}
                        </p>
                    </Card.Content>
                </Card.Root>
                <Card.Root class="sm:col-span-2 lg:col-span-3">
                    <Card.Header class="pb-2">
                        <Card.Description
                            >Distribution Channels</Card.Description
                        >
                    </Card.Header>
                    <Card.Content>
                        {#if channels.length > 0}
                            <p class="text-sm font-medium text-foreground">
                                {channels.length} channel{channels.length !== 1
                                    ? "s"
                                    : ""} generated
                            </p>
                        {:else}
                            <p class="text-sm text-muted-foreground">
                                {campaign.status === "DRAFT"
                                    ? "Activate the campaign first, then generate channels."
                                    : "No channels generated yet."}
                            </p>
                        {/if}
                    </Card.Content>
                </Card.Root>
            </div>
        {:else if activeTab === "settings"}
            <Card.Root>
                <Card.Header>
                    <Card.Title>Campaign Settings</Card.Title>
                    <Card.Description>
                        Configure security, limits, and UX options.
                    </Card.Description>
                </Card.Header>
                <Card.Content>
                    <form
                        onsubmit={(e) => {
                            e.preventDefault();
                            saveSettings();
                        }}
                        class="space-y-6"
                    >
                        <!-- Security -->
                        <div class="space-y-3">
                            <h3 class="text-sm font-semibold text-foreground">
                                Security
                            </h3>
                            <div class="grid gap-3 sm:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="s-password">Password</Label>
                                    <Input
                                        id="s-password"
                                        type="password"
                                        placeholder="Optional"
                                        bind:value={settings.password}
                                    />
                                </div>
                                <div class="space-y-2">
                                    <Label for="s-timeout"
                                        >Session Timeout (min)</Label
                                    >
                                    <Input
                                        id="s-timeout"
                                        type="number"
                                        min="1"
                                        bind:value={
                                            settings.sessionTimeoutMinutes
                                        }
                                    />
                                </div>
                            </div>
                            <div class="flex flex-wrap gap-4">
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.captchaEnabled}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Captcha
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={
                                            settings.oneResponsePerDevice
                                        }
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    One response per device
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={
                                            settings.ipRestrictionEnabled
                                        }
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    IP restriction
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={
                                            settings.emailRestrictionEnabled
                                        }
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Email restriction
                                </label>
                            </div>
                        </div>

                        <!-- Limits -->
                        <div class="space-y-3">
                            <h3 class="text-sm font-semibold text-foreground">
                                Limits
                            </h3>
                            <div class="grid gap-3 sm:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="s-quota">Response Quota</Label>
                                    <Input
                                        id="s-quota"
                                        type="number"
                                        min="0"
                                        placeholder="Unlimited"
                                        bind:value={settings.responseQuota}
                                    />
                                </div>
                                <div class="space-y-2">
                                    <Label for="s-close">Close Date</Label>
                                    <Input
                                        id="s-close"
                                        type="date"
                                        bind:value={settings.closeDate}
                                    />
                                </div>
                            </div>
                        </div>

                        <!-- UX -->
                        <div class="space-y-3">
                            <h3 class="text-sm font-semibold text-foreground">
                                User Experience
                            </h3>
                            <div class="flex flex-wrap gap-4">
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={
                                            settings.showQuestionNumbers
                                        }
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Question numbers
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={
                                            settings.showProgressIndicator
                                        }
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Progress indicator
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.allowBackButton}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Back button
                                </label>
                            </div>
                        </div>

                        <!-- Data Collection -->
                        <div class="space-y-3">
                            <h3 class="text-sm font-semibold text-foreground">
                                Data Collection
                            </h3>
                            <div class="flex flex-wrap gap-4">
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.collectName}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Name
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.collectEmail}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Email
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.collectPhone}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Phone
                                </label>
                                <label class="flex items-center gap-2 text-sm">
                                    <input
                                        type="checkbox"
                                        bind:checked={settings.collectAddress}
                                        class="h-4 w-4 rounded border-border"
                                    />
                                    Address
                                </label>
                            </div>
                        </div>

                        <!-- Messages -->
                        <div class="space-y-3">
                            <h3 class="text-sm font-semibold text-foreground">
                                Messages
                            </h3>
                            <div class="grid gap-3 sm:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="s-start">Start Message</Label>
                                    <Input
                                        id="s-start"
                                        placeholder="Welcome message..."
                                        bind:value={settings.startMessage}
                                    />
                                </div>
                                <div class="space-y-2">
                                    <Label for="s-finish">Finish Message</Label>
                                    <Input
                                        id="s-finish"
                                        placeholder="Thank you message..."
                                        bind:value={settings.finishMessage}
                                    />
                                </div>
                            </div>
                        </div>

                        <div class="flex items-center justify-end gap-3 pt-2">
                            {#if settingsSaved}
                                <span
                                    class="flex items-center gap-1 text-sm text-emerald-500"
                                >
                                    <Check class="h-4 w-4" />
                                    Saved
                                </span>
                            {/if}
                            <Button type="submit" disabled={settingsLoading}>
                                {#if settingsLoading}
                                    <span
                                        class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                    ></span>
                                {/if}
                                Save Settings
                            </Button>
                        </div>
                    </form>
                </Card.Content>
            </Card.Root>
        {:else if activeTab === "distribution"}
            {#if channels.length === 0}
                <Card.Root
                    class="flex flex-col items-center justify-center py-16 text-center"
                >
                    <Card.Content>
                        <div
                            class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                        >
                            <Share2 class="h-8 w-8 text-muted-foreground" />
                        </div>
                        <h3 class="text-lg font-semibold text-foreground">
                            No channels generated
                        </h3>
                        <p class="mt-1 text-sm text-muted-foreground">
                            {campaign.status === "DRAFT"
                                ? "Activate the campaign first to generate distribution channels."
                                : 'Click "Generate Channels" to create distribution links.'}
                        </p>
                        {#if campaign.status === "ACTIVE"}
                            <Button
                                class="mt-4"
                                onclick={distribute}
                                disabled={distributeLoading}
                            >
                                <Send class="mr-2 h-4 w-4" />
                                Generate Channels
                            </Button>
                        {/if}
                    </Card.Content>
                </Card.Root>
            {:else}
                <div class="space-y-3">
                    {#each channels as channel (channel.id)}
                        <Card.Root>
                            <Card.Content
                                class="flex items-center justify-between gap-4 py-4"
                            >
                                <div class="min-w-0 flex-1">
                                    <Badge variant="secondary" class="mb-1">
                                        {channelLabel(channel.channelType)}
                                    </Badge>
                                    <p
                                        class="mt-1 truncate font-mono text-xs text-muted-foreground"
                                    >
                                        {channel.channelValue}
                                    </p>
                                </div>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onclick={() =>
                                        copyToClipboard(
                                            channel.channelValue,
                                            channel.id,
                                        )}
                                >
                                    {#if copiedId === channel.id}
                                        <Check
                                            class="mr-1 h-4 w-4 text-emerald-500"
                                        />
                                        Copied
                                    {:else}
                                        <Copy class="mr-1 h-4 w-4" />
                                        Copy
                                    {/if}
                                </Button>
                            </Card.Content>
                        </Card.Root>
                    {/each}
                </div>
            {/if}
        {/if}
    </div>
{/if}
