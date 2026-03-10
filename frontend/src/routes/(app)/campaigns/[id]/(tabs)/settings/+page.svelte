<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Switch } from "$lib/components/ui/switch";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { toast } from "svelte-sonner";
    import {
        CalendarDays,
        X,
        Plus,
        Trash2,
        GripVertical,
        Info,
        Check
    } from "lucide-svelte";
    import type {
        CampaignSettingsRequest,
        CampaignSettingsResponse,
        SurveyThemeConfig,
    } from "$lib/types";

    const campaignId = $derived(page.params.id);

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
    let initialLoading = $state(true);

    function cloneTheme<T>(value: T): T {
        return JSON.parse(JSON.stringify(value));
    }

    // Default theme structure ensuring sub-objects exist
    function createDefaultTheme(seed?: Partial<SurveyThemeConfig>): SurveyThemeConfig {
        return {
            templateKey: "aurora-premium",
            paletteKey: "ocean-aurora",
            palette: {
                background: "#f8fbfb",
                shell: "#ffffff",
                panel: "#e8f6f4",
                card: "#ffffff",
                border: "#9fd6cf",
                textPrimary: "#102a43",
                textSecondary: "#4e676c",
                primary: "#0f766e",
                primaryText: "#f8fffe",
                accent: "#14b8a6",
                accentSoft: "#d8f5f1",
                headerBackground: "#102a43",
                headerText: "#f8fffe",
                footerBackground: "#edf9f7",
                footerText: "#35545a",
            },
            branding: {
                brandLabel: "Confidential Evaluation Ledger",
                logoUrl: "",
                logoPosition: "left",
                fontFamily: "\"Iowan Old Style\", \"Palatino Linotype\", \"Book Antiqua\", Georgia, serif",
            },
            layout: {
                contentWidth: "standard",
                headerStyle: "hero",
                headerAlignment: "left",
                footerStyle: "support",
                footerAlignment: "left",
                sectionStyle: "panel",
                questionCardStyle: "soft",
                categorySeparatorStyle: "divider",
            },
            motion: {
                animationPreset: "subtle",
            },
            header: {
                enabled: true,
                eyebrow: "Confidential Evaluation Ledger",
                title: "Survey Experience",
                subtitle: "Share your feedback with clarity and confidence.",
                note: "",
            },
            footer: {
                enabled: true,
                line1: "Thank you for completing this survey.",
                line2: "Need assistance? Contact your survey administrator for support.",
                legal: "Responses are securely processed under your organization's data policy.",
            },
            advanced: {
                useCustomHeaderHtml: false,
                useCustomFooterHtml: false,
                customHeaderHtml: "",
                customFooterHtml: "",
                customCss: "",
            },
            ...seed,
        };
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
        return `${value}T23:59:59Z`;
    }

    function openCloseDatePicker() {
        const input = document.getElementById("s-close") as HTMLInputElement | null;
        input?.showPicker?.();
        input?.focus();
    }

    function buildSettingsPayload() {
        if (!settings.theme) {
            settings.theme = createDefaultTheme();
        }
        
        return {
            ...settings,
            headerHtml: settings.theme.advanced.useCustomHeaderHtml ? settings.theme.advanced.customHeaderHtml : "",
            footerHtml: settings.theme.advanced.useCustomFooterHtml ? settings.theme.advanced.customFooterHtml : "",
            closeDate: toIsoCloseDateOrNull(settings.closeDate),
            responseQuota: toNumberOrNull(settings.responseQuota),
            sessionTimeoutMinutes: toNumberOrNull(settings.sessionTimeoutMinutes) ?? 30,
        };
    }

    async function loadSettings() {
        initialLoading = true;
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
                theme: data.theme
                    ? cloneTheme(data.theme)
                    : createDefaultTheme({
                          header: {
                              enabled: true,
                              eyebrow: "Confidential Evaluation Ledger",
                              title: data.campaignId ?? "Survey Experience",
                              subtitle: data.startMessage ?? "Share your feedback with clarity and confidence.",
                              note: "",
                          },
                          footer: {
                              enabled: true,
                              line1: data.finishMessage ?? "Thank you for completing this survey.",
                              line2: "Need assistance? Contact your survey administrator for support.",
                              legal: "Responses are securely processed under your organization's data policy.",
                          },
                          advanced: {
                              useCustomHeaderHtml: false,
                              useCustomFooterHtml: false,
                              customHeaderHtml: data.headerHtml ?? "",
                              customFooterHtml: data.footerHtml ?? "",
                              customCss: "",
                          },
                      }),
                collectName: data.collectName ?? false,
                collectEmail: data.collectEmail ?? false,
                collectPhone: data.collectPhone ?? false,
                collectAddress: data.collectAddress ?? false,
                dataCollectionFields: data.dataCollectionFields ?? [],
            };
        } catch {
            // keep defaults
            toast.error("Failed to load settings");
        } finally {
            initialLoading = false;
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
            toast.success("Settings saved successfully");
            settingsSaved = true;
            setTimeout(() => (settingsSaved = false), 3000);
        } catch {
            toast.error("Failed to save settings");
        } finally {
            settingsLoading = false;
        }
    }

    onMount(loadSettings);
</script>

{#if initialLoading}
    <div class="space-y-6">
        <div class="grid gap-6 md:grid-cols-2">
            <Card.Root>
                <Card.Header>
                    <Skeleton class="h-6 w-[150px] mb-2" />
                    <Skeleton class="h-4 w-[250px]" />
                </Card.Header>
                <Card.Content class="space-y-4">
                    <Skeleton class="h-10 w-full" />
                    <Skeleton class="h-10 w-full" />
                    <div class="space-y-3 pt-2">
                        <Skeleton class="h-6 w-full" />
                        <Skeleton class="h-6 w-full" />
                    </div>
                </Card.Content>
            </Card.Root>
            <Card.Root>
                <Card.Header>
                    <Skeleton class="h-6 w-[150px] mb-2" />
                    <Skeleton class="h-4 w-[250px]" />
                </Card.Header>
                <Card.Content class="space-y-4">
                    <Skeleton class="h-8 w-full" />
                    <Skeleton class="h-8 w-full" />
                    <Skeleton class="h-8 w-full" />
                </Card.Content>
            </Card.Root>
        </div>
    </div>
{:else}
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
{/if}
