<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{value, label}], color }]
        xKey = 'value', 
        yKey = 'label', 
        color = '#3b82f6',
        stacked = false
    } = $props<{
        data?: any[];
        datasets?: any[];
        xKey?: string;
        yKey?: string;
        color?: string;
        stacked?: boolean;
    }>();

    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'bar'> | null = null;

    function buildLabels() {
        if (datasets && datasets.length > 0) {
            const allLabels = new Set<string>();
            datasets.forEach((ds: any) => {
                ds.data.forEach((d: any) => allLabels.add(d[yKey]));
            });
            return Array.from(allLabels);
        } else {
            return data.map((d: any) => d[yKey]);
        }
    }

    function buildDatasets() {
        if (datasets && datasets.length > 0) {
            const labels = buildLabels();
            return datasets.map((ds: any) => {
                // Map the data to align with the global labels array
                const alignedData = labels.map((label: string) => {
                    const found = ds.data.find((d: any) => d[yKey] === label);
                    return found ? (Number(found[xKey]) || 0) : 0;
                });
                
                return {
                    label: ds.label,
                    data: alignedData,
                    backgroundColor: ds.color || color,
                    borderRadius: 4,
                };
            });
        } else {
            return [{
                label: 'Count',
                data: data.map((d: any) => Number(d[xKey]) || 0),
                backgroundColor: color,
                borderRadius: 4,
            }];
        }
    }

    $effect(() => {
        if (chartInstance && (data.length > 0 || datasets.length > 0)) {
            chartInstance.data.labels = buildLabels();
            chartInstance.data.datasets = buildDatasets();
            chartInstance.update();
        }
    });

    onMount(() => {
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        chartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: buildLabels(),
                datasets: buildDatasets()
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y', // Horizontal
                plugins: {
                    legend: { display: datasets && datasets.length > 0 },
                    tooltip: {
                        callbacks: {
                            label: (context: any) => {
                                let label = context.dataset.label || '';
                                if (label) label += ': ';
                                if (context.parsed.x !== null) {
                                    label += context.parsed.x;
                                    const idx = context.dataIndex;
                                    const targetDatasets = datasets && datasets.length > 0 ? datasets : [{ data }];
                                    const dsData = targetDatasets[context.datasetIndex]?.data || [];
                                    
                                    // Lookup the original item by iterating until we find the matching label since it's aligned now
                                    const labelName = context.label;
                                    const item = dsData.find((d: any) => d[yKey] === labelName);
                                    
                                    if (item && item.percentage !== undefined && item.percentage !== null) {
                                        label += ` (${Number(item.percentage).toFixed(1)}%)`;
                                    }
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    x: { 
                        beginAtZero: true,
                        stacked: stacked
                    },
                    y: {
                        stacked: stacked
                    }
                }
            }
        });
    });

    onDestroy(() => {
        if (chartInstance) {
            chartInstance.destroy();
        }
    });
</script>

<div class="w-full h-full relative">
    <canvas bind:this={canvas}></canvas>
</div>
