<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount, onDestroy, tick } from "svelte";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Textarea } from "$lib/components/ui/textarea";
    import { Button } from "$lib/components/ui/button";
    import { Switch } from "$lib/components/ui/switch";
    import { Badge } from "$lib/components/ui/badge";
    import {
        Globe, Save, Upload, Eye, Plus, Trash2, Loader2,
        CheckCircle2, AlertCircle, CircleDot, Search, Megaphone,
        Star, Zap, Tag, MousePointerClick, HelpCircle, Home,
        LayoutDashboard, ArrowUpRight,
    } from "lucide-svelte";
    import {
        defaultHomeSiteContent, defaultPricingSiteContent,
        normalizeHomeSiteContent, normalizePricingSiteContent,
        type HomeSiteContent, type PricingSiteContent, type SiteContentPageKey,
    } from "$lib/site-content";

    interface PageResponse {
        pageKey: SiteContentPageKey; draftContent: unknown;
        publishedContent: unknown; published: boolean;
        publishedAt?: string | null; updatedAt?: string | null;
    }
    interface Toast { id: number; type: "success" | "error" | "info"; message: string; }

    let currentPageKey = $state<SiteContentPageKey>("HOME");
    let loading = $state(true);
    let saving = $state(false);
    let publishing = $state(false);
    let autosaveStatus = $state<"idle"|"pending"|"saving"|"saved"|"error">("idle");
    let published = $state(false);
    let publishedAt = $state<string | null>(null);
    let isDirty = $state(false);
    let toasts = $state<Toast[]>([]);
    let toastN = 0;
    let saveTimer: ReturnType<typeof setTimeout> | null = null;
    let activeSectionId = $state("");
    let observers: IntersectionObserver[] = [];

    let homeContent = $state<HomeSiteContent>(defaultHomeSiteContent());
    let pricingContent = $state<PricingSiteContent>(defaultPricingSiteContent());

    const HOME_NAV = [
        { id: "seo",            label: "SEO",             Icon: Search },
        { id: "announcement",   label: "Announcement",    Icon: Megaphone },
        { id: "hero",           label: "Hero",            Icon: Star },
        { id: "logos",          label: "Logo Strip",      Icon: LayoutDashboard },
        { id: "features",       label: "Features",        Icon: Zap },
        { id: "carousel",       label: "Carousel",        Icon: Globe },
        { id: "testimonials",    label: "Testimonials",    Icon: Star },
        { id: "pricing",        label: "Pricing Preview", Icon: Tag },
        { id: "cta",            label: "Call to Action",  Icon: MousePointerClick },
    ];
    const PRICING_NAV = [
        { id: "seo",          label: "SEO",           Icon: Search },
        { id: "announcement", label: "Announcement",  Icon: Megaphone },
        { id: "hero",         label: "Hero",          Icon: Star },
        { id: "faq",          label: "FAQ",           Icon: HelpCircle },
        { id: "cta",          label: "Call to Action",Icon: MousePointerClick },
    ];

    const navItems = $derived(currentPageKey === "HOME" ? HOME_NAV : PRICING_NAV);

    $effect(() => {
        const _ = JSON.stringify(currentPageKey === "HOME" ? homeContent : pricingContent);
        if (!loading) {
            isDirty = true; autosaveStatus = "pending";
            if (saveTimer) clearTimeout(saveTimer);
            saveTimer = setTimeout(autosave, 1500);
        }
    });

    function toast(type: Toast["type"], message: string) {
        const id = ++toastN;
        toasts = [...toasts, { id, type, message }];
        setTimeout(() => { toasts = toasts.filter(t => t.id !== id); }, 3500);
    }

    function setupObservers() {
        observers.forEach(o => o.disconnect()); observers = [];
        navItems.forEach(({ id }) => {
            const el = document.getElementById(`s-${id}`);
            if (!el) return;
            const o = new IntersectionObserver(
                ([e]) => { if (e.isIntersecting) activeSectionId = id; },
                { rootMargin: "-20% 0px -65% 0px" }
            );
            o.observe(el); observers.push(o);
        });
    }

    async function loadPage(key: SiteContentPageKey) {
        loading = true; isDirty = false; autosaveStatus = "idle";
        if (saveTimer) { clearTimeout(saveTimer); saveTimer = null; }
        try {
            const { data } = await api.get<PageResponse>(`/admin/superadmin/site-content/${key}`);
            currentPageKey = key; published = data.published; publishedAt = data.publishedAt ?? null;
            if (key === "HOME") homeContent = normalizeHomeSiteContent(data.draftContent);
            else pricingContent = normalizePricingSiteContent(data.draftContent);
        } catch { toast("error", "Failed to load page."); }
        finally { loading = false; await tick(); setupObservers(); }
    }

    async function autosave() {
        if (!isDirty) return;
        autosaveStatus = "saving";
        try {
            await api.put(`/admin/superadmin/site-content/${currentPageKey}`, {
                content: currentPageKey === "HOME" ? homeContent : pricingContent,
            });
            isDirty = false; autosaveStatus = "saved";
            setTimeout(() => { if (autosaveStatus === "saved") autosaveStatus = "idle"; }, 2500);
        } catch { autosaveStatus = "error"; }
    }

    async function saveDraft() {
        if (saveTimer) { clearTimeout(saveTimer); saveTimer = null; }
        saving = true;
        try {
            await api.put(`/admin/superadmin/site-content/${currentPageKey}`, {
                content: currentPageKey === "HOME" ? homeContent : pricingContent,
            });
            isDirty = false; autosaveStatus = "saved";
            toast("success", "Draft saved.");
            setTimeout(() => { if (autosaveStatus === "saved") autosaveStatus = "idle"; }, 2500);
        } catch { toast("error", "Failed to save."); }
        finally { saving = false; }
    }

    async function publishPage() {
        publishing = true;
        try {
            await api.post(`/admin/superadmin/site-content/${currentPageKey}/publish`);
            published = true; publishedAt = new Date().toISOString();
            toast("success", "Page published — changes are live.");
        } catch { toast("error", "Failed to publish."); }
        finally { publishing = false; }
    }

    async function unpublishPage() {
        publishing = true;
        try {
            await api.post(`/admin/superadmin/site-content/${currentPageKey}/unpublish`);
            published = false; toast("info", "Page unpublished — now in draft mode.");
        } catch { toast("error", "Failed to unpublish."); }
        finally { publishing = false; }
    }

    function jumpTo(id: string) {
        document.getElementById(`s-${id}`)?.scrollIntoView({ behavior: "smooth", block: "start" });
    }

    function addLogo() { homeContent.logoStrip.logos = [...homeContent.logoStrip.logos, { label: "" }]; }
    function removeLogo(i: number) { homeContent.logoStrip.logos = homeContent.logoStrip.logos.filter((_, x) => x !== i); }
    function addFeature() {
        homeContent.featureSection.items = [...homeContent.featureSection.items,
            { icon: "FileText", title: "", description: "", color: "text-blue-500", bg: "bg-blue-500/10" }];
    }
    function removeFeature(i: number) { homeContent.featureSection.items = homeContent.featureSection.items.filter((_, x) => x !== i); }
    function addCarouselSlide() {
        homeContent.carousel.slides = [...homeContent.carousel.slides, {
            tag: "", headline: "", subheadline: "", description: "",
            ctaLabel: "", ctaUrl: "", secondaryCtaLabel: "", secondaryCtaUrl: "",
            accentColor: "hsl(263 70% 55%)",
        }];
    }
    function removeCarouselSlide(i: number) { homeContent.carousel.slides = homeContent.carousel.slides.filter((_, x) => x !== i); }
    function addTestimonial() {
        homeContent.testimonials.items = [...homeContent.testimonials.items, {
            quote: "", authorName: "", authorRole: "", authorCompany: "", authorInitials: "", avatarHue: 263,
        }];
    }
    function removeTestimonial(i: number) { homeContent.testimonials.items = homeContent.testimonials.items.filter((_, x) => x !== i); }
    function addFaq() { pricingContent.faq.items = [...pricingContent.faq.items, { question: "", answer: "" }]; }
    function removeFaq(i: number) { pricingContent.faq.items = pricingContent.faq.items.filter((_, x) => x !== i); }

    onMount(async () => {
        if (!auth.isAuthenticated) { goto("/login"); return; }
        if (auth.user?.role !== "SUPER_ADMIN") { goto("/dashboard"); return; }
        await loadPage("HOME");
    });
    onDestroy(() => { observers.forEach(o => o.disconnect()); if (saveTimer) clearTimeout(saveTimer); });
