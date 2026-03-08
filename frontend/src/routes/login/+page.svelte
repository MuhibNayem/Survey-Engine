<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import * as Card from "$lib/components/ui/card";
    import { LogIn, ArrowLeft } from "lucide-svelte";

    let email = $state("");
    let password = $state("");

    async function handleLogin(e: Event) {
        e.preventDefault();
        const success = await auth.login({ email, password });
        if (success) {
            if (auth.user?.role === "SUPER_ADMIN") {
                goto("/admin/dashboard");
            } else {
                goto("/dashboard");
            }
        }
    }
</script>

<svelte:head>
    <title>Login — Survey Engine</title>
</svelte:head>

<div
    class="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-background to-muted/30 p-4 relative"
>
    <!-- Back Button -->
    <div class="absolute left-4 top-4 md:left-8 md:top-8">
        <Button
            variant="ghost"
            size="sm"
            href="/"
            class="text-muted-foreground hover:text-foreground"
        >
            <ArrowLeft class="mr-2 h-4 w-4" />
            Back to Home
        </Button>
    </div>

    <div class="w-full max-w-md">
        <!-- Brand -->
        <div class="mb-8 text-center">
            <div
                class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground text-xl font-bold shadow-lg"
            >
                SE
            </div>
            <h1 class="text-2xl font-bold text-foreground">Welcome back</h1>
            <p class="mt-1 text-sm text-muted-foreground">
                Sign in to your Survey Engine account
            </p>
        </div>

        <Card.Root class="border-border/50 shadow-xl backdrop-blur">
            <Card.Content class="pt-6">
                <form onsubmit={handleLogin} class="space-y-4">
                    {#if auth.error}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {auth.error}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="email">Email</Label>
                        <Input
                            id="email"
                            type="email"
                            placeholder="admin@example.com"
                            bind:value={email}
                            required
                            autocomplete="email"
                        />
                    </div>

                    <div class="space-y-2">
                        <Label for="password">Password</Label>
                        <Input
                            id="password"
                            type="password"
                            placeholder="••••••••"
                            bind:value={password}
                            required
                            autocomplete="current-password"
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
                            Signing in...
                        {:else}
                            <LogIn class="mr-2 h-4 w-4" />
                            Sign in
                        {/if}
                    </Button>
                </form>
            </Card.Content>
            <Card.Footer class="justify-center">
                <p class="text-sm text-muted-foreground">
                    Don't have an account?
                    <a
                        href="/register"
                        class="font-medium text-primary hover:underline"
                        >Create one</a
                    >
                </p>
            </Card.Footer>
        </Card.Root>
    </div>
</div>
