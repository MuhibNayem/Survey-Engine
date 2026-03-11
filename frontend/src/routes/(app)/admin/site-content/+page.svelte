<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Textarea } from "$lib/components/ui/textarea";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Switch } from "$lib/components/ui/switch";
    import {
        FilePenLine,
        Globe,
        Megaphone,
        Plus,
        Trash2,
        Eye,
        Upload,
        Save,
    } from "lucide-svelte";
    import {
        defaultHomeSiteContent,
        defaultPricingSiteContent,
        normalizeHomeSiteContent,
        normalizePricingSiteContent,
        type HomeSiteContent,
        type PricingSiteContent,
        type SiteContentPageKey,
    } from "$lib/site-content";

    interface SiteContentPageResponse {
        pageKey: SiteContentPageKey;
        draftContent: unknown;
        publishedContent: unknown;
        published: boolean;
        publishedAt?: string | null;
        updatedAt?: string | null;
    }

    let currentPageKey = $state<SiteContentPageKey>("HOME");
    let loading = $state(true);
    let saving = $state(false);
    let publishing = $state(false);
    let error = $state<string | null>(null);
    let success = $state<string | null>(null);
    let published = $state(false);
    let publishedAt = $state<string | null>(null);
    let homeContent = $state<HomeSiteContent>(defaultHomeSiteContent());
    let pricingContent = $state<PricingSiteContent>(defaultPricingSiteContent());

    function clone<T>(value: T): T {
        return JSON.parse(JSON.stringify(value));
    }

    function currentPath() {
        return currentPageKey === "HOME" ? "/" : "/pricing";
    }

    async function loadPage(pageKey: SiteContentPageKey) {
        loading = true;
        error = null;
        success = null;
        try {
            const { data } = await api.get<SiteContentPageResponse>(`/admin/superadmin/site-content/${pageKey}`);
            currentPageKey = pageKey;
            published = data.published;
            publishedAt = data.publishedAt ?? null;
            if (pageKey === "HOME") {
                homeContent = normalizeHomeSiteContent(data.draftContent);
            } else {
                pricingContent = normalizePricingSiteContent(data.draftContent);
            }
        } catch (err: any) {
            error = err?.response?.data?.message || "Failed to load site content.";
        } finally {
            loading = false;
        }
    }

    async function saveDraft() {
        saving = true;
        error = null;
        success = null;
        try {
            await api.put(`/admin/superadmin/site-content/${currentPageKey}`, {
                content: currentPageKey === "HOME" ? homeContent : pricingContent,
            });
            success = "Draft saved.";
        } catch (err: any) {
            error = err?.response?.data?.message || "Failed to save draft.";
        } finally {
            saving = false;
        }
    }

    async function publishPage() {
        publishing = true;
        error = null;
        success = null;
        try {
            await api.post(`/admin/superadmin/site-content/${currentPageKey}/publish`);
            published = true;
            publishedAt = new Date().toISOString();
            success = "Page published.";
        } catch (err: any) {
            error = err?.response?.data?.message || "Failed to publish page.";
        } finally {
            publishing = false;
        }
    }

    async function unpublishPage() {
        publishing = true;
        error = null;
        success = null;
        try {
            await api.post(`/admin/superadmin/site-content/${currentPageKey}/unpublish`);
            published = false;
            success = "Page unpublished.";
        } catch (err: any) {
            error = err?.response?.data?.message || "Failed to unpublish page.";
        } finally {
            publishing = false;
        }
    }

    function previewPage() {
        window.open(currentPath(), "_blank", "noopener,noreferrer");
    }

    function addHomeLogo() {
        homeContent.logoStrip.logos = [...homeContent.logoStrip.logos, { label: "" }];
    }

    function removeHomeLogo(index: number) {
        homeContent.logoStrip.logos = homeContent.logoStrip.logos.filter((_, i) => i !== index);
    }

    function addHomeFeature() {
        homeContent.featureSection.items = [
            ...homeContent.featureSection.items,
            { icon: "FileText", title: "", description: "", color: "text-blue-500", bg: "bg-blue-500/10" },
        ];
    }

    function removeHomeFeature(index: number) {
        homeContent.featureSection.items = homeContent.featureSection.items.filter((_, i) => i !== index);
    }

    function addPricingFaq() {
        pricingContent.faq.items = [...pricingContent.faq.items, { question: "", answer: "" }];
    }

    function removePricingFaq(index: number) {
        pricingContent.faq.items = pricingContent.faq.items.filter((_, i) => i !== index);
    }

    onMount(async () => {
        if (!auth.isAuthenticated) {
            goto("/login");
            return;
        }
        if (auth.user?.role !== "SUPER_ADMIN") {
            goto("/dashboard");
            return;
        }
        await loadPage("HOME");
    });