</script>

<svelte:head>
    <title>{isDirty ? "● " : ""}Site Content – Survey Engine</title>
</svelte:head>

<!-- ROOT: full height, no overflow -->
<div class="builder-root">

    <!-- ══ TOP TOOLBAR ══════════════════════════════════════════════════════ -->
    <div class="builder-toolbar">
        <!-- Page switcher tabs -->
        <div class="page-tabs">
            <button class="page-tab {currentPageKey === 'HOME' ? 'active' : ''}" onclick={() => loadPage("HOME")}>
                <Home class="h-3.5 w-3.5" />Home
            </button>
            <button class="page-tab {currentPageKey === 'PRICING' ? 'active' : ''}" onclick={() => loadPage("PRICING")}>
                <Tag class="h-3.5 w-3.5" />Pricing
            </button>
        </div>

        <!-- Status + Actions -->
        <div class="toolbar-actions">
            <!-- Autosave pill -->
            {#if autosaveStatus === "saving"}
                <span class="save-pill saving"><Loader2 class="h-3 w-3 animate-spin" />Saving…</span>
            {:else if autosaveStatus === "saved"}
                <span class="save-pill saved"><CheckCircle2 class="h-3 w-3" />Saved</span>
            {:else if autosaveStatus === "error"}
                <span class="save-pill err"><AlertCircle class="h-3 w-3" />Error</span>
            {:else if isDirty}
                <span class="save-pill dirty"><CircleDot class="h-3 w-3" />Unsaved</span>
            {/if}

            <!-- Publish status badge -->
            {#if published}
                <Badge class="status-live">Live</Badge>
            {:else}
                <Badge variant="outline" class="status-draft">Draft</Badge>
            {/if}

            <Button variant="ghost" size="sm" class="toolbar-btn"
                onclick={() => window.open(currentPageKey === "HOME" ? "/" : "/pricing", "_blank", "noopener")}>
                <Eye class="h-3.5 w-3.5" />Preview
                <ArrowUpRight class="h-3 w-3 opacity-50" />
            </Button>
            <Button variant="outline" size="sm" class="toolbar-btn" onclick={saveDraft} disabled={saving || loading}>
                {#if saving}<Loader2 class="h-3.5 w-3.5 animate-spin" />{:else}<Save class="h-3.5 w-3.5" />{/if}
                Save Draft
            </Button>
            <Button size="sm" class="publish-btn" onclick={publishPage} disabled={publishing || loading}>
                {#if publishing}<Loader2 class="h-3.5 w-3.5 animate-spin" />{:else}<Upload class="h-3.5 w-3.5" />{/if}
                Publish
            </Button>
            {#if published}
                <Button variant="ghost" size="sm" class="unpublish-btn" onclick={unpublishPage} disabled={publishing}>
                    Unpublish
                </Button>
            {/if}
        </div>
    </div>

    <!-- ══ BODY: SIDEBAR + CANVAS ══════════════════════════════════════════ -->
    <div class="builder-body">

        <!-- ── LEFT NAV ──────────────────────────────────────────────────── -->
        <nav class="builder-nav">
            <div class="nav-header">Sections</div>
            {#each navItems as { id, label, Icon }}
                <button
                    class="nav-item {activeSectionId === id ? 'active' : ''}"
                    onclick={() => jumpTo(id)}>
                    <span class="nav-icon"><Icon class="h-3.5 w-3.5" /></span>
                    {label}
                    {#if activeSectionId === id}
                        <span class="nav-pip"></span>
                    {/if}
                </button>
            {/each}
        </nav>

        <!-- ── CANVAS ────────────────────────────────────────────────────── -->
        <main class="builder-canvas">
            {#if loading}
                <div class="canvas-inner">
                    {#each Array(5) as _}
                        <div class="skel-section">
                            <div class="skel-bar" style="width:9rem;height:1.25rem;margin-bottom:.75rem"></div>
                            <div class="skel-bar" style="width:100%;height:2.5rem;margin-bottom:.5rem"></div>
                            <div class="skel-bar" style="width:100%;height:2.5rem;margin-bottom:.5rem"></div>
                            <div class="skel-bar" style="width:60%;height:2.5rem"></div>
                        </div>
                    {/each}
                </div>

            {:else if currentPageKey === "HOME"}
            <div class="canvas-inner">

                <!-- SEO ─────────────────────────────────────────────────── -->
                <section id="s-seo" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Search class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">SEO</h2>
                            <p class="section-desc">Controls how search engines index and display this page.</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Page Title</Label>
                            <Input bind:value={homeContent.seo.title} placeholder="Survey Engine – Enterprise Survey Platform" /></div>
                        <div class="field md2"><Label>Meta Description</Label>
                            <Textarea bind:value={homeContent.seo.description} rows={2} placeholder="Describe this page for search engines…" /></div>
                    </div>
                </section>

                <!-- ANNOUNCEMENT ─────────────────────────────────────────── -->
                <section id="s-announcement" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Megaphone class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Announcement Bar</h2>
                            <p class="section-desc">Top-of-page banner strip with optional link.</p>
                        </div>
                        <div class="section-head-right">
                            <Switch bind:checked={homeContent.announcement.enabled} />
                            <span class="toggle-label">{homeContent.announcement.enabled ? "Enabled" : "Disabled"}</span>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field md2"><Label>Announcement Text</Label>
                            <Input bind:value={homeContent.announcement.text} disabled={!homeContent.announcement.enabled} /></div>
                        <div class="field"><Label>Link Label</Label>
                            <Input bind:value={homeContent.announcement.linkLabel} disabled={!homeContent.announcement.enabled} /></div>
                        <div class="field"><Label>Link URL</Label>
                            <Input bind:value={homeContent.announcement.linkUrl} disabled={!homeContent.announcement.enabled} /></div>
                    </div>
                </section>

                <!-- HERO ───────────────────────────────────────────────── -->
                <section id="s-hero" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Star class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Hero</h2>
                            <p class="section-desc">Main headline, badge, description, and CTAs.</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Top Badge</Label><Input bind:value={homeContent.hero.badge} /></div>
                        <div class="field"><Label>Footnote</Label><Input bind:value={homeContent.hero.footnote} /></div>
                        <div class="field"><Label>Title (before highlight)</Label><Input bind:value={homeContent.hero.title} /></div>
                        <div class="field"><Label>Highlighted Word</Label><Input bind:value={homeContent.hero.highlight} /></div>
                        <div class="field md2"><Label>Description</Label><Textarea bind:value={homeContent.hero.description} rows={3} /></div>
                        <div class="field"><Label>Primary CTA Label</Label><Input bind:value={homeContent.hero.primaryCtaLabel} /></div>
                        <div class="field"><Label>Primary CTA URL</Label><Input bind:value={homeContent.hero.primaryCtaUrl} /></div>
                        <div class="field"><Label>Secondary CTA Label</Label><Input bind:value={homeContent.hero.secondaryCtaLabel} /></div>
                        <div class="field"><Label>Secondary CTA URL</Label><Input bind:value={homeContent.hero.secondaryCtaUrl} /></div>
                        <div class="field"><Label>Tertiary CTA Label</Label><Input bind:value={homeContent.hero.tertiaryCtaLabel} /></div>
                        <div class="field"><Label>Tertiary CTA URL</Label><Input bind:value={homeContent.hero.tertiaryCtaUrl} /></div>
                    </div>
                </section>

                <!-- LOGO STRIP ──────────────────────────────────────────── -->
                <section id="s-logos" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><LayoutDashboard class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Logo Strip <span class="count-chip">{homeContent.logoStrip.logos.length}</span></h2>
                            <p class="section-desc">Trust signal logo labels below the hero.</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field md2"><Label>Section Title</Label><Input bind:value={homeContent.logoStrip.title} /></div>
                    </div>
                    <div class="list-editor">
                        {#each homeContent.logoStrip.logos as logo, i}
                            <div class="list-row">
                                <span class="list-num">{i + 1}</span>
                                <Input bind:value={logo.label} placeholder="Company or team name…" class="flex-1 min-w-0" />
                                <button class="rem-btn" onclick={() => removeLogo(i)} aria-label="Remove"><Trash2 class="h-4 w-4" /></button>
                            </div>
                        {/each}
                        <Button variant="outline" size="sm" onclick={addLogo} class="add-btn"><Plus class="h-4 w-4" />Add Logo</Button>
                    </div>
                </section>

                <!-- FEATURES ────────────────────────────────────────────── -->
                <section id="s-features" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Zap class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Features <span class="count-chip">{homeContent.featureSection.items.length}</span></h2>
                            <p class="section-desc">Icons: FileText · Shield · Megaphone · BarChart3 · Users · Zap · Globe</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Section Title</Label><Input bind:value={homeContent.featureSection.title} /></div>
                        <div class="field"><Label>Section Description</Label><Input bind:value={homeContent.featureSection.description} /></div>
                    </div>
                    <div class="list-editor">
                        {#each homeContent.featureSection.items as item, i}
                            <div class="item-card">
                                <div class="item-card-head">
                                    <span class="item-card-label">Feature {i + 1}</span>
                                    <button class="rem-btn" onclick={() => removeFeature(i)}><Trash2 class="h-4 w-4" /></button>
                                </div>
                                <div class="form-grid">
                                    <div class="field"><Label>Icon Name</Label><Input bind:value={item.icon} placeholder="Zap" /></div>
                                    <div class="field"><Label>Title</Label><Input bind:value={item.title} /></div>
                                    <div class="field md2"><Label>Description</Label><Textarea bind:value={item.description} rows={2} /></div>
                                    <div class="field"><Label>Text Color Class</Label><Input bind:value={item.color} placeholder="text-blue-500" /></div>
                                    <div class="field"><Label>Background Class</Label><Input bind:value={item.bg} placeholder="bg-blue-500/10" /></div>
                                </div>
                            </div>
                        {/each}
                        <Button variant="outline" size="sm" onclick={addFeature} class="add-btn"><Plus class="h-4 w-4" />Add Feature</Button>
                    </div>
                </section>

                <!-- CAROUSEL ─────────────────────────────────────────────── -->
                <section id="s-carousel" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Globe class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Carousel <span class="count-chip">{homeContent.carousel?.slides?.length ?? 0}</span></h2>
                            <p class="section-desc">Events, announcements, or promotions. Only shown on the public site when enabled.</p>
                        </div>
                        <div class="section-head-right">
                            <Switch bind:checked={homeContent.carousel.enabled} />
                            <span class="toggle-label">{homeContent.carousel.enabled ? "Visible" : "Hidden"}</span>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Section Title</Label><Input bind:value={homeContent.carousel.title} /></div>
                        <div class="field"><Label>Section Description</Label><Input bind:value={homeContent.carousel.description} /></div>
                        <div class="field"><Label>Autoplay (seconds, 0 = off)</Label>
                            <Input type="number" min="0" max="30" bind:value={homeContent.carousel.autoPlaySeconds} /></div>
                    </div>
                    <div class="list-editor">
                        {#each homeContent.carousel.slides as slide, i}
                            <div class="item-card">
                                <div class="item-card-head">
                                    <span class="item-card-label">Slide {i + 1}</span>
                                    <button class="rem-btn" onclick={() => removeCarouselSlide(i)}><Trash2 class="h-4 w-4" /></button>
                                </div>
                                <div class="form-grid">
                                    <div class="field"><Label>Tag / Category</Label><Input bind:value={slide.tag} placeholder="Webinar, Event, New Feature…" /></div>
                                    <div class="field"><Label>Accent Color</Label><Input bind:value={slide.accentColor} placeholder="hsl(263 70% 55%)" /></div>
                                    <div class="field md2"><Label>Headline</Label><Input bind:value={slide.headline} /></div>
                                    <div class="field md2"><Label>Subheadline</Label><Input bind:value={slide.subheadline} /></div>
                                    <div class="field md2"><Label>Description</Label><Textarea bind:value={slide.description} rows={2} /></div>
                                    <div class="field"><Label>Primary CTA Label</Label><Input bind:value={slide.ctaLabel} /></div>
                                    <div class="field"><Label>Primary CTA URL</Label><Input bind:value={slide.ctaUrl} /></div>
                                    <div class="field"><Label>Secondary CTA Label</Label><Input bind:value={slide.secondaryCtaLabel} /></div>
                                    <div class="field"><Label>Secondary CTA URL</Label><Input bind:value={slide.secondaryCtaUrl} /></div>
                                </div>
                            </div>
                        {/each}
                        <Button variant="outline" size="sm" onclick={addCarouselSlide} class="add-btn"><Plus class="h-4 w-4" />Add Slide</Button>
                    </div>
                </section>

                <!-- TESTIMONIALS ─────────────────────────────────────────── -->
                <section id="s-testimonials" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Star class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Testimonials <span class="count-chip">{homeContent.testimonials?.items?.length ?? 0}</span></h2>
                            <p class="section-desc">Customer quotes shown below the logo strip. Toggle off to hide the section entirely.</p>
                        </div>
                        <div class="section-head-right">
                            <Switch bind:checked={homeContent.testimonials.enabled} />
                            <span class="toggle-label">{homeContent.testimonials.enabled ? "Visible" : "Hidden"}</span>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Section Title</Label><Input bind:value={homeContent.testimonials.title} /></div>
                        <div class="field md2"><Label>Section Description</Label><Textarea bind:value={homeContent.testimonials.description} rows={2} /></div>
                    </div>
                    <div class="list-editor">
                        {#each homeContent.testimonials.items as item, i}
                            <div class="item-card">
                                <div class="item-card-head">
                                    <span class="item-card-label">Testimonial {i + 1}</span>
                                    <button class="rem-btn" onclick={() => removeTestimonial(i)}><Trash2 class="h-4 w-4" /></button>
                                </div>
                                <div class="form-grid">
                                    <div class="field md2"><Label>Quote</Label><Textarea bind:value={item.quote} rows={3} placeholder="Describe the value they get…" /></div>
                                    <div class="field"><Label>Author Name</Label><Input bind:value={item.authorName} /></div>
                                    <div class="field"><Label>Initials (avatar)</Label><Input bind:value={item.authorInitials} placeholder="SC" maxlength={3} /></div>
                                    <div class="field"><Label>Role</Label><Input bind:value={item.authorRole} placeholder="Head of Engineering" /></div>
                                    <div class="field"><Label>Company</Label><Input bind:value={item.authorCompany} /></div>
                                    <div class="field"><Label>Avatar Hue (0–360)</Label><Input type="number" min="0" max="360" value={item.avatarHue} oninput={(e) => { item.avatarHue = Number((e.target as HTMLInputElement).value); }} /></div>
                                </div>
                            </div>
                        {/each}
                        <Button variant="outline" size="sm" onclick={addTestimonial} class="add-btn"><Plus class="h-4 w-4" />Add Testimonial</Button>
                    </div>
                </section>

                <!-- PRICING PREVIEW ─────────────────────────────────────── -->
                <section id="s-pricing" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Tag class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Pricing Preview</h2>
                            <p class="section-desc">Headline and description above the plan cards.</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Title</Label><Input bind:value={homeContent.pricingPreview.title} /></div>
                        <div class="field"><Label>Description</Label><Input bind:value={homeContent.pricingPreview.description} /></div>
                    </div>
                </section>

                <!-- CTA ─────────────────────────────────────────────────── -->
                <section id="s-cta" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><MousePointerClick class="h-4 w-4" /></div>
                        <div>
                            <h2 class="section-title">Call to Action</h2>
                            <p class="section-desc">Bottom-of-page conversion block.</p>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Title</Label><Input bind:value={homeContent.cta.title} /></div>
                        <div class="field"><Label>Description</Label><Input bind:value={homeContent.cta.description} /></div>
                        <div class="field"><Label>Primary CTA Label</Label><Input bind:value={homeContent.cta.primaryCtaLabel} /></div>
                        <div class="field"><Label>Primary CTA URL</Label><Input bind:value={homeContent.cta.primaryCtaUrl} /></div>
                        <div class="field"><Label>Secondary CTA Label</Label><Input bind:value={homeContent.cta.secondaryCtaLabel} /></div>
                        <div class="field"><Label>Secondary CTA URL</Label><Input bind:value={homeContent.cta.secondaryCtaUrl} /></div>
                        <div class="field md2"><Label>Footnote</Label><Input bind:value={homeContent.cta.footnote} /></div>
                    </div>
                </section>

            </div>

            <!-- ═══ PRICING PAGE ═════════════════════════════════════════ -->
            {:else}
            <div class="canvas-inner">

                <section id="s-seo" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Search class="h-4 w-4" /></div>
                        <div><h2 class="section-title">SEO</h2><p class="section-desc">Search engine title and description for the pricing page.</p></div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Page Title</Label><Input bind:value={pricingContent.seo.title} /></div>
                        <div class="field md2"><Label>Meta Description</Label><Textarea bind:value={pricingContent.seo.description} rows={2} /></div>
                    </div>
                </section>

                <section id="s-announcement" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Megaphone class="h-4 w-4" /></div>
                        <div><h2 class="section-title">Announcement Bar</h2><p class="section-desc">Top banner strip for the pricing page.</p></div>
                        <div class="section-head-right">
                            <Switch bind:checked={pricingContent.announcement.enabled} />
                            <span class="toggle-label">{pricingContent.announcement.enabled ? "Enabled" : "Disabled"}</span>
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="field md2"><Label>Text</Label><Input bind:value={pricingContent.announcement.text} disabled={!pricingContent.announcement.enabled} /></div>
                        <div class="field"><Label>Link Label</Label><Input bind:value={pricingContent.announcement.linkLabel} disabled={!pricingContent.announcement.enabled} /></div>
                        <div class="field"><Label>Link URL</Label><Input bind:value={pricingContent.announcement.linkUrl} disabled={!pricingContent.announcement.enabled} /></div>
                    </div>
                </section>

                <section id="s-hero" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><Star class="h-4 w-4" /></div>
                        <div><h2 class="section-title">Hero</h2><p class="section-desc">Pricing page headline, badge, and description.</p></div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Badge</Label><Input bind:value={pricingContent.hero.badge} /></div>
                        <div class="field"><Label>Highlighted Word</Label><Input bind:value={pricingContent.hero.highlight} /></div>
                        <div class="field md2"><Label>Title</Label><Input bind:value={pricingContent.hero.title} /></div>
                        <div class="field md2"><Label>Description</Label><Textarea bind:value={pricingContent.hero.description} rows={3} /></div>
                    </div>
                </section>

                <section id="s-faq" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><HelpCircle class="h-4 w-4" /></div>
                        <div><h2 class="section-title">FAQ <span class="count-chip">{pricingContent.faq.items.length}</span></h2><p class="section-desc">Common questions shown on the pricing page.</p></div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>FAQ Section Title</Label><Input bind:value={pricingContent.faq.title} /></div>
                        <div class="field"><Label>FAQ Section Description</Label><Input bind:value={pricingContent.faq.description} /></div>
                    </div>
                    <div class="list-editor">
                        {#each pricingContent.faq.items as item, i}
                            <div class="item-card">
                                <div class="item-card-head">
                                    <span class="item-card-label">FAQ {i + 1}</span>
                                    <button class="rem-btn" onclick={() => removeFaq(i)}><Trash2 class="h-4 w-4" /></button>
                                </div>
                                <div class="field"><Label>Question</Label><Input bind:value={item.question} /></div>
                                <div class="field" style="margin-top:.625rem"><Label>Answer</Label><Textarea bind:value={item.answer} rows={3} /></div>
                            </div>
                        {/each}
                        <Button variant="outline" size="sm" onclick={addFaq} class="add-btn"><Plus class="h-4 w-4" />Add FAQ Item</Button>
                    </div>
                </section>

                <section id="s-cta" class="form-section">
                    <div class="section-head">
                        <div class="section-icon-wrap"><MousePointerClick class="h-4 w-4" /></div>
                        <div><h2 class="section-title">Call to Action</h2><p class="section-desc">Bottom conversion block on the pricing page.</p></div>
                    </div>
                    <div class="form-grid">
                        <div class="field"><Label>Title</Label><Input bind:value={pricingContent.cta.title} /></div>
                        <div class="field"><Label>Description</Label><Input bind:value={pricingContent.cta.description} /></div>
                        <div class="field"><Label>Primary CTA Label</Label><Input bind:value={pricingContent.cta.primaryCtaLabel} /></div>
                        <div class="field"><Label>Primary CTA URL</Label><Input bind:value={pricingContent.cta.primaryCtaUrl} /></div>
                        <div class="field"><Label>Secondary CTA Label</Label><Input bind:value={pricingContent.cta.secondaryCtaLabel} /></div>
                        <div class="field"><Label>Secondary CTA URL</Label><Input bind:value={pricingContent.cta.secondaryCtaUrl} /></div>
                        <div class="field md2"><Label>Footnote</Label><Input bind:value={pricingContent.cta.footnote} /></div>
                    </div>
                </section>

            </div>
            {/if}
        </main>
    </div>
</div>

<!-- TOASTS -->
<div class="toast-region" aria-live="polite">
    {#each toasts as t (t.id)}
        <div class="toast toast-{t.type}">
            {#if t.type === "success"}<CheckCircle2 class="h-4 w-4 shrink-0" />
            {:else if t.type === "error"}<AlertCircle class="h-4 w-4 shrink-0" />
            {:else}<Globe class="h-4 w-4 shrink-0" />{/if}
            <span>{t.message}</span>
        </div>
    {/each}
</div>

<style>
/* ── Layout ─────────────────────────────────────────────────────────────── */
.builder-root {
    display: flex; flex-direction: column;
    height: calc(100vh - 4rem);
    background: hsl(var(--background));
    overflow: hidden;
}

/* ── Toolbar ─────────────────────────────────────────────────────────────── */
.builder-toolbar {
    display: flex; align-items: center; gap: .75rem;
    padding: 0 1.25rem;
    height: 3.25rem;
    border-bottom: 1px solid hsl(var(--border) / .6);
    background: hsl(var(--background));
    flex-shrink: 0; z-index: 20;
}
.page-tabs { display: flex; gap: .25rem; padding: .25rem; background: hsl(var(--muted) / .6); border-radius: .625rem; }
.page-tab {
    display: flex; align-items: center; gap: .375rem;
    padding: .3125rem .875rem;
    font-size: .8125rem; font-weight: 500;
    border-radius: .4375rem; border: none; cursor: pointer;
    color: hsl(var(--muted-foreground)); background: transparent;
    transition: all .15s;
}
.page-tab:hover { color: hsl(var(--foreground)); }
.page-tab.active { background: hsl(var(--background)); color: hsl(var(--foreground)); box-shadow: 0 1px 3px hsl(0 0% 0% / .08); }

.toolbar-actions { display: flex; align-items: center; gap: .5rem; margin-left: auto; }
.toolbar-btn { font-size: .8125rem; gap: .375rem; height: 2rem; }
.publish-btn {
    font-size: .8125rem; gap: .375rem; height: 2rem;
    background: linear-gradient(135deg, hsl(var(--primary)), hsl(263 70% 55%));
    color: white; border: none;
}
.publish-btn:hover { opacity: .9; }
.unpublish-btn { font-size: .75rem; color: hsl(var(--muted-foreground)); height: 2rem; }
.status-live { background: hsl(142 71% 45% / .12); color: hsl(142 71% 38%); border: 1px solid hsl(142 71% 45% / .25); font-size: .6875rem; }
.status-draft { font-size: .6875rem; }

.save-pill {
    display: flex; align-items: center; gap: .3125rem;
    font-size: .75rem; font-weight: 500; padding: .25rem .625rem;
    border-radius: 999px;
}
.save-pill.saving { color: hsl(var(--muted-foreground)); }
.save-pill.saved  { color: hsl(142 71% 40%); background: hsl(142 71% 45% / .08); }
.save-pill.err    { color: hsl(var(--destructive)); background: hsl(var(--destructive) / .08); }
.save-pill.dirty  { color: hsl(38 92% 45%); }

/* ── Body split ─────────────────────────────────────────────────────────── */
.builder-body { display: flex; flex: 1; overflow: hidden; }

/* ── Left nav ───────────────────────────────────────────────────────────── */
.builder-nav {
    width: 200px; flex-shrink: 0;
    border-right: 1px solid hsl(var(--border) / .5);
    background: hsl(var(--background));
    overflow-y: auto;
    padding: 1rem .5rem;
    display: flex; flex-direction: column; gap: .125rem;
}
.nav-header {
    font-size: .6875rem; font-weight: 700; letter-spacing: .08em;
    text-transform: uppercase; color: hsl(var(--muted-foreground) / .7);
    padding: 0 .625rem .625rem;
}
.nav-item {
    position: relative;
    display: flex; align-items: center; gap: .5rem;
    padding: .4375rem .625rem;
    border-radius: .5rem; border: none; cursor: pointer;
    background: transparent; font-size: .8125rem; font-weight: 500;
    color: hsl(var(--muted-foreground)); text-align: left;
    transition: all .15s ease; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.nav-item:hover { background: hsl(var(--accent)); color: hsl(var(--foreground)); }
.nav-item.active {
    background: hsl(var(--primary) / .08);
    color: hsl(var(--primary));
    font-weight: 600;
}
.nav-icon {
    display: flex; align-items: center; justify-content: center;
    width: 1.625rem; height: 1.625rem;
    border-radius: .375rem; flex-shrink: 0;
    background: hsl(var(--muted) / .6);
}
.nav-item.active .nav-icon { background: hsl(var(--primary) / .12); }
.nav-pip {
    position: absolute; left: 0; top: 50%; transform: translateY(-50%);
    width: 2.5px; height: 1rem; background: hsl(var(--primary));
    border-radius: 0 2px 2px 0;
}

/* ── Canvas ─────────────────────────────────────────────────────────────── */
.builder-canvas { flex: 1; overflow-y: auto; background: hsl(var(--muted) / .3); }
.canvas-inner { max-width: 44rem; margin: 0 auto; padding: 1.5rem 1.5rem 5rem; display: flex; flex-direction: column; gap: .75rem; }

/* ── Form sections ──────────────────────────────────────────────────────── */
.form-section {
    background: hsl(var(--background));
    border: 1px solid hsl(var(--border) / .6);
    border-radius: .875rem;
    overflow: hidden;
    transition: box-shadow .2s;
}
.form-section:hover { box-shadow: 0 2px 20px hsl(var(--primary) / .05); }

.section-head {
    display: flex; align-items: flex-start; gap: .875rem;
    padding: 1.125rem 1.25rem 0;
    margin-bottom: 1rem;
}
.section-head-right { margin-left: auto; display: flex; align-items: center; gap: .5rem; }
.toggle-label { font-size: .75rem; font-weight: 500; color: hsl(var(--muted-foreground)); }

.section-icon-wrap {
    display: flex; align-items: center; justify-content: center;
    width: 2.25rem; height: 2.25rem; flex-shrink: 0;
    border-radius: .625rem; margin-top: .125rem;
    background: hsl(var(--primary) / .08);
    color: hsl(var(--primary));
}
.section-title {
    font-size: 1rem; font-weight: 700;
    color: hsl(var(--foreground));
    display: flex; align-items: center; gap: .5rem;
    margin: 0 0 .125rem;
}
.section-desc { font-size: .75rem; color: hsl(var(--muted-foreground)); margin: 0; line-height: 1.4; }

.count-chip {
    display: inline-flex; align-items: center; justify-content: center;
    background: hsl(var(--muted));
    color: hsl(var(--muted-foreground));
    font-size: .6875rem; font-weight: 600;
    border-radius: 999px; padding: .0625rem .4375rem;
    vertical-align: middle;
}

/* ── Fields ─────────────────────────────────────────────────────────────── */
.form-grid {
    display: grid; grid-template-columns: 1fr 1fr;
    gap: .75rem; padding: 0 1.25rem 1.25rem;
}
.field { display: flex; flex-direction: column; gap: .3125rem; }
.field :global(label) { font-size: .8rem; font-weight: 500; color: hsl(var(--foreground) / .75); }
.field :global(input), .field :global(textarea) { font-size: .875rem; }
.md2 { grid-column: span 2; }

/* ── List editor ────────────────────────────────────────────────────────── */
.list-editor { padding: 0 1.25rem 1.25rem; display: flex; flex-direction: column; gap: .4375rem; }
.list-row {
    display: flex; align-items: center; gap: .5rem;
    padding: .4375rem .625rem;
    background: hsl(var(--muted) / .4);
    border: 1px solid hsl(var(--border) / .5);
    border-radius: .5rem;
}
.list-num { font-size: .6875rem; font-weight: 700; color: hsl(var(--muted-foreground)); width: 1.125rem; flex-shrink: 0; }
.rem-btn {
    display: flex; align-items: center; justify-content: center;
    width: 1.75rem; height: 1.75rem; flex-shrink: 0;
    border-radius: .375rem; border: none; background: transparent;
    color: hsl(var(--muted-foreground)); cursor: pointer; transition: all .15s;
}
.rem-btn:hover { background: hsl(var(--destructive) / .1); color: hsl(var(--destructive)); }
.add-btn { align-self: flex-start; gap: .375rem; }

.item-card {
    background: hsl(var(--muted) / .35);
    border: 1px solid hsl(var(--border) / .5);
    border-radius: .625rem; padding: .875rem;
}
.item-card:not(:last-of-type) { margin-bottom: .25rem; }
.item-card-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: .75rem; }
.item-card-label { font-size: .8125rem; font-weight: 600; color: hsl(var(--muted-foreground)); }
.item-card .form-grid { padding: 0; }

/* ── Skeletons ──────────────────────────────────────────────────────────── */
.skel-section {
    background: hsl(var(--background));
    border: 1px solid hsl(var(--border) / .5);
    border-radius: .875rem; padding: 1.25rem;
}
.skel-bar {
    border-radius: .375rem; display: block;
    background: linear-gradient(90deg, hsl(var(--muted)) 25%, hsl(var(--muted) / .4) 50%, hsl(var(--muted)) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
}
@keyframes shimmer { 0%{background-position:200% 0} 100%{background-position:-200% 0} }

/* ── Toasts ─────────────────────────────────────────────────────────────── */
.toast-region {
    position: fixed; bottom: 1.5rem; right: 1.5rem; z-index: 9999;
    display: flex; flex-direction: column; gap: .5rem; pointer-events: none;
}
.toast {
    display: flex; align-items: center; gap: .625rem;
    padding: .75rem 1.125rem; border-radius: .75rem;
    font-size: .875rem; font-weight: 500;
    backdrop-filter: blur(16px);
    box-shadow: 0 8px 32px hsl(0 0% 0% / .15);
    animation: toastIn .2s cubic-bezier(.34,1.56,.64,1);
    pointer-events: auto;
}
@keyframes toastIn { from { opacity:0; transform:translateY(.75rem) scale(.96); } to { opacity:1; transform:none; } }
.toast-success { background: hsl(142 71% 14% / .92); color: hsl(142 71% 72%); border: 1px solid hsl(142 71% 25% / .4); }
.toast-error   { background: hsl(0 80% 14% / .92);  color: hsl(0 80% 72%);  border: 1px solid hsl(0 80% 25% / .4); }
.toast-info    { background: hsl(220 80% 14% / .92); color: hsl(220 80% 72%); border: 1px solid hsl(220 80% 25% / .4); }
</style>
