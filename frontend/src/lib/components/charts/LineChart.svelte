<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{date, count}], color }]
        xKey = 'count',
        yKey = 'date',
        color = '#3b82f6' 
    } = $props<{
        data?: any[];
        datasets?: any[];
        xKey?: string;
        yKey?: string;
        color?: string;
    }>();
    
    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'line'> | null = null;

    function buildDatasets() {
        if (datasets && datasets.length > 0) {
            return datasets.map((ds: any) => ({
                label: ds.label,
                data: ds.data.map((d: any) => ({
                    x: d[yKey],
                    y: Number(d[xKey]) || 0,
                    percentage: d.percentage
                })),
                borderColor: ds.color || color,
                backgroundColor: ds.color || color,
                tension: 0.3,
                fill: false,
            }));
        } else {
            return [{
                label: 'Count',
                data: data.map((d: any) => ({
                    x: d[yKey],
                    y: Number(d[xKey]) || 0,
                    percentage: d.percentage
                })),
                borderColor: color,
                backgroundColor: color,
                tension: 0.3,
                fill: true,
            }];
        }
    }

    function buildLabels() {
        // Find longest dataset to extract labels if using datasets mapped, else use single data prop
        // Labels are now derived from the yKey (which is typically date for x-axis)
        const primaryData = datasets && datasets.length > 0 ? datasets[0].data : data;
        return primaryData.map((d: any) => new Date(d[yKey]).toLocaleDateString());
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
            type: 'line',
            data: {
                labels: buildLabels(),
                datasets: buildDatasets()
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: datasets && datasets.length > 0
                    },
                    tooltip: {
                        intersect: false,
                        mode: 'index',
                        callbacks: {
                            label: (context: any) => {
                                let label = context.dataset.label || '';
                                if (label) label += ': ';
                                if (context.parsed.y !== null) {
                                    label += context.parsed.y;
                                    const raw = context.raw as { y: number, percentage?: number };
                                    if (raw && raw.percentage !== undefined && raw.percentage !== null) {
                                        label += ` (${Number(raw.percentage).toFixed(1)}%)`;
                                    }
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
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
