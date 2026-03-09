<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{value, label}], color }]
        xKey = 'value', 
        yKey = 'label', 
        colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899', '#f97316'] 
    } = $props();
    
    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'pie'> | null = null;

    function buildDatasets() {
        const primaryData = datasets && datasets.length > 0 ? datasets[0].data : data;
        return [{
            data: primaryData.map((d: any) => Number(d[xKey]) || 0),
            backgroundColor: colors.slice(0, primaryData.length),
            borderWidth: 1,
            borderColor: '#ffffff'
        }];
    }

    function buildLabels() {
        const primaryData = datasets && datasets.length > 0 ? datasets[0].data : data;
        return primaryData.map((d: any) => d[yKey]);
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
            type: 'pie',
            data: {
                labels: buildLabels(),
                datasets: buildDatasets()
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: { boxWidth: 12 }
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                let label = context.label || '';
                                if (label) label += ': ';
                                if (context.parsed !== null) {
                                    label += context.parsed;
                                    const idx = context.dataIndex;
                                    const activeData = datasets && datasets.length > 0 ? datasets[0].data : data;
                                    if (activeData[idx] && activeData[idx].percentage !== undefined) {
                                        label += ` (${Number(activeData[idx].percentage).toFixed(1)}%)`;
                                    }
                                }
                                return label;
                            }
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
