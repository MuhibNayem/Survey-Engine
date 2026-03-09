<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Separator } from "$lib/components/ui/separator";
    import { Switch } from "$lib/components/ui/switch";
    import { Label } from "$lib/components/ui/label";
    import { Confetti } from "$lib/components/confetti";
    import {
        CreditCard,
        ShieldCheck,
        User,
        ArrowRight,
        Moon,
        Sun,
    } from "lucide-svelte";

    // Theme toggle state
    let isDarkMode = $state(
        typeof document !== "undefined"
            ? document.documentElement.classList.contains("dark")
            : false,
    );

    // Confetti celebration for profile completion
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');

    function toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            document.documentElement.classList.add("dark");
        } else {
            document.documentElement.classList.remove("dark");
        }
    }

    onMount(() => {
        // 🎉 Celebrate if profile is complete (has email and tenant)
        if (auth.user?.email && auth.user?.tenantId && auth.user?.role) {
            showConfetti = true;
            confettiTitle = '✅ Profile Complete!';
            confettiMessage = 'Your account is fully set up and ready to go.';
            setTimeout(() => (showConfetti = false), 4000);
        }
    });
</script>

<svelte:head>
    <title>Settings — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-4xl mx-auto">
    <div>
        <h1 class="text-3xl font-bold tracking-tight text-foreground">
            Settings
        </h1>
        <p class="mt-1 text-muted-foreground">
            Manage your tenant configuration, subscription, and account physics.
        </p>
    </div>

    <div class="grid gap-6 md:grid-cols-2">
        <!-- Navigation Cards -->
        <div class="space-y-4">
            <Card.Root
                class="hover:border-primary/50 transition-colors cursor-pointer"
                onclick={() => goto("/settings/subscription")}
            >
                <Card.Header>
                    <div class="flex items-center gap-3 mb-2">
                        <div
                            class="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10"
                        >
                            <CreditCard class="h-5 w-5 text-primary" />
                        </div>
                        <div>
                            <Card.Title>Subscription & Billing</Card.Title>
                        </div>
                    </div>
                    <Card.Description
                        >View your current plan, check usage against quotas, and
                        upgrade your tier.</Card.Description
                    >
                </Card.Header>
                <Card.Content class="flex justify-end pt-0 pb-4">
                    <Button variant="ghost" size="sm" class="gap-1">
                        Manage Billing <ArrowRight class="h-4 w-4" />
                    </Button>
                </Card.Content>
            </Card.Root>

            <Card.Root
                class="hover:border-indigo-500/50 transition-colors cursor-pointer"
                onclick={() => goto("/settings/auth")}
            >
                <Card.Header>
                    <div class="flex items-center gap-3 mb-2">
                        <div
                            class="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-500/10"
                        >
                            <ShieldCheck class="h-5 w-5 text-indigo-500" />
                        </div>
                        <div>
                            <Card.Title>Authentication Profiles</Card.Title>
                        </div>
                    </div>
                    <Card.Description
                        >Configure external Identity Providers (OIDC) to secure
                        Private campaigns via SSO.</Card.Description
                    >
                </Card.Header>
                <Card.Content class="flex justify-end pt-0 pb-4">
                    <Button
                        variant="ghost"
                        size="sm"
                        class="gap-1 text-indigo-600 dark:text-indigo-400"
                    >
                        Configure Auth <ArrowRight class="h-4 w-4" />
                    </Button>
                </Card.Content>
            </Card.Root>
        </div>

        <!-- User / Tenant Preferences -->
        <div class="space-y-4">
            <Card.Root>
                <Card.Header>
                    <div class="flex items-center gap-3">
                        <div
                            class="flex h-10 w-10 items-center justify-center rounded-xl bg-muted"
                        >
                            <User class="h-5 w-5 text-muted-foreground" />
                        </div>
                        <div>
                            <Card.Title>Account Information</Card.Title>
                            <Card.Description
                                >Your identity within the engine.</Card.Description
                            >
                        </div>
                    </div>
                </Card.Header>
                <Card.Content>
                    <div class="space-y-4">
                        <div class="space-y-1">
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Email
                            </p>
                            <p
                                class="text-base text-foreground bg-muted/30 p-2 rounded border border-border/50"
                            >
                                {auth.user?.email ?? "—"}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Tenant ID
                            </p>
                            <p
                                class="font-mono text-sm text-muted-foreground bg-muted/30 p-2 rounded border border-border/50 break-all"
                            >
                                {auth.user?.tenantId ?? "—"}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Role
                            </p>
                            <p
                                class="text-sm text-foreground inline-block bg-primary/10 text-primary px-2 py-0.5 rounded font-bold tracking-wide"
                            >
                                {auth.user?.role ?? "—"}
                            </p>
                        </div>
                    </div>
                    <Separator class="my-6" />
                    <div class="flex items-center justify-between">
                        <div class="space-y-0.5">
                            <Label class="text-base">Dark Mode</Label>
                            <p class="text-sm text-muted-foreground">
                                Switch UI color scheme.
                            </p>
                        </div>
                        <div class="flex items-center gap-2">
                            <Sun class="h-4 w-4 text-muted-foreground" />
                            <Switch
                                checked={isDarkMode}
                                onCheckedChange={toggleTheme}
                            />
                            <Moon class="h-4 w-4 text-muted-foreground" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>
        </div>
    </div>
</div>

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
        colors={['#10b981', '#3b82f6', '#8b5cf6']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
