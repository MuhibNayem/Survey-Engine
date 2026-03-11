<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import Sidebar from "$lib/components/layout/Sidebar.svelte";
    import Header from "$lib/components/layout/Header.svelte";
    import CommandPalette from "$lib/components/CommandPalette.svelte";
    import { OnboardingOrchestrator } from "$lib/components/onboarding";
    import { themePreferences } from "$lib/stores/theme.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";

    let { children } = $props();
    let sidebarCollapsed = $state(false);
    let desktopSidebarHidden = $state(false);
    let mobileSidebarOpen = $state(false);
    let commandPaletteOpen = $state(false);

    onMount(() => {
        if (!auth.isAuthenticated) {
            goto("/login");
            return;
        }
        themePreferences.hydrateFromBackend();
    });

    const shouldLoadRuntimeFeatures = $derived(
        auth.user?.role !== "SUPER_ADMIN"
    );
</script>

{#if auth.isAuthenticated}
    <div class="flex h-screen overflow-hidden bg-background">
        <!-- Desktop sidebar -->
        <div
            class="hidden md:flex transition-[margin] duration-300"
            style:margin-left={desktopSidebarHidden
                ? sidebarCollapsed
                    ? "-4rem"
                    : "-16rem"
                : "0"}
        >
            <Sidebar bind:collapsed={sidebarCollapsed} />
        </div>

        <!-- Mobile sidebar overlay -->
        {#if mobileSidebarOpen}
            <div class="fixed inset-0 z-50 md:hidden">
                <button
                    class="absolute inset-0 bg-black/50"
                    onclick={() => (mobileSidebarOpen = false)}
                    aria-label="Close sidebar"
                ></button>
                <div class="relative z-10 h-full w-64">
                    <Sidebar collapsed={false} />
                </div>
            </div>
        {/if}

        <!-- Main content -->
        <div class="flex flex-1 flex-col overflow-hidden">
            <Header
                onToggleMobileSidebar={() =>
                    (mobileSidebarOpen = !mobileSidebarOpen)}
                onToggleDesktopSidebar={() =>
                    (desktopSidebarHidden = !desktopSidebarHidden)}
                desktopSidebarHidden={desktopSidebarHidden}
            />
            <main class="flex-1 overflow-y-auto p-4 md:p-6 lg:p-8">
                {@render children()}
            </main>
        </div>

        <!-- Global Command Palette -->
        <CommandPalette bind:open={commandPaletteOpen} />
        {#if shouldLoadRuntimeFeatures}
            <OnboardingOrchestrator />
        {/if}
    </div>
{/if}
