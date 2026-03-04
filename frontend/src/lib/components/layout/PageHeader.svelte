<script lang="ts">
    import { Button } from "$lib/components/ui/button";
    import type { Component } from "svelte";

    interface Props {
        title: string;
        description?: string;
        actionLabel?: string;
        actionIcon?: any;
        onAction?: () => void;
    }

    let {
        title,
        description,
        actionLabel,
        actionIcon: ActionIcon,
        onAction,
    }: Props = $props();
</script>

<div class="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between">
    <div>
        <h1 class="text-3xl font-bold tracking-tight text-foreground">
            {title}
        </h1>
        {#if description}
            <p class="mt-1 text-muted-foreground">{description}</p>
        {/if}
    </div>
    {#if actionLabel && onAction}
        <Button onclick={onAction} class="shrink-0">
            {#if ActionIcon}
                <ActionIcon class="mr-2 h-4 w-4" />
            {/if}
            {actionLabel}
        </Button>
    {/if}
</div>
