<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import * as Card from "$lib/components/ui/card";
    import { UserPlus } from "lucide-svelte";

    let fullName = $state("");
    let email = $state("");
    let password = $state("");
    let confirmPassword = $state("");
    let localError = $state<string | null>(null);

    async function handleRegister(e: Event) {
        e.preventDefault();
        localError = null;

        if (password !== confirmPassword) {
            localError = "Passwords do not match";
            return;
        }
        if (password.length < 8) {
            localError = "Password must be at least 8 characters";
            return;
        }
        if (!fullName.trim()) {
            localError = "Full name is required";
            return;
        }

        const success = await auth.register({
            fullName: fullName.trim(),
            email: email.trim(),
            password,
        });
        if (success) {
            goto("/onboarding/plan");
        }
    }

    const displayError = $derived(localError || auth.error);
</script>

<svelte:head>
    <title>Register — Survey Engine</title>
</svelte:head>

<div
    class="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-background to-muted/30 p-4"
>
    <div class="w-full max-w-md">
        <!-- Brand -->
        <div class="mb-8 text-center">
            <div
                class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground text-xl font-bold shadow-lg"
            >
                SE
            </div>
            <h1 class="text-2xl font-bold text-foreground">
                Create your account
            </h1>
            <p class="mt-1 text-sm text-muted-foreground">
                Start building surveys in minutes
            </p>
        </div>

        <Card.Root class="border-border/50 shadow-xl backdrop-blur">
            <Card.Content class="pt-6">
                <form onsubmit={handleRegister} class="space-y-4">
                    {#if displayError}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {displayError}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="reg-fullname">Full Name</Label>
                        <Input
                            id="reg-fullname"
                            type="text"
                            placeholder="Jane Doe"
                            bind:value={fullName}
                            required
                            autocomplete="name"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-email">Email</Label>
                        <Input
                            id="reg-email"
                            type="email"
                            placeholder="you@company.com"
                            bind:value={email}
                            required
                            autocomplete="email"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-password">Password</Label>
                        <Input
                            id="reg-password"
                            type="password"
                            placeholder="••••••••"
                            bind:value={password}
                            required
                            autocomplete="new-password"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-confirm">Confirm Password</Label>
                        <Input
                            id="reg-confirm"
                            type="password"
                            placeholder="••••••••"
                            bind:value={confirmPassword}
                            required
                            autocomplete="new-password"
                        />
                    </div>

                    <Button
                        type="submit"
                        class="w-full"
                        disabled={auth.isLoading}
                    >
                        {#if auth.isLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                            Creating account...
                        {:else}
                            <UserPlus class="mr-2 h-4 w-4" />
                            Create account
                        {/if}
                    </Button>
                </form>
            </Card.Content>
            <Card.Footer class="justify-center">
                <p class="text-sm text-muted-foreground">
                    Already have an account?
                    <a
                        href="/login"
                        class="font-medium text-primary hover:underline"
                        >Sign in</a
                    >
                </p>
            </Card.Footer>
        </Card.Root>
    </div>
</div>
