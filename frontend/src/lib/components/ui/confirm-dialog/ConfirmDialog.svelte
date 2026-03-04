<script lang="ts">
    import { Button } from "$lib/components/ui/button";
    import * as Card from "$lib/components/ui/card";
    import { AlertTriangle } from "lucide-svelte";

    interface Props {
        open: boolean;
        title?: string;
        description?: string;
        confirmLabel?: string;
        variant?: "danger" | "warning";
        loading?: boolean;
        onConfirm: () => void;
        onCancel: () => void;
    }

    let {
        open = $bindable(false),
        title = "Are you sure?",
        description = "This action cannot be undone.",
        confirmLabel = "Delete",
        variant = "danger",
        loading = false,
        onConfirm,
        onCancel,
    }: Props = $props();
</script>

{#if open}
    <!-- Backdrop -->
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={onCancel}
            aria-label="Close dialog"
        ></button>

        <!-- Dialog -->
        <Card.Root
            class="relative z-10 w-full max-w-md mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <div class="flex items-center gap-3">
                    <div
                        class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full {variant ===
                        'danger'
                            ? 'bg-destructive/10'
                            : 'bg-yellow-500/10'}"
                    >
                        <AlertTriangle
                            class="h-5 w-5 {variant === 'danger'
                                ? 'text-destructive'
                                : 'text-yellow-500'}"
                        />
                    </div>
                    <div>
                        <Card.Title>{title}</Card.Title>
                        <Card.Description>{description}</Card.Description>
                    </div>
                </div>
            </Card.Header>
            <Card.Footer class="flex justify-end gap-2">
                <Button variant="outline" onclick={onCancel} disabled={loading}
                    >Cancel</Button
                >
                <Button
                    variant="destructive"
                    onclick={onConfirm}
                    disabled={loading}
                >
                    {#if loading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    {confirmLabel}
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}
