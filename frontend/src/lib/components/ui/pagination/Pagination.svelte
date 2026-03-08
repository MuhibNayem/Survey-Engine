<script lang="ts">
    import { Button } from "$lib/components/ui/button";
    import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-svelte";

    let {
        currentPage = 0,
        totalPages = 0,
        totalElements = 0,
        pageSize = 10,
        onPageChange,
        onSizeChange = () => {}
    }: {
        currentPage: number;
        totalPages: number;
        totalElements: number;
        pageSize: number;
        onPageChange: (newPage: number) => void;
        onSizeChange?: (newSize: number) => void;
    } = $props();

    // 1-indexed for display
    let displayPage = $derived(currentPage + 1);
    let startItem = $derived(totalElements === 0 ? 0 : currentPage * pageSize + 1);
    let endItem = $derived(Math.min((currentPage + 1) * pageSize, totalElements));

    // Compute range of visible page buttons (show max 5 pages)
    let pageRange = $derived(() => {
        const range = [];
        let start = Math.max(0, currentPage - 2);
        let end = Math.min(totalPages - 1, start + 4);
        
        // Adjust start if end is at the limit
        if (end === totalPages - 1) {
            start = Math.max(0, end - 4);
        }

        for (let i = start; i <= end; i++) {
            range.push(i);
        }
        return range;
    });
</script>

{#if totalPages > 0}
<div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between px-2 py-4">
    <!-- Items Summary -->
    <div class="flex items-center gap-4">
        <span class="text-sm text-muted-foreground">
            Showing <strong>{startItem}</strong> to <strong>{endItem}</strong> of <strong>{totalElements}</strong> items
        </span>
        <!-- Page Size Selector -->
        <div class="flex items-center gap-2">
            <label for="page-size-select" class="text-sm text-muted-foreground whitespace-nowrap">Rows:</label>
            <select
                id="page-size-select"
                class="h-8 rounded-md border border-input bg-background px-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                value={pageSize}
                onchange={(e) => {
                    const target = e.target as HTMLSelectElement;
                    onSizeChange(Number(target.value));
                }}
            >
                {#each [10, 25, 50] as size}
                    <option value={size} selected={size === pageSize}>{size}</option>
                {/each}
            </select>
        </div>
    </div>    <!-- Page Controls -->
    <div class="flex items-center space-x-2">
        <Button
            variant="outline"
            class="hidden h-8 w-8 p-0 lg:flex"
            disabled={currentPage === 0}
            onclick={() => onPageChange(0)}
            title="First Page"
        >
            <ChevronsLeft class="h-4 w-4" />
        </Button>
        <Button
            variant="outline"
            class="h-8 w-8 p-0"
            disabled={currentPage === 0}
            onclick={() => onPageChange(currentPage - 1)}
            title="Previous Page"
        >
            <ChevronLeft class="h-4 w-4" />
        </Button>

        {#each pageRange() as p}
            <Button
                variant={p === currentPage ? "default" : "outline"}
                class="hidden h-8 w-8 p-0 sm:flex"
                onclick={() => onPageChange(p)}
            >
                {p + 1}
            </Button>
        {/each}

        <Button
            variant="outline"
            class="h-8 w-8 p-0"
            disabled={currentPage >= totalPages - 1}
            onclick={() => onPageChange(currentPage + 1)}
            title="Next Page"
        >
            <ChevronRight class="h-4 w-4" />
        </Button>
        <Button
            variant="outline"
            class="hidden h-8 w-8 p-0 lg:flex"
            disabled={currentPage >= totalPages - 1}
            onclick={() => onPageChange(totalPages - 1)}
            title="Last Page"
        >
            <ChevronsRight class="h-4 w-4" />
        </Button>
    </div>
</div>
{/if}
