<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import * as Select from "$lib/components/ui/select";
    import { Switch } from "$lib/components/ui/switch";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        ArrowLeft,
        ShieldCheck,
        Key,
        Plus,
        Trash2,
        AlertCircle,
        CheckCircle2,
        Settings2,
        RefreshCw,
    } from "lucide-svelte";
    import type {
        AuthProfileResponse,
        AuthProfileRequest,
        ProviderTemplateResponse,
        ClaimMappingRequest,
        AuthenticationMode,
        FallbackPolicy,
    } from "$lib/types";
    import { toast } from "svelte-sonner";
    import { auth } from "$lib/stores/auth.svelte";
    import { Confetti } from "$lib/components/confetti";

    let profile = $state<AuthProfileResponse | null>(null);
    let templates = $state<ProviderTemplateResponse[]>([]);
    let loading = $state(true);
    let error = $state<string | null>(null);

    // Confetti celebration
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');

    // Form state
    let isEditing = $state(false);
    let formLoading = $state(false);

    let authMode = $state<AuthenticationMode>("SIGNED_LAUNCH_TOKEN");
    let fallbackPolicy = $state<FallbackPolicy>("SSO_REQUIRED");
    let issuer = $state("");
    let audience = $state("");
    let oidcClientId = $state("");
    let oidcClientSecret = $state("");
    let oidcDiscoveryUrl = $state("");
    let oidcScopes = $state("");
    let claimMappings = $state<ClaimMappingRequest[]>([]);

    // Key Rotation
    let confirmRotate = $state(false);
    let rotating = $state(false);

    async function loadData() {
        loading = true;
        error = null;
        try {
            if (!auth.user?.tenantId) {
                await auth.fetchCurrentUser();
            }
            const tenantId = auth.user?.tenantId;
            if (!tenantId) throw new Error("No tenant ID found in session.");

            const [profRes, tempRes] = await Promise.allSettled([
                api.get<AuthProfileResponse>(
                    `/auth/profiles/tenant/${tenantId}`,
                ),
                api.get<ProviderTemplateResponse[]>(
                    "/auth/providers/templates",
                ),
            ]);

            if (profRes.status === "fulfilled") {
                profile = profRes.value.data;
                populateForm(profile);
            }

            if (tempRes.status === "fulfilled") {
                templates = tempRes.value.data;
            }
        } catch (err: any) {
            if (err?.response?.status !== 404) {
                error = "Failed to load authentication configuration.";
            }
        } finally {
            loading = false;
        }
    }

    function populateForm(p: AuthProfileResponse) {
        authMode = p.authMode;
        fallbackPolicy = p.fallbackPolicy;
        issuer = p.issuer || "";
        audience = p.audience || "";
        oidcClientId = p.oidcClientId || "";
        oidcDiscoveryUrl = p.oidcDiscoveryUrl || "";
        oidcScopes = p.oidcScopes || "";
        claimMappings = p.claimMappings.map((c) => ({
            externalClaim: c.externalClaim,
            internalField: c.internalField,
            required: c.required,
        }));
        // Secret is never returned by API
        oidcClientSecret = "";
    }

    function handleTemplateSelect(val: string) {
        const template = templates.find((t) => t.providerCode === val);
        if (!template) return;

        oidcScopes = template.defaultScopes.join(" ");
        claimMappings = template.defaultClaimMappings.map((c) => ({ ...c }));
        toast.success(`Applied ${template.displayName} template defaults.`);
    }

    function addMapping() {
        claimMappings = [
            ...claimMappings,
            {
                externalClaim: "",
                internalField: "custom_claim",
                required: false,
            },
        ];
    }

    function removeMapping(index: number) {
        claimMappings = claimMappings.filter((_, i) => i !== index);
    }

    async function handleSave(e: Event) {
        e.preventDefault();
        formLoading = true;
        error = null;
        const tenantId = auth.user?.tenantId;
        if (!tenantId) {
            error = "No tenant ID found in session.";
            formLoading = false;
            return;
        }

        const payload: AuthProfileRequest = {
            tenantId,
            authMode,
            fallbackPolicy,
            issuer: issuer || undefined,
            audience: audience || undefined,
            oidcClientId: oidcClientId || undefined,
            oidcClientSecret: oidcClientSecret || undefined,
            oidcDiscoveryUrl: oidcDiscoveryUrl || undefined,
            oidcScopes: oidcScopes || undefined,
            claimMappings,
        };

        try {
            let res;
            if (profile) {
                res = await api.put<AuthProfileResponse>(
                    `/auth/profiles/${profile.id}`,
                    payload,
                );
            } else {
                res = await api.post<AuthProfileResponse>(
                    "/auth/profiles",
                    payload,
                );
            }
            profile = res.data;
            // 🎉 Celebrate auth profile configuration
            showConfetti = true;
            confettiTitle = '🔐 Auth Configured!';
            confettiMessage = 'Your authentication profile has been successfully configured.';
            setTimeout(() => (showConfetti = false), 4000);
            toast.success("Authentication profile saved successfully.");
            isEditing = false;
        } catch (err: any) {
            error = err?.response?.data?.message || "Failed to save profile.";
            toast.error(error ?? "Failed to save profile.");
        } finally {
            formLoading = false;
        }
    }

    async function handleRotateKey() {
        if (!profile) return;
        rotating = true;
        try {
            await api.post(`/auth/profiles/${profile.id}/rotate-key`);
            toast.success("Signing key rotated successfully.");
            await loadData();
        } catch (err: any) {
            toast.error(
                err?.response?.data?.message || "Failed to rotate key.",
            );
        } finally {
            rotating = false;
            confirmRotate = false;
        }
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Auth Settings — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-5xl mx-auto pb-12">
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
            <Button variant="ghost" size="sm" onclick={() => goto("/settings")}>
                <ArrowLeft class="h-4 w-4" />
            </Button>
            <div>
                <h1 class="text-2xl font-bold tracking-tight text-foreground">
                    Authentication Settings
                </h1>
                <p
                    class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
                >
                    <ShieldCheck class="h-3.5 w-3.5" /> Secure your private campaigns
                    via Single Sign-On (OIDC)
                </p>
            </div>
        </div>
        {#if profile && !isEditing}
            <Button onclick={() => (isEditing = true)}>
                <Settings2 class="h-4 w-4 mr-2" />
                Edit Configuration
            </Button>
        {/if}
    </div>

    {#if error && !isEditing}
        <div
            class="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive"
        >
            <AlertCircle class="h-4 w-4 shrink-0 mt-0.5" />
            <p>{error}</p>
        </div>
    {/if}

    {#if loading}
        <Card.Root class="border-indigo-500/30 shadow-md">
            <Card.Header class="border-b bg-indigo-500/5 pb-4">
                <div class="space-y-2">
                    <Skeleton class="h-6 w-[250px]" />
                    <Skeleton class="h-4 w-[400px]" />
                </div>
            </Card.Header>
            <Card.Content class="pt-6">
                <div class="space-y-8">
                    {#each Array(5) as _}
                        <div class="space-y-2">
                            <Skeleton class="h-4 w-[120px]" />
                            <Skeleton class="h-10 w-full" />
                        </div>
                    {/each}
                </div>
            </Card.Content>
        </Card.Root>
    {:else if !profile || isEditing}
        <Card.Root class="border-indigo-500/30 shadow-md">
            <Card.Header class="border-b bg-indigo-500/5 pb-4">
                <Card.Title>Configure OpenID Connect Integration</Card.Title>
                <Card.Description>
                    Connect your identity provider to map external users to
                    survey respondents.
                </Card.Description>
            </Card.Header>
            <Card.Content class="pt-6">
                <form id="authForm" onsubmit={handleSave} class="space-y-8">
                    <!-- Step 1: Mode & Templates -->
                    <div class="grid gap-6 md:grid-cols-2">
                        <div class="space-y-4">
                            <h3 class="text-sm font-semibold border-b pb-2">
                                Behavior Flags
                            </h3>

                            <div class="space-y-1.5">
                                <Label>Authentication Mode</Label>
                                <Select.Root
                                    type="single"
                                    bind:value={authMode}
                                    required
                                >
                                    <Select.Trigger class="w-full">
                                        {authMode === "PUBLIC_ANONYMOUS"
                                            ? "Public Anonymous"
                                            : authMode ===
                                                  "EXTERNAL_SSO_TRUST"
                                              ? "External SSO Trust"
                                              : "Signed Launch Token"}
                                    </Select.Trigger>
                                    <Select.Content>
                                        <Select.Item value="SIGNED_LAUNCH_TOKEN"
                                            >Signed Launch Token</Select.Item
                                        >
                                        <Select.Item value="EXTERNAL_SSO_TRUST"
                                            >External SSO Trust</Select.Item
                                        >
                                        <Select.Item value="PUBLIC_ANONYMOUS"
                                            >Public Anonymous</Select.Item
                                        >
                                    </Select.Content>
                                </Select.Root>
                            </div>

                            <div class="space-y-1.5 mt-4">
                                <Label>Fallback Policy</Label>
                                <Select.Root
                                    type="single"
                                    bind:value={fallbackPolicy}
                                    required
                                >
                                    <Select.Trigger class="w-full">
                                        {fallbackPolicy === "SSO_REQUIRED"
                                            ? "SSO Required"
                                            : fallbackPolicy ===
                                                  "ANONYMOUS_FALLBACK"
                                              ? "Anonymous Fallback"
                                              : "Disable On Failure"}
                                    </Select.Trigger>
                                    <Select.Content>
                                        <Select.Item value="SSO_REQUIRED"
                                            >SSO Required</Select.Item
                                        >
                                        <Select.Item
                                            value="ANONYMOUS_FALLBACK"
                                            >Anonymous Fallback</Select.Item
                                        >
                                        <Select.Item value="DISABLE_ON_FAILURE"
                                            >Disable On Failure</Select.Item
                                        >
                                    </Select.Content>
                                </Select.Root>
                            </div>
                        </div>

                        <div
                            class="space-y-4 bg-muted/30 p-4 rounded-lg border border-border/50"
                        >
                            <h3
                                class="text-sm font-semibold flex items-center gap-2"
                            >
                                <Key class="h-4 w-4 text-indigo-500" /> Kickstart
                                from Template
                            </h3>
                            <p class="text-xs text-muted-foreground">
                                Select a provider to auto-fill common scope and
                                claim defaults.
                            </p>
                            <Select.Root
                                type="single"
                                onValueChange={handleTemplateSelect}
                            >
                                <Select.Trigger class="w-full">
                                    Select an IdP Template...
                                </Select.Trigger>
                                <Select.Content>
                                    {#each templates as t}
                                        <Select.Item value={t.providerCode}
                                            >{t.displayName}</Select.Item
                                        >
                                    {/each}
                                </Select.Content>
                            </Select.Root>
                        </div>
                    </div>

                    <!-- Step 2: OIDC Network Config -->
                    <div class="space-y-4 pt-4 border-t">
                        <h3 class="text-sm font-semibold border-b pb-2">
                            OpenID Connect Details
                        </h3>
                        <div class="grid gap-4 md:grid-cols-2">
                            <div class="space-y-1.5">
                                <Label>Client ID *</Label>
                                <Input
                                    bind:value={oidcClientId}
                                    required
                                    placeholder="0oa2..."
                                />
                            </div>
                            <div class="space-y-1.5">
                                <Label
                                    >Client Secret {profile
                                        ? "(Leave blank to keep existing)"
                                        : "*"}</Label
                                >
                                <Input
                                    type="password"
                                    bind:value={oidcClientSecret}
                                    required={!profile}
                                    placeholder="••••••••••••"
                                />
                            </div>
                            <div class="space-y-1.5 md:col-span-2">
                                <Label
                                    >Discovery URL (Issuer path ending in
                                    .well-known/openid-configuration) *</Label
                                >
                                <Input
                                    bind:value={oidcDiscoveryUrl}
                                    required
                                    placeholder="https://your-tenant.auth0.com/.well-known/openid-configuration"
                                />
                            </div>
                            <div class="space-y-1.5">
                                <Label>Issuer URI (Optional Override)</Label>
                                <Input
                                    bind:value={issuer}
                                    placeholder="https://your-tenant.auth0.com/"
                                />
                            </div>
                            <div class="space-y-1.5">
                                <Label>Token Audience (Optional)</Label>
                                <Input
                                    bind:value={audience}
                                    placeholder="api://survey-engine"
                                />
                            </div>
                            <div class="space-y-1.5 md:col-span-2">
                                <Label>OIDC Scopes</Label>
                                <Input
                                    bind:value={oidcScopes}
                                    placeholder="openid profile email"
                                />
                            </div>
                        </div>
                    </div>

                    <!-- Step 3: Claim Mapping -->
                    <div class="space-y-4 pt-4 border-t">
                        <div
                            class="flex items-center justify-between border-b pb-2"
                        >
                            <h3 class="text-sm font-semibold">
                                JWT Claim Mapping
                            </h3>
                            <Button
                                variant="ghost"
                                size="sm"
                                onclick={addMapping}
                            >
                                <Plus class="h-4 w-4 mr-1" /> Add Mapping
                            </Button>
                        </div>

                        {#if claimMappings.length === 0}
                            <div
                                class="text-center py-6 bg-muted/20 border border-dashed rounded font-medium text-sm text-muted-foreground"
                            >
                                No claim mappings configured. The system won't
                                extract identifying information from the IdP.
                            </div>
                        {/if}

                        <div class="space-y-3">
                            {#each claimMappings as mapping, i}
                                <div
                                    class="flex items-end gap-3 bg-muted/10 p-3 rounded border"
                                >
                                    <div class="flex-1 space-y-1.5">
                                        <Label>IdP Token Claim</Label>
                                        <Input
                                            bind:value={mapping.externalClaim}
                                            placeholder="e.g. upn or sub"
                                            required
                                        />
                                    </div>
                                    <div class="flex-1 space-y-1.5">
                                        <Label>Engine Internal Field</Label>
                                        <Select.Root
                                            type="single"
                                            bind:value={mapping.internalField}
                                            required
                                        >
                                            <Select.Trigger class="w-full"
                                                >{mapping.internalField}</Select.Trigger
                                            >
                                            <Select.Content>
                                                <Select.Item value="subject"
                                                    >subject</Select.Item
                                                >
                                                <Select.Item value="email"
                                                    >email</Select.Item
                                                >
                                                <Select.Item value="name"
                                                    >name</Select.Item
                                                >
                                                <Select.Item value="roles"
                                                    >roles</Select.Item
                                                >
                                                <Select.Item value="groups"
                                                    >groups</Select.Item
                                                >
                                            </Select.Content>
                                        </Select.Root>
                                    </div>
                                    <div class="pb-2">
                                        <div
                                            class="flex items-center space-x-2 mr-4"
                                        >
                                            <Switch
                                                id="req-{i}"
                                                bind:checked={mapping.required}
                                            />
                                            <Label for="req-{i}" class="text-xs"
                                                >Required</Label
                                            >
                                        </div>
                                    </div>
                                    <Button
                                        variant="ghost"
                                        size="icon"
                                        class="text-destructive hover:text-destructive hover:bg-destructive/10"
                                        onclick={() => removeMapping(i)}
                                    >
                                        <Trash2 class="h-4 w-4" />
                                    </Button>
                                </div>
                            {/each}
                        </div>
                    </div>
                </form>
            </Card.Content>
            <Card.Footer
                class="flex justify-end gap-3 border-t bg-muted/10 pt-4"
            >
                {#if profile}
                    <Button
                        variant="outline"
                        onclick={() => {
                            isEditing = false;
                            populateForm(profile!);
                        }}>Cancel</Button
                    >
                {/if}
                <Button
                    type="submit"
                    form="authForm"
                    disabled={formLoading}
                    class="bg-indigo-600 hover:bg-indigo-700"
                >
                    {#if formLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    {profile ? "Save Changes" : "Create Profile"}
                </Button>
            </Card.Footer>
        </Card.Root>
    {:else}
        <!-- View Mode -->
        <div class="grid gap-6 md:grid-cols-3">
            <!-- Status Panel -->
            <Card.Root
                class="md:col-span-1 border-indigo-500/20 shadow-sm relative overflow-hidden"
            >
                <div
                    class="absolute top-0 right-0 w-24 h-24 bg-indigo-500/10 rounded-bl-full -z-10"
                ></div>
                <Card.Header>
                    <Card.Title>Active Profile</Card.Title>
                    <Badge variant="default" class="bg-emerald-500 mt-2 w-fit"
                        >Configured</Badge
                    >
                </Card.Header>
                <Card.Content class="space-y-4 pt-2">
                    <div class="space-y-1">
                        <p
                            class="text-xs text-muted-foreground font-semibold uppercase tracking-wider"
                        >
                            Mode
                        </p>
                        <p class="font-medium">{profile.authMode}</p>
                    </div>
                    <div class="space-y-1">
                        <p
                            class="text-xs text-muted-foreground font-semibold uppercase tracking-wider"
                        >
                            Fallback
                        </p>
                        <p class="font-medium">{profile.fallbackPolicy}</p>
                    </div>
                    <div class="space-y-1">
                        <p
                            class="text-xs text-muted-foreground font-semibold uppercase tracking-wider"
                        >
                            Keys
                        </p>
                        <p
                            class="font-medium flex items-center justify-between"
                        >
                            v{profile.activeKeyVersion} active
                            <Button
                                variant="ghost"
                                size="sm"
                                class="h-6 px-2 text-indigo-600 bg-indigo-500/10"
                                onclick={() => (confirmRotate = true)}
                            >
                                <RefreshCw class="h-3 w-3 mr-1" /> Rotate
                            </Button>
                        </p>
                    </div>
                </Card.Content>
            </Card.Root>

            <!-- Details List -->
            <Card.Root class="md:col-span-2">
                <Card.Header class="pb-3 border-b border-border/50">
                    <Card.Title class="text-lg flex items-center gap-2">
                        <Settings2 class="h-5 w-5" /> OpenID Configuration
                    </Card.Title>
                </Card.Header>
                <Card.Content class="pt-4 space-y-4">
                    <div class="grid sm:grid-cols-2 gap-y-4 gap-x-6 text-sm">
                        <div>
                            <span class="text-muted-foreground block mb-1"
                                >Discovery Endpoint</span
                            >
                            <span
                                class="font-mono bg-muted/50 px-2 py-0.5 rounded break-all"
                                >{profile.oidcDiscoveryUrl}</span
                            >
                        </div>
                        <div>
                            <span class="text-muted-foreground block mb-1"
                                >Client ID</span
                            >
                            <span
                                class="font-mono bg-muted/50 px-2 py-0.5 rounded break-all"
                                >{profile.oidcClientId}</span
                            >
                        </div>
                        {#if profile.issuer}
                            <div>
                                <span class="text-muted-foreground block mb-1"
                                    >Issuer Restriction</span
                                >
                                <span
                                    class="font-mono bg-muted/50 px-2 py-0.5 rounded break-all"
                                    >{profile.issuer}</span
                                >
                            </div>
                        {/if}
                        <div>
                            <span class="text-muted-foreground block mb-1"
                                >Scopes Requested</span
                            >
                            <span class="font-medium">{profile.oidcScopes}</span
                            >
                        </div>
                        <div>
                            <span class="text-muted-foreground block mb-1"
                                >Redirect URI (Callback)</span
                            >
                            <span
                                class="font-mono text-xs text-muted-foreground break-all"
                                >{profile.oidcRedirectUri}</span
                            >
                        </div>
                    </div>

                    <div class="pt-4 mt-2 border-t border-border/50">
                        <h4 class="text-sm font-semibold mb-3">
                            Claim Pipeline Mappings
                        </h4>
                        <div class="grid sm:grid-cols-2 gap-3">
                            {#each profile.claimMappings as m}
                                <div
                                    class="flex items-center gap-2 text-sm border p-2 rounded bg-muted/5"
                                >
                                    <span
                                        class="font-mono text-indigo-600 dark:text-indigo-400"
                                        >{m.externalClaim}</span
                                    >
                                    <ArrowLeft
                                        class="h-3 w-3 text-muted-foreground rotate-180"
                                    />
                                    <span class="font-medium"
                                        >{m.internalField}</span
                                    >
                                    {#if m.required}
                                        <Badge
                                            variant="outline"
                                            class="ml-auto text-[10px] h-5 py-0"
                                            >Req</Badge
                                        >
                                    {/if}
                                </div>
                            {/each}
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>
        </div>
    {/if}
</div>

<ConfirmDialog
    open={confirmRotate}
    title="Rotate Signing Keys"
    description="Rotating keys will invalidate all current active sessions initialized by this provider. Users will need to re-authenticate. Proceed with caution."
    confirmLabel="Rotate Keys Now"
    variant="danger"
    loading={rotating}
    onConfirm={handleRotateKey}
    onCancel={() => (confirmRotate = false)}
/>

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
        colors={['#8b5cf6', '#3b82f6', '#10b981']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
