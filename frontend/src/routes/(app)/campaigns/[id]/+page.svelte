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
    import { Switch } from "$lib/components/ui/switch";
    import { Textarea } from "$lib/components/ui/textarea";
    import { toast } from "svelte-sonner";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        ArrowLeft,
        Settings,
        Share2,
        Info,
        Play,
        Copy,
        Check,
        Send,
        Eye,
        CalendarDays,
        X,
        BarChart3,
        Plus,
        Trash2,
        GripVertical
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        CampaignSettingsRequest,
        CampaignSettingsResponse,
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
        dataCollectionFields: [],
    });
    let settingsLoading = $state(false);
    let settingsSaved = $state(false);
    let headerEnabled = $state(false);
    let footerEnabled = $state(false);
    let showAdvancedHeader = $state(false);
    let showAdvancedFooter = $state(false);
    let brandingSyncReady = $state(false);
    let headerTemplate = $state<"clean" | "accent" | "notice">("clean");
    let footerTemplate = $state<"simple" | "support" | "compliance">("simple");
    let headerFields = $state({
        title: "",
        subtitle: "",
        note: "",
    });
    let footerFields = $state({
        line1: "",
        line2: "",
        legal: "",
    });

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
            await loadSettings();

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

    function normalizeDateForInput(value?: string) {
        if (!value) {
            return undefined;
        }
        return value.includes("T") ? value.split("T")[0] : value;
    }

    function toNumberOrNull(value: unknown) {
        if (typeof value === "number" && Number.isFinite(value)) {
            return value;
        }
        if (typeof value === "string" && value.trim() !== "") {
            const parsed = Number(value);
            if (Number.isFinite(parsed)) {
                return parsed;
            }
        }
        return null;
    }

    function toIsoCloseDateOrNull(value?: string) {
        if (!value || value.trim() === "") {
            return null;
        }
        // Send full ISO instant to satisfy backend Instant parsing.
        return `${value}T23:59:59Z`;
    }

    function openCloseDatePicker() {
        const input = document.getElementById("s-close") as HTMLInputElement | null;
        input?.showPicker?.();
        input?.focus();
    }

    function buildSettingsPayload() {
        return {
            ...settings,
            closeDate: toIsoCloseDateOrNull(settings.closeDate),
            responseQuota: toNumberOrNull(settings.responseQuota),
            sessionTimeoutMinutes:
                toNumberOrNull(settings.sessionTimeoutMinutes) ?? 30,
        };
    }

    async function loadSettings() {
        try {
            const { data } = await api.get<CampaignSettingsResponse>(
                `/campaigns/${campaignId}/settings`,
            );
            settings = {
                password: data.password ?? "",
                captchaEnabled: data.captchaEnabled ?? false,
                oneResponsePerDevice: data.oneResponsePerDevice ?? false,
                ipRestrictionEnabled: data.ipRestrictionEnabled ?? false,
                emailRestrictionEnabled: data.emailRestrictionEnabled ?? false,
                responseQuota: data.responseQuota ?? undefined,
                closeDate: normalizeDateForInput(data.closeDate),
                sessionTimeoutMinutes: data.sessionTimeoutMinutes ?? 30,
                showQuestionNumbers: data.showQuestionNumbers ?? true,
                showProgressIndicator: data.showProgressIndicator ?? true,
                allowBackButton: data.allowBackButton ?? true,
                startMessage: data.startMessage ?? "",
                finishMessage: data.finishMessage ?? "",
                headerHtml: data.headerHtml ?? "",
                footerHtml: data.footerHtml ?? "",
                collectName: data.collectName ?? false,
                collectEmail: data.collectEmail ?? false,
                collectPhone: data.collectPhone ?? false,
                collectAddress: data.collectAddress ?? false,
                dataCollectionFields: data.dataCollectionFields ?? [],
            };
            initializeBrandingBuilder();
        } catch {
            // keep defaults
        }
    }

    function addDataCollectionField() {
        if (!settings.dataCollectionFields) {
            settings.dataCollectionFields = [];
        }
        settings.dataCollectionFields.push({
            fieldKey: "new_field",
            label: "New Field",
            fieldType: "TEXT",
            required: false,
            sortOrder: settings.dataCollectionFields.length,
            enabled: true,
        });
    }

    function removeDataCollectionField(index: number) {
        if (!settings.dataCollectionFields) return;
        settings.dataCollectionFields.splice(index, 1);
    }

    async function saveSettings() {
        settingsLoading = true;
        settingsSaved = false;
        try {
            await api.put(`/campaigns/${campaignId}/settings`, buildSettingsPayload());
            await loadSettings();
            settingsSaved = true;
            setTimeout(() => (settingsSaved = false), 3000);
        } catch {
            // silent
        } finally {
            settingsLoading = false;
        }
    }

    function escapeHtml(value: string) {
        return value
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }

    function buildHeaderHtml() {
        const title = escapeHtml(headerFields.title || campaign?.name || "Survey");
        const subtitle = escapeHtml(
            headerFields.subtitle ||
                campaign?.description ||
                "Share your feedback",
        );
        const note = escapeHtml(headerFields.note);

        if (headerTemplate === "accent") {
            return `<section style="border:1px solid #1e293b;background:linear-gradient(135deg,#0f172a 0%,#1d4ed8 100%);color:#f8fafc;padding:18px 20px;border-radius:14px;">
  <div style="font-size:11px;letter-spacing:.08em;text-transform:uppercase;font-weight:600;opacity:.82;">Enterprise Feedback Form</div>
  <h2 style="margin:10px 0 0;font-size:26px;line-height:1.2;font-weight:700;">${title}</h2>
  <p style="margin:6px 0 0;font-size:14px;line-height:1.5;opacity:.95;">${subtitle}</p>
  ${note ? `<p style="margin:12px 0 0;font-size:12px;line-height:1.5;opacity:.9;border-top:1px solid rgba(248,250,252,0.25);padding-top:10px;">${note}</p>` : ""}
</section>`;
        }

        if (headerTemplate === "notice") {
            return `<section style="background:linear-gradient(180deg,#f0f9ff 0%,#e0f2fe 100%);border:1px solid #bae6fd;padding:14px 16px;border-radius:12px;">
  <div style="font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:#0c4a6e;font-weight:600;">Important Notice</div>
  <h3 style="margin:6px 0 0;font-size:18px;line-height:1.35;color:#0f172a;">${title}</h3>
  <p style="margin:6px 0 0;font-size:13px;line-height:1.5;color:#155e75;">${subtitle}</p>
  ${note ? `<p style="margin:10px 0 0;font-size:12px;line-height:1.5;color:#0f766e;border-top:1px solid #bae6fd;padding-top:8px;">${note}</p>` : ""}
</section>`;
        }

        return `<section style="background:#ffffff;border:1px solid #e2e8f0;border-radius:12px;padding:16px 18px;box-shadow:0 1px 2px rgba(15,23,42,0.06);">
  <div style="display:flex;align-items:flex-start;gap:12px;">
    <div style="width:4px;min-height:56px;border-radius:999px;background:#1d4ed8;"></div>
    <div>
      <h2 style="margin:0;font-size:23px;line-height:1.25;color:#0f172a;font-weight:700;">${title}</h2>
      <p style="margin:6px 0 0;color:#475569;font-size:14px;line-height:1.5;">${subtitle}</p>
      ${note ? `<p style="margin:10px 0 0;color:#64748b;font-size:12px;line-height:1.5;">${note}</p>` : ""}
    </div>
  </div>
</section>`;
    }

    function buildFooterHtml() {
        const line1 = escapeHtml(
            footerFields.line1 || "Thank you for completing this survey.",
        );
        const line2 = escapeHtml(footerFields.line2);
        const legal = escapeHtml(footerFields.legal);

        if (footerTemplate === "support") {
            return `<footer style="border:1px solid #e2e8f0;background:#f8fafc;border-radius:12px;padding:14px 16px;color:#334155;font-size:12px;">
  <p style="margin:0;font-weight:600;color:#0f172a;">${line1}</p>
  <p style="margin:6px 0 0;line-height:1.5;">${line2 || "Need assistance? Contact your survey administrator for support."}</p>
  <p style="margin:8px 0 0;color:#64748b;line-height:1.5;">${legal || "Responses are securely processed under your organization's data policy."}</p>
</footer>`;
        }

        if (footerTemplate === "compliance") {
            return `<footer style="padding-top:12px;border-top:1px solid #dbe1ea;color:#475569;font-size:12px;">
  <p style="margin:0;font-weight:600;color:#334155;">${line1}</p>
  ${line2 ? `<p style="margin:5px 0 0;line-height:1.5;">${line2}</p>` : ""}
  <p style="margin:5px 0 0;line-height:1.5;color:#64748b;">${legal || "Data usage and retention follow approved compliance policy."}</p>
</footer>`;
        }

        return `<footer style="padding-top:10px;border-top:1px solid #e2e8f0;color:#64748b;font-size:12px;text-align:center;">
  <p style="margin:0;font-weight:600;color:#475569;">${line1}</p>
  ${line2 ? `<p style="margin:5px 0 0;line-height:1.5;">${line2}</p>` : ""}
</footer>`;
    }

    function initializeBrandingBuilder() {
        headerEnabled = !!(settings.headerHtml && settings.headerHtml.trim() !== "");
        footerEnabled = !!(settings.footerHtml && settings.footerHtml.trim() !== "");
        headerFields = {
            title: campaign?.name ?? "Survey",
            subtitle: campaign?.description ?? "",
            note: "",
        };
        footerFields = {
            line1: "Thank you for completing this survey.",
            line2: "",
            legal: "",
        };
        showAdvancedHeader = false;
        showAdvancedFooter = false;
        brandingSyncReady = true;
    }

    function applyHeaderBuilder() {
        settings.headerHtml = buildHeaderHtml();
    }

    function applyFooterBuilder() {
        settings.footerHtml = buildFooterHtml();
    }

    $effect(() => {
        if (!brandingSyncReady) return;
        if (!headerEnabled) {
            settings.headerHtml = "";
        }
        if (!footerEnabled) {
            settings.footerHtml = "";
        }
    });

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
            toast.success("Channels generated successfully");
        } catch (err: any) {
            toast.error(
                err.response?.data?.message ||
                    "Failed to generate distribution channels.",
            );
        } finally {
            distributeLoading = false;
        }
    }

    async function copyToClipboard(text: string, id: string) {
        try {
            if (navigator?.clipboard && navigator.clipboard.writeText) {
                await navigator.clipboard.writeText(text);
            } else {
                const textArea = document.createElement("textarea");
                textArea.value = text;
                textArea.style.position = "fixed";
                textArea.style.left = "-999999px";
                textArea.style.top = "-999999px";
                document.body.appendChild(textArea);
                textArea.focus();
                textArea.select();
                try {
                    document.execCommand('copy');
                } finally {
                    textArea.remove();
                }
            }
            copiedId = id;
            setTimeout(() => (copiedId = null), 2000);
        } catch (err) {
            console.error("Failed to copy:", err);
            toast.error("Failed to copy to clipboard");
        }
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
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Skeleton class="h-4 w-[100px]" />
                    </Card.Header>
                    <Card.Content>
                        <Skeleton class="h-6 w-[80px]" />
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
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
                <button
                    class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] {activeTab ===
                    tab.id
                        ? 'border-primary text-primary'
                        : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'}"
                    onclick={() => (activeTab = tab.id)}
                >
                    <tab.icon class="h-4 w-4" />
                    {tab.label}
                </button>
            {/each}
            <button
                class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] border-transparent text-muted-foreground hover:text-foreground hover:border-border"
                onclick={() => goto(`/campaigns/${campaignId}/analytics`)}
            >
                <BarChart3 class="h-4 w-4" />
                Analytics
            </button>
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
            <form
                onsubmit={(e) => {
                    e.preventDefault();
                    saveSettings();
                }}
                class="space-y-6"
            >
                <div class="grid gap-6 md:grid-cols-2">
                    <!-- Security -->
                    <Card.Root>
                        <Card.Header>
                            <Card.Title class="text-base">Security</Card.Title>
                            <Card.Description
                                >Access control and rate limiting</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
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
                                    bind:value={settings.sessionTimeoutMinutes}
                                />
                            </div>

                            <div class="space-y-3 pt-2">
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            Captcha
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Require human verification
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={settings.captchaEnabled}
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            One response per device
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Prevent duplicate submissions via cookies
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.oneResponsePerDevice
                                        }
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            IP Restriction
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Block repeated IPs
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.ipRestrictionEnabled
                                        }
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            Email Restriction
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Require unique email addresses
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.emailRestrictionEnabled
                                        }
                                    />
                                </div>
                            </div>
                        </Card.Content>
                    </Card.Root>

                    <!-- User Experience & Limits -->
                    <div class="space-y-6">
                        <Card.Root>
                            <Card.Header>
                                <Card.Title class="text-base"
                                    >User Experience</Card.Title
                                >
                                <Card.Description
                                    >Configure how respondents see the survey</Card.Description
                                >
                            </Card.Header>
                            <Card.Content class="space-y-3">
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Show Question Numbers</Label
                                    >
                                    <Switch
                                        bind:checked={
                                            settings.showQuestionNumbers
                                        }
                                    />
                                </div>
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Show Progress Indicator</Label
                                    >
                                    <Switch
                                        bind:checked={
                                            settings.showProgressIndicator
                                        }
                                    />
                                </div>
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Allow Back Button</Label
                                    >
                                    <Switch
                                        bind:checked={settings.allowBackButton}
                                    />
                                </div>
                            </Card.Content>
                        </Card.Root>

                        <Card.Root>
                            <Card.Header>
                                <Card.Title class="text-base">Limits</Card.Title
                                >
                                <Card.Description
                                    >Time and capacity boundaries</Card.Description
                                >
                            </Card.Header>
                            <Card.Content class="grid gap-3 sm:grid-cols-2">
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
                                    <div class="relative">
                                        <input
                                            id="s-close"
                                            type="date"
                                            class="h-10 w-full rounded-lg border border-border bg-background pl-10 pr-20 text-sm outline-none ring-offset-background transition-colors focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                                            bind:value={settings.closeDate}
                                        />
                                        <CalendarDays
                                            class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                                        />
                                        <div
                                            class="absolute right-2 top-1/2 flex -translate-y-1/2 items-center gap-1"
                                        >
                                            {#if settings.closeDate}
                                                <Button
                                                    type="button"
                                                    size="sm"
                                                    variant="ghost"
                                                    class="h-7 px-2 text-xs"
                                                    onclick={() =>
                                                        (settings.closeDate =
                                                            undefined)}
                                                >
                                                    <X class="h-3.5 w-3.5" />
                                                </Button>
                                            {/if}
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant="outline"
                                                class="h-7 px-2 text-xs"
                                                onclick={openCloseDatePicker}
                                            >
                                                Pick
                                            </Button>
                                        </div>
                                    </div>
                                    <p class="text-xs text-muted-foreground">
                                        Stored as end-of-day UTC for reliable backend parsing.
                                    </p>
                                </div>
                            </Card.Content>
                        </Card.Root>
                    </div>

                    <!-- Data Collection Fields -->
                    <Card.Root class="md:col-span-2">
                        <Card.Header>
                            <div class="flex items-center justify-between">
                                <div>
                                    <Card.Title class="text-base">Data Collection</Card.Title>
                                    <Card.Description>Configure what information respondents must provide before starting the survey.</Card.Description>
                                </div>
                                <Button size="sm" variant="outline" onclick={addDataCollectionField} class="h-8">
                                    <Plus class="mr-2 h-4 w-4" /> Add Field
                                </Button>
                            </div>
                        </Card.Header>
                        <Card.Content>
                            {#if !settings.dataCollectionFields || settings.dataCollectionFields.length === 0}
                                <div class="flex flex-col items-center justify-center rounded-md border border-dashed p-6 text-center text-sm text-muted-foreground">
                                    No data collection fields configured.
                                    <button type="button" class="mt-1 font-medium text-primary hover:underline" onclick={addDataCollectionField}>
                                        Add your first field
                                    </button>
                                </div>
                            {:else}
                                <div class="space-y-4">
                                    {#each settings.dataCollectionFields as field, i}
                                        <div class="flex flex-col gap-4 rounded-md border bg-muted/40 p-4 sm:flex-row sm:items-start">
                                            <div class="pt-2 text-muted-foreground hidden sm:block shrink-0">
                                                <GripVertical class="h-5 w-5" />
                                            </div>
                                            <div class="grid flex-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                                                <!-- Field Key -->
                                                <div class="space-y-2">
                                                    <div class="flex items-center gap-1.5">
                                                        <Label for="key-{i}">Key</Label>
                                                        <div 
                                                            title="Internal variable name used for data segregation and analytics. Must be alphanumeric (e.g., student_id, age)." 
                                                            class="text-muted-foreground hover:text-foreground cursor-help"
                                                        >
                                                            <Info class="h-3.5 w-3.5" />
                                                        </div>
                                                    </div>
                                                    <Input
                                                        id="key-{i}"
                                                        bind:value={field.fieldKey}
                                                        placeholder="e.g., student_id"
                                                    />
                                                    <p class="text-[10px] text-muted-foreground">Internal identifier</p>
                                                </div>
                                                
                                                <!-- Display Label -->
                                                <div class="space-y-2">
                                                    <Label for="label-{i}">Display Label</Label>
                                                    <Input
                                                        id="label-{i}"
                                                        bind:value={field.label}
                                                        placeholder="e.g., Your Full Name"
                                                    />
                                                </div>

                                                <!-- Type -->
                                                <div class="space-y-2">
                                                    <Label for="type-{i}">Input Type</Label>
                                                    <select
                                                        id="type-{i}"
                                                        bind:value={field.fieldType}
                                                        class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
                                                    >
                                                        <option value="TEXT">Short Text</option>
                                                        <option value="EMAIL">Email Address</option>
                                                        <option value="PHONE">Phone Number</option>
                                                        <option value="NUMBER">Number</option>
                                                        <option value="TEXTAREA">Long Text (Paragraph)</option>
                                                    </select>
                                                </div>

                                                <!-- Toggles -->
                                                <div class="space-y-2 flex flex-col">
                                                    <Label class="text-transparent hidden sm:block">&nbsp;</Label>
                                                    <div class="flex items-center space-x-2 h-9">
                                                        <Switch id="req-{i}" bind:checked={field.required} />
                                                        <Label for="req-{i}" class="text-sm font-normal cursor-pointer leading-none">Required</Label>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="flex justify-end sm:flex-col sm:space-y-2">
                                                <Label class="text-transparent hidden sm:block">&nbsp;</Label>
                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    class="h-9 w-9 text-muted-foreground hover:bg-destructive/10 hover:text-destructive shrink-0"
                                                    onclick={() => removeDataCollectionField(i)}
                                                    type="button"
                                                    title="Remove field"
                                                >
                                                    <Trash2 class="h-4 w-4" />
                                                    <span class="sr-only">Remove field</span>
                                                </Button>
                                            </div>
                                        </div>
                                    {/each}
                                </div>
                            {/if}
                        </Card.Content>
                    </Card.Root>

                    <!-- Messages -->
                    <Card.Root class="md:col-span-2">
                        <Card.Header>
                            <Card.Title class="text-base">Messages</Card.Title>
                            <Card.Description
                                >Custom text displayed to respondents</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            <div class="space-y-2">
                                <Label for="s-start">Start Message</Label>
                                <Input
                                    id="s-start"
                                    placeholder="Welcome to our survey..."
                                    bind:value={settings.startMessage}
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="s-finish">Finish Message</Label>
                                <Input
                                    id="s-finish"
                                    placeholder="Thank you for participating..."
                                    bind:value={settings.finishMessage}
                                />
                            </div>
                        </Card.Content>
                    </Card.Root>

                    <!-- Header/Footer Branding -->
                    <Card.Root class="md:col-span-2">
                        <Card.Header>
                            <Card.Title class="text-base">
                                Form Header & Footer
                            </Card.Title>
                            <Card.Description>
                                No HTML required. Choose a template, fill a few
                                fields, and preview instantly.
                            </Card.Description>
                        </Card.Header>
                        <Card.Content class="space-y-6">
                            <div class="rounded-lg border border-border bg-muted/30 p-3 text-xs text-muted-foreground">
                                Tip: Use Campaign Preview after saving to verify
                                what responders will see.
                            </div>

                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">
                                            Header Block
                                        </Label>
                                        <p class="text-xs text-muted-foreground">
                                            Shown above survey questions.
                                        </p>
                                    </div>
                                    <Switch bind:checked={headerEnabled} />
                                </div>

                                {#if headerEnabled}
                                    <div class="space-y-3">
                                        <Label class="text-xs uppercase tracking-wide text-muted-foreground">
                                            Template
                                        </Label>
                                        <div class="flex flex-wrap gap-2">
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={headerTemplate === "clean"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() => (headerTemplate = "clean")}
                                            >
                                                Clean
                                            </Button>
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={headerTemplate === "accent"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() => (headerTemplate = "accent")}
                                            >
                                                Accent
                                            </Button>
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={headerTemplate === "notice"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() => (headerTemplate = "notice")}
                                            >
                                                Notice
                                            </Button>
                                        </div>
                                    </div>

                                    <div class="grid gap-3 md:grid-cols-2">
                                        <div class="space-y-2">
                                            <Label for="header-title">Title</Label>
                                            <Input
                                                id="header-title"
                                                placeholder="Course Evaluation"
                                                bind:value={headerFields.title}
                                            />
                                        </div>
                                        <div class="space-y-2">
                                            <Label for="header-subtitle">Subtitle</Label>
                                            <Input
                                                id="header-subtitle"
                                                placeholder="Share your experience"
                                                bind:value={headerFields.subtitle}
                                            />
                                        </div>
                                        <div class="space-y-2 md:col-span-2">
                                            <Label for="header-note">
                                                Optional Note
                                            </Label>
                                            <Input
                                                id="header-note"
                                                placeholder="Estimated completion time: 3 minutes"
                                                bind:value={headerFields.note}
                                            />
                                        </div>
                                    </div>

                                    <div class="flex items-center justify-between">
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onclick={applyHeaderBuilder}
                                        >
                                            Apply Header Template
                                        </Button>
                                        <Button
                                            type="button"
                                            size="sm"
                                            variant="ghost"
                                            onclick={() =>
                                                (showAdvancedHeader =
                                                    !showAdvancedHeader)}
                                        >
                                            {showAdvancedHeader
                                                ? "Hide Advanced HTML"
                                                : "Edit Advanced HTML"}
                                        </Button>
                                    </div>

                                    <div class="campaign-branding-preview">
                                        {#if settings.headerHtml && settings.headerHtml.trim() !== ""}
                                            {@html settings.headerHtml}
                                        {:else}
                                            <p class="text-sm text-muted-foreground">
                                                Header preview will appear here after applying a template.
                                            </p>
                                        {/if}
                                    </div>

                                    {#if showAdvancedHeader}
                                        <div class="space-y-2">
                                            <Label for="s-header-html">
                                                Advanced Header HTML
                                            </Label>
                                            <Textarea
                                                id="s-header-html"
                                                rows={8}
                                                class="font-mono text-xs"
                                                placeholder="<header>...</header>"
                                                bind:value={settings.headerHtml}
                                            />
                                        </div>
                                    {/if}
                                {/if}
                            </div>

                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">
                                            Footer Block
                                        </Label>
                                        <p class="text-xs text-muted-foreground">
                                            Shown after survey content.
                                        </p>
                                    </div>
                                    <Switch bind:checked={footerEnabled} />
                                </div>

                                {#if footerEnabled}
                                    <div class="space-y-3">
                                        <Label class="text-xs uppercase tracking-wide text-muted-foreground">
                                            Template
                                        </Label>
                                        <div class="flex flex-wrap gap-2">
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={footerTemplate === "simple"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() => (footerTemplate = "simple")}
                                            >
                                                Simple
                                            </Button>
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={footerTemplate === "support"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() => (footerTemplate = "support")}
                                            >
                                                Support
                                            </Button>
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant={footerTemplate ===
                                                "compliance"
                                                    ? "default"
                                                    : "outline"}
                                                onclick={() =>
                                                    (footerTemplate =
                                                        "compliance")}
                                            >
                                                Compliance
                                            </Button>
                                        </div>
                                    </div>

                                    <div class="grid gap-3 md:grid-cols-2">
                                        <div class="space-y-2">
                                            <Label for="footer-line-1">
                                                Primary Line
                                            </Label>
                                            <Input
                                                id="footer-line-1"
                                                placeholder="Thank you for your feedback"
                                                bind:value={footerFields.line1}
                                            />
                                        </div>
                                        <div class="space-y-2">
                                            <Label for="footer-line-2">
                                                Secondary Line
                                            </Label>
                                            <Input
                                                id="footer-line-2"
                                                placeholder="Need support? Contact your admin"
                                                bind:value={footerFields.line2}
                                            />
                                        </div>
                                        <div class="space-y-2 md:col-span-2">
                                            <Label for="footer-legal">
                                                Legal / Compliance Note
                                            </Label>
                                            <Input
                                                id="footer-legal"
                                                placeholder="Responses are processed under policy"
                                                bind:value={footerFields.legal}
                                            />
                                        </div>
                                    </div>

                                    <div class="flex items-center justify-between">
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onclick={applyFooterBuilder}
                                        >
                                            Apply Footer Template
                                        </Button>
                                        <Button
                                            type="button"
                                            size="sm"
                                            variant="ghost"
                                            onclick={() =>
                                                (showAdvancedFooter =
                                                    !showAdvancedFooter)}
                                        >
                                            {showAdvancedFooter
                                                ? "Hide Advanced HTML"
                                                : "Edit Advanced HTML"}
                                        </Button>
                                    </div>

                                    <div class="campaign-branding-preview">
                                        {#if settings.footerHtml && settings.footerHtml.trim() !== ""}
                                            {@html settings.footerHtml}
                                        {:else}
                                            <p class="text-sm text-muted-foreground">
                                                Footer preview will appear here after applying a template.
                                            </p>
                                        {/if}
                                    </div>

                                    {#if showAdvancedFooter}
                                        <div class="space-y-2">
                                            <Label for="s-footer-html">
                                                Advanced Footer HTML
                                            </Label>
                                            <Textarea
                                                id="s-footer-html"
                                                rows={8}
                                                class="font-mono text-xs"
                                                placeholder="<footer>...</footer>"
                                                bind:value={settings.footerHtml}
                                            />
                                        </div>
                                    {/if}
                                {/if}
                            </div>
                        </Card.Content>
                    </Card.Root>
                </div>

                <div
                    class="flex items-center justify-end gap-3 pt-6 border-t border-border"
                >
                    {#if settingsSaved}
                        <span
                            class="flex items-center gap-1 text-sm text-emerald-500 font-medium"
                        >
                            <Check class="h-4 w-4" />
                            Saved successfully
                        </span>
                    {/if}
                    <Button
                        type="submit"
                        disabled={settingsLoading}
                        class="w-[140px]"
                    >
                        {#if settingsLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        Save Settings
                    </Button>
                </div>
            </form>
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

<style>
    .no-scrollbar::-webkit-scrollbar {
        display: none;
    }
    .no-scrollbar {
        -ms-overflow-style: none;
        scrollbar-width: none;
    }

    .campaign-branding-preview {
        border: 1px solid hsl(var(--border));
        border-radius: 0.75rem;
        padding: 1rem;
        background: linear-gradient(180deg, hsl(var(--muted) / 0.3), hsl(var(--background)));
    }

    .campaign-branding-preview :global(h1),
    .campaign-branding-preview :global(h2),
    .campaign-branding-preview :global(h3),
    .campaign-branding-preview :global(h4),
    .campaign-branding-preview :global(h5),
    .campaign-branding-preview :global(h6) {
        margin: 0;
        color: hsl(var(--foreground));
        letter-spacing: -0.01em;
    }

    .campaign-branding-preview :global(p) {
        margin: 0.45rem 0 0;
        color: hsl(var(--muted-foreground));
        line-height: 1.55;
    }
</style>
