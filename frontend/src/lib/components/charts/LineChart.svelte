<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{date, count}], color }]
        color = '#3b82f6' 
    } = $props();
    
    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'line'> | null = null;

    function buildDatasets() {
        if (datasets && datasets.length > 0) {
            return datasets.map((ds: any) => ({
                label: ds.label,
                data: ds.data.map((d: any) => Number(d.count) || 0),
                borderColor: ds.color || color,
                backgroundColor: (ds.color || color) + '33',
                borderWidth: 2,
                pointBackgroundColor: '#ffffff',
                pointBorderColor: ds.color || color,
                pointRadius: 4,
                pointHoverRadius: 6,
                fill: true,
                tension: 0.4
            }));
        } else {
            return [{
                label: 'Responses',
                data: data.map((d: any) => Number(d.count) || 0),
                borderColor: color,
                backgroundColor: color + '33', // 20% opacity hex
                borderWidth: 2,
                pointBackgroundColor: '#ffffff',
                pointBorderColor: color,
                pointRadius: 4,
                pointHoverRadius: 6,
                fill: true,
                tension: 0.4
            }];
        }
    }

    function buildLabels() {
        // Find longest dataset to extract labels if using datasets mapped, else use single data prop
        const primaryData = datasets && datasets.length > 0 ? datasets[0].data : data;
        return primaryData.map((d: any) => new Date(d.date).toLocaleDateString());
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