</script>

<svelte:head>
    <title>Site Content Admin - Survey Engine</title>
</svelte:head>

<div class="mx-auto max-w-6xl space-y-6">
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
            <h1 class="flex items-center gap-2 text-2xl font-bold tracking-tight text-foreground">
                <FilePenLine class="h-6 w-6 text-primary" />
                Site Content Admin
            </h1>
            <p class="mt-1 text-sm text-muted-foreground">
                Manage public landing and pricing content with draft and publish controls.
            </p>
        </div>
        <Badge variant="outline" class="w-fit border-primary/20 bg-primary/5 text-primary">
            Super Admin
        </Badge>
    </div>

    <div class="flex flex-wrap items-center gap-3">
        <Button variant={currentPageKey === "HOME" ? "default" : "outline"} onclick={() => loadPage("HOME")}>
            Home
        </Button>
        <Button variant={currentPageKey === "PRICING" ? "default" : "outline"} onclick={() => loadPage("PRICING")}>
            Pricing
        </Button>
        <div class="ml-auto flex flex-wrap items-center gap-2">
            <Button variant="outline" onclick={previewPage}>
                <Eye class="mr-2 h-4 w-4" />
                Preview
            </Button>
            <Button variant="outline" onclick={saveDraft} disabled={saving || loading}>
                <Save class="mr-2 h-4 w-4" />
                Save Draft
            </Button>
            <Button onclick={publishPage} disabled={publishing || loading}>
                <Upload class="mr-2 h-4 w-4" />
                Publish
            </Button>
            <Button variant="outline" onclick={unpublishPage} disabled={!published || publishing || loading}>
                Unpublish
            </Button>
        </div>
    </div>

    {#if error}
        <div class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
            {error}
        </div>
    {/if}

    {#if success}
        <div class="rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-700">
            {success}
        </div>
    {/if}

    <div class="flex items-center gap-2 text-sm text-muted-foreground">
        <Globe class="h-4 w-4" />
        <span>Status: {published ? "Published" : "Draft only"}</span>
        {#if publishedAt}
            <span>· Published at {new Date(publishedAt).toLocaleString()}</span>
        {/if}
    </div>

    {#if loading}
        <div class="grid gap-6 lg:grid-cols-2">
            {#each Array(4) as _}
                <Card.Root>
                    <Card.Header>
                        <Skeleton class="h-6 w-[180px]" />
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        {#each Array(4) as _}
                            <Skeleton class="h-10 w-full" />
                        {/each}
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {:else if currentPageKey === "HOME"}
        <div class="grid gap-6">
            <Card.Root>
                <Card.Header>
                    <Card.Title class="flex items-center gap-2">
                        <Megaphone class="h-5 w-5 text-primary" />
                        Announcement Bar
                    </Card.Title>
                </Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div class="flex items-center gap-3">
                        <Switch bind:checked={homeContent.announcement.enabled} />
                        <span class="text-sm text-foreground">Enable announcement</span>
                    </div>
                    <div class="md:col-span-2">
                        <Label for="home-announcement-text">Text</Label>
                        <Input id="home-announcement-text" bind:value={homeContent.announcement.text} />
                    </div>
                    <div>
                        <Label for="home-announcement-link-label">Link Label</Label>
                        <Input id="home-announcement-link-label" bind:value={homeContent.announcement.linkLabel} />
                    </div>
                    <div>
                        <Label for="home-announcement-link-url">Link URL</Label>
                        <Input id="home-announcement-link-url" bind:value={homeContent.announcement.linkUrl} />
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header><Card.Title>Hero</Card.Title></Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div><Label>Badge</Label><Input bind:value={homeContent.hero.badge} /></div>
                    <div><Label>Footnote</Label><Input bind:value={homeContent.hero.footnote} /></div>
                    <div><Label>Title</Label><Input bind:value={homeContent.hero.title} /></div>
                    <div><Label>Highlight</Label><Input bind:value={homeContent.hero.highlight} /></div>
                    <div class="md:col-span-2"><Label>Description</Label><Textarea bind:value={homeContent.hero.description} rows={4} /></div>
                    <div><Label>Primary CTA Label</Label><Input bind:value={homeContent.hero.primaryCtaLabel} /></div>
                    <div><Label>Primary CTA URL</Label><Input bind:value={homeContent.hero.primaryCtaUrl} /></div>
                    <div><Label>Secondary CTA Label</Label><Input bind:value={homeContent.hero.secondaryCtaLabel} /></div>
                    <div><Label>Secondary CTA URL</Label><Input bind:value={homeContent.hero.secondaryCtaUrl} /></div>
                    <div><Label>Tertiary CTA Label</Label><Input bind:value={homeContent.hero.tertiaryCtaLabel} /></div>
                    <div><Label>Tertiary CTA URL</Label><Input bind:value={homeContent.hero.tertiaryCtaUrl} /></div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header>
                    <Card.Title>Logo Strip</Card.Title>
                    <Card.Description>Use short customer or sector labels for trust signals.</Card.Description>
                </Card.Header>
                <Card.Content class="space-y-4">
                    <div><Label>Section Title</Label><Input bind:value={homeContent.logoStrip.title} /></div>
                    {#each homeContent.logoStrip.logos as logo, index}
                        <div class="flex items-end gap-3">
                            <div class="flex-1">
                                <Label>Logo Label {index + 1}</Label>
                                <Input bind:value={logo.label} />
                            </div>
                            <Button variant="outline" size="icon" onclick={() => removeHomeLogo(index)}>
                                <Trash2 class="h-4 w-4" />
                            </Button>
                        </div>
                    {/each}
                    <Button variant="outline" onclick={addHomeLogo}>
                        <Plus class="mr-2 h-4 w-4" />
                        Add Logo
                    </Button>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header>
                    <Card.Title>Feature Section</Card.Title>
                    <Card.Description>Icons supported: FileText, Shield, Megaphone, BarChart3, Users, Zap, Globe.</Card.Description>
                </Card.Header>
                <Card.Content class="space-y-4">
                    <div><Label>Title</Label><Input bind:value={homeContent.featureSection.title} /></div>
                    <div><Label>Description</Label><Textarea bind:value={homeContent.featureSection.description} rows={3} /></div>
                    {#each homeContent.featureSection.items as item, index}
                        <div class="rounded-xl border border-border/60 p-4 space-y-3">
                            <div class="flex items-center justify-between">
                                <p class="text-sm font-semibold text-foreground">Feature {index + 1}</p>
                                <Button variant="outline" size="icon" onclick={() => removeHomeFeature(index)}>
                                    <Trash2 class="h-4 w-4" />
                                </Button>
                            </div>
                            <div class="grid gap-3 md:grid-cols-2">
                                <div><Label>Icon</Label><Input bind:value={item.icon} /></div>
                                <div><Label>Title</Label><Input bind:value={item.title} /></div>
                                <div class="md:col-span-2"><Label>Description</Label><Textarea bind:value={item.description} rows={3} /></div>
                                <div><Label>Text Color Class</Label><Input bind:value={item.color} /></div>
                                <div><Label>Background Class</Label><Input bind:value={item.bg} /></div>
                            </div>
                        </div>
                    {/each}
                    <Button variant="outline" onclick={addHomeFeature}>
                        <Plus class="mr-2 h-4 w-4" />
                        Add Feature
                    </Button>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header><Card.Title>Pricing Preview + CTA + SEO</Card.Title></Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div><Label>Pricing Preview Title</Label><Input bind:value={homeContent.pricingPreview.title} /></div>
                    <div><Label>Pricing Preview Description</Label><Input bind:value={homeContent.pricingPreview.description} /></div>
                    <div><Label>CTA Title</Label><Input bind:value={homeContent.cta.title} /></div>
                    <div><Label>CTA Description</Label><Input bind:value={homeContent.cta.description} /></div>
                    <div><Label>CTA Button Label</Label><Input bind:value={homeContent.cta.primaryCtaLabel} /></div>
                    <div><Label>CTA Button URL</Label><Input bind:value={homeContent.cta.primaryCtaUrl} /></div>
                    <div><Label>SEO Title</Label><Input bind:value={homeContent.seo.title} /></div>
                    <div><Label>SEO Description</Label><Input bind:value={homeContent.seo.description} /></div>
                </Card.Content>
            </Card.Root>
        </div>
    {:else}
        <div class="grid gap-6">
            <Card.Root>
                <Card.Header><Card.Title>Announcement Bar</Card.Title></Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div class="flex items-center gap-3">
                        <Switch bind:checked={pricingContent.announcement.enabled} />
                        <span class="text-sm text-foreground">Enable announcement</span>
                    </div>
                    <div class="md:col-span-2"><Label>Text</Label><Input bind:value={pricingContent.announcement.text} /></div>
                    <div><Label>Link Label</Label><Input bind:value={pricingContent.announcement.linkLabel} /></div>
                    <div><Label>Link URL</Label><Input bind:value={pricingContent.announcement.linkUrl} /></div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header><Card.Title>Pricing Hero</Card.Title></Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div><Label>Badge</Label><Input bind:value={pricingContent.hero.badge} /></div>
                    <div><Label>Highlight</Label><Input bind:value={pricingContent.hero.highlight} /></div>
                    <div class="md:col-span-2"><Label>Title</Label><Input bind:value={pricingContent.hero.title} /></div>
                    <div class="md:col-span-2"><Label>Description</Label><Textarea bind:value={pricingContent.hero.description} rows={4} /></div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header><Card.Title>FAQ</Card.Title></Card.Header>
                <Card.Content class="space-y-4">
                    <div><Label>FAQ Title</Label><Input bind:value={pricingContent.faq.title} /></div>
                    <div><Label>FAQ Description</Label><Textarea bind:value={pricingContent.faq.description} rows={3} /></div>
                    {#each pricingContent.faq.items as item, index}
                        <div class="rounded-xl border border-border/60 p-4 space-y-3">
                            <div class="flex items-center justify-between">
                                <p class="text-sm font-semibold text-foreground">FAQ {index + 1}</p>
                                <Button variant="outline" size="icon" onclick={() => removePricingFaq(index)}>
                                    <Trash2 class="h-4 w-4" />
                                </Button>
                            </div>
                            <div><Label>Question</Label><Input bind:value={item.question} /></div>
                            <div><Label>Answer</Label><Textarea bind:value={item.answer} rows={4} /></div>
                        </div>
                    {/each}
                    <Button variant="outline" onclick={addPricingFaq}>
                        <Plus class="mr-2 h-4 w-4" />
                        Add FAQ
                    </Button>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Header><Card.Title>Final CTA + SEO</Card.Title></Card.Header>
                <Card.Content class="grid gap-4 md:grid-cols-2">
                    <div><Label>CTA Title</Label><Input bind:value={pricingContent.cta.title} /></div>
                    <div><Label>CTA Description</Label><Input bind:value={pricingContent.cta.description} /></div>
                    <div><Label>Primary CTA Label</Label><Input bind:value={pricingContent.cta.primaryCtaLabel} /></div>
                    <div><Label>Primary CTA URL</Label><Input bind:value={pricingContent.cta.primaryCtaUrl} /></div>
                    <div><Label>Secondary CTA Label</Label><Input bind:value={pricingContent.cta.secondaryCtaLabel} /></div>
                    <div><Label>Secondary CTA URL</Label><Input bind:value={pricingContent.cta.secondaryCtaUrl} /></div>
                    <div class="md:col-span-2"><Label>Footnote</Label><Input bind:value={pricingContent.cta.footnote} /></div>
                    <div><Label>SEO Title</Label><Input bind:value={pricingContent.seo.title} /></div>
                    <div><Label>SEO Description</Label><Input bind:value={pricingContent.seo.description} /></div>
                </Card.Content>
            </Card.Root>
        </div>
    {/if}
</div>
