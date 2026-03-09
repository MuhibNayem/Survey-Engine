<script lang="ts">
  import SimpleCodeBlock from '$lib/components/ApiDocs/SimpleCodeBlock.svelte';
  import { 
    Terminal, Key, Database, Zap, UserCircle, BarChart3, ShieldCheck, ShoppingBag, 
    History, Info, AlertCircle, CheckCircle2, Library, BookOpen, Shield, Lock, 
    CreditCard, Layout, Globe, Activity, ListOrdered, FileJson, GraduationCap, ChevronRight
  } from "lucide-svelte";
  import { Button } from "$lib/components/ui/button";
  import { onMount } from 'svelte';

  let activeTab = $state('typescript');

  const languages = [
    { id: 'typescript', name: 'TypeScript', icon: 'TS' },
    { id: 'java', name: 'Java', icon: 'Java' },
    { id: 'go', name: 'Go', icon: 'Go' },
    { id: 'python', name: 'Python', icon: 'Py' }
  ];

  // We rely on the parent DocsSidebar now.
</script>

<div class="bg-background min-h-screen">
  <!-- 1. CENTER: DESCRIPTION & INLINE CODE (FLUID) -->
  <div class="px-8 md:px-12 py-12 space-y-32">
    <!-- Global Language Selector (Sticky) -->
    <div class="sticky top-0 z-50 py-4 bg-background/80 backdrop-blur-md border-b border-border/50 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <span class="text-xs font-bold text-muted-foreground uppercase tracking-widest">Select Language:</span>
        <div class="flex items-center gap-1 p-1 bg-muted rounded-xl border border-border">
          {#each languages as lang}
            <button
              onclick={() => activeTab = lang.id}
              class="px-4 py-2 text-xs font-bold rounded-lg transition-all {activeTab === lang.id ? 'bg-primary text-primary-foreground shadow-md' : 'text-muted-foreground hover:text-foreground hover:bg-background'}"
            >
              {lang.name}
            </button>
          {/each}
        </div>
      </div>
      <div class="flex items-center gap-2 text-[10px] font-mono text-muted-foreground uppercase tracking-tighter max-md:hidden">
        <span class="w-2 h-2 rounded-full bg-green-500"></span>
        Unified Code Sync
      </div>
    </div>

    <!-- Hero Section -->
    <header id="foundations" class="space-y-6 border-b border-border/50 pb-16">
      <h1 class="text-6xl font-black tracking-tighter leading-[0.8] mb-8">
        Enterprise <br/>
        <span class="text-primary">SDK Reference</span>
      </h1>
      <p class="text-xl text-muted-foreground leading-relaxed max-w-2xl font-medium">
        Integrate full-scale survey orchestration, advanced category-based analytics, and multi-tenant governance into your ecosystem with native type safety.
      </p>
      <div class="flex items-center gap-4">
        <Button variant="outline" class="rounded-full h-11 px-8 font-bold shadow-sm">View API Spec</Button>
        <Button variant="ghost" class="rounded-full h-11 px-8 font-bold group text-muted-foreground hover:text-primary">
          GitHub Repos
          <ChevronRight class="ml-1 w-4 h-4 group-hover:translate-x-1 transition-transform" />
        </Button>
      </div>
    </header>

    <!-- Foundations Details -->
    <section class="space-y-12">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Terminal class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Foundations</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Survey Engine SDKs are built for performance and type safety. We provide official clients for modern stacks, 
          handling complex token rotation, CSRF protection, and serialization internally.
        </p>
      </div>

      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-8">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">1. Installation</h3>
            <p class="text-muted-foreground leading-relaxed">
              Add the core SDK package to your project. Our packages include built-in TypeScript types, 
              Pydantic models for Python, and native POJOs for Java.
            </p>
          </div>
          
          <div class="space-y-4 mt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">2. Client Initialization</h3>
            <p class="text-muted-foreground leading-relaxed">
              Initialize the `ApiClient` with your tenant's base path. You can provide credentials during initialization 
              or lazily after performing a login request.
            </p>
          </div>
        </div>

        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
             <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Setup</span>
             <div class="flex gap-1.5">
               <div class="w-2.5 h-2.5 rounded-full bg-red-500/50"></div>
               <div class="w-2.5 h-2.5 rounded-full bg-yellow-500/50"></div>
               <div class="w-2.5 h-2.5 rounded-full bg-green-500/50"></div>
             </div>
          </div>
          {#if activeTab === 'typescript'}
            <div class="space-y-6">
              <SimpleCodeBlock language="bash" code={`npm install @survey-engine/sdk --save`} />
              <SimpleCodeBlock language="typescript" code={`import { Configuration, ApiClient } from '@survey-engine/sdk';

const config = new Configuration({
    basePath: "https://api.yourdomain.com",
    accessToken: "YOUR_JWT_TOKEN"
});`} />
            </div>
          {:else if activeTab === 'java'}
            <div class="space-y-6">
              <SimpleCodeBlock language="bash" code={`<!-- Maven -->
<dependency>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-java-client</artifactId>
  <version>1.1.0</version>
</dependency>`} />
              <SimpleCodeBlock language="java" code={`import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

ApiClient client = Configuration.getDefaultApiClient();
client.setBasePath("https://api.yourdomain.com");`} />
            </div>
          {:else if activeTab === 'go'}
            <div class="space-y-6">
              <SimpleCodeBlock language="bash" code={`go get github.com/your-org/survey-engine-go`} />
              <SimpleCodeBlock language="go" code={`import openapi "github.com/your-org/survey-engine-go"

cfg := openapi.NewConfiguration()
cfg.Host = "api.yourdomain.com"
client := openapi.NewAPIClient(cfg)`} />
            </div>
          {:else if activeTab === 'python'}
            <div class="space-y-6">
              <SimpleCodeBlock language="bash" code={`pip install survey-engine-sdk`} />
              <SimpleCodeBlock language="python" code={`import openapi_client

conf = openapi_client.Configuration(host="https://api.yourdomain.com")
client = openapi_client.ApiClient(conf)`} />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Identity & Access Section -->
    <section id="id-access" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Key class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Admin Identity</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Securely authenticate platform administrators. We support both <strong>Headless Token Mode</strong> for backend services
          and <strong>Secure Cookie Mode</strong> for browser-based administrative tools.
        </p>
      </div>

      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Registration & Onboarding</h3>
            <p class="text-muted-foreground leading-relaxed">
              Bootstrap new tenant administrators. This endpoint provisions initial tenant records and issues a trial subscription.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Headless Login</h3>
            <p class="text-muted-foreground leading-relaxed">
              Exchange credentials for access and refresh tokens. Use these tokens in subsequent `Configuration` updates.
            </p>
          </div>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Identity</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { AdminAuthenticationApi } from '@survey-engine/sdk';

const authApi = new AdminAuthenticationApi(config);
await authApi.loginAdminTokenMode({
    email: "admin@tenant.com",
    password: "pass"
});`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`AdminAuthenticationApi api = new AdminAuthenticationApi(client);
AuthResponse res = api.loginAdminTokenMode(new LoginRequest()
    .email("admin@tenant.com").password("pass"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`res, _, _ := client.AdminAuthenticationAPI.LoginAdminTokenMode(ctx).LoginRequest(req).Execute()`} />
          {:else if activeTab === 'python'}
             <SimpleCodeBlock language="python" code={`auth_api = openapi_client.AdminAuthenticationApi(client)
res = auth_api.login_admin_token_mode(openapi_client.LoginRequest(
    email="admin@tenant.com", password="pass"))`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Auth Profiles Section -->
    <section id="auth-profiles" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Lock class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Auth Profiles</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Define security requirements for respondent sessions. Configure multi-factor requirements,
          session timeouts, and OIDC provider mappings.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Global Policies</h3>
            <p class="text-muted-foreground leading-relaxed">
              Retrieve or update the respondent security policy for your tenant.
            </p>
          </div>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Profiles</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { AuthProfilesApi } from '@survey-engine/sdk';

const profileApi = new AuthProfilesApi(config);
const policy = await profileApi.getAuthProfiles();
console.log("MFA Required:", policy.mfaEnabled);`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`AuthProfilesApi api = new AuthProfilesApi(client);
RespondentAuthProfileResponse profile = api.getAuthProfiles();`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`profile, _, _ := client.AuthProfilesAPI.GetAuthProfiles(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api = openapi_client.AuthProfilesApi(client)
profile = api.get_auth_profiles()`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Plans Section -->
    <section id="plans-catalog" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Layout class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Plans & Catalog</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Explore available platform plans and their commercial limits. Super admins can mutate this catalog to adjust
          pricing and quota globally.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Listing Active Plans</h3>
            <p class="text-muted-foreground leading-relaxed">
              Get a canonical list of plans with their campaign, response, and user limits.
            </p>
          </div>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Plans</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { PlansApi } from '@survey-engine/sdk';

const plansApi = new PlansApi(config);
const plans = await plansApi.listActivePlans();`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`PlansApi api = new PlansApi(client);
List<PlanDefinitionResponse> plans = api.listActivePlans();`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`plans, _, _ := client.PlansAPI.ListActivePlans(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api = openapi_client.PlansApi(client)
plans = api.list_active_plans()`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Subscriptions Section -->
    <section id="subscriptions" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <CreditCard class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Subscriptions</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Manage your current plan and billing cycles. Perform plan upgrades and monitor usage against quotas.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Self-Service Upgrades</h3>
            <p class="text-muted-foreground leading-relaxed">
              Initiate a checkout flow for a higher-tier plan. Quotas are applied immediately upon successful payment.
            </p>
          </div>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Billing</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { SubscriptionsApi } from '@survey-engine/sdk';

const billingApi = new SubscriptionsApi(config);
await billingApi.subscribe({ plan: 'PRO' });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`SubscriptionsApi api = new SubscriptionsApi(client);
api.subscribe(new SubscribeRequest().plan("PRO"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.SubscriptionsAPI.Subscribe(ctx).Plan("PRO").Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api = openapi_client.SubscriptionsApi(client)
api.subscribe(openapi_client.SubscribeRequest(plan='PRO'))`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Question Bank Section -->
    <section id="question-bank" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Database class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Question Bank</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The question bank is the foundation of reusable survey content. Teams can add standardized question assets
          once and reuse them across many surveys.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Standardized Assets</h3>
          <p class="text-muted-foreground leading-relaxed">
            Create questions with complex validation rules, scale definitions, and multi-language labels.
          </p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Questions</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { QuestionsApi } from '@survey-engine/sdk';
const qApi = new QuestionsApi(config);
await qApi.createQuestion({ text: "Satisfied?", type: "SCALE" });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`QuestionsApi api = new QuestionsApi(client);
api.createQuestion(new QuestionRequest().text("Satisfied?"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.QuestionsAPI.CreateQuestion(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.create_question(openapi_client.QuestionRequest(text="Satisfied?"))`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section id="categories" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <ListOrdered class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Categories</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Organize questions into logical groups. Categories power the scoring engine and provide segmented 
          insights during analytics.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Segmented Content</h3>
          <p class="text-muted-foreground leading-relaxed">
            Group questions by topic (e.g., "Support Quality", "Product Fit") to enable granular reporting.
          </p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Categories</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { CategoriesApi } from '@survey-engine/sdk';
const catApi = new CategoriesApi(config);
await catApi.createCategory({ name: "Support", questionIds: ["Q1"] });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`CategoriesApi api = new CategoriesApi(client);
api.createCategory(new CategoryRequest().name("Support"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.CategoriesAPI.CreateCategory(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.create_category(name="Support")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Survey Lifecycle Section -->
    <section id="surveys" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <History class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Survey Lifecycle</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Design multi-page surveys by composing questions into pages. Manage state transitions using the 
          built-in lifecycle state machine.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Composition</h3>
            <p class="text-muted-foreground leading-relaxed">Define survey structure with pages and nested questions.</p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Lifecycle Actions</h3>
            <p class="text-muted-foreground leading-relaxed italic">Transitions are validated: a survey must have at least one question to be PUBLISHED.</p>
          </div>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Surveys</span>
          </div>
          {#if activeTab === 'typescript'}
            <div class="space-y-6">
              <SimpleCodeBlock language="typescript" code={`import { SurveysApi } from '@survey-engine/sdk';
const surveyApi = new SurveysApi(config);
await surveyApi.createSurvey({ title: "Q1 Feedback", pages: [] });
await surveyApi.transitionSurveyLifecycle("S1", { action: "PUBLISH" });`} />
            </div>
          {:else if activeTab === 'java'}
            <div class="space-y-6">
              <SimpleCodeBlock language="java" code={`SurveysApi api = new SurveysApi(client);
api.createSurvey(new SurveyRequest().title("Q1 Feedback"));
api.transitionSurveyLifecycle("S1", new LifecycleTransitionRequest().action("PUBLISH"));`} />
            </div>
          {:else if activeTab === 'go'}
            <div class="space-y-6">
              <SimpleCodeBlock language="go" code={`client.SurveysAPI.CreateSurvey(ctx).Execute()
client.SurveysAPI.TransitionSurveyLifecycle(ctx, "S1").Execute()`} />
            </div>
          {:else if activeTab === 'python'}
            <div class="space-y-6">
              <SimpleCodeBlock language="python" code={`api.create_survey(title="Q1 Feedback")
api.transition_survey_lifecycle("S1", action="PUBLISH")`} />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Campaigns Section -->
    <section id="campaigns" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Zap class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Campaigns</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Launch surveys as campaigns. Campaigns manage the distribution window, respondent eligibility, 
          and data anonymity settings.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Activation</h3>
          <p class="text-muted-foreground leading-relaxed">Connect a published survey to a campaign and set distribution flags.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Campaigns</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { CampaignsApi } from '@survey-engine/sdk';
const campaignApi = new CampaignsApi(config);
await campaignApi.createCampaign({ surveyId: "S1", name: "Outreach" });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`CampaignsApi api = new CampaignsApi(client);
api.createCampaign(new CampaignRequest().surveyId("S1"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.CampaignsAPI.CreateCampaign(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.create_campaign(survey_id="S1")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Respondent OIDC Section -->
    <section id="respondent" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Globe class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Respondent OIDC</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          For private campaigns, initiate OpenID Connect flows to authenticate respondents against your 
          identity provider.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Authorize URL</h3>
          <p class="text-muted-foreground leading-relaxed">Retrieve the OIDC authorize URL for a specific private campaign.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} OIDC</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { OIDCRespondentFlowApi } from '@survey-engine/sdk';
const oidcApi = new OIDCRespondentFlowApi(config);
const { authUrl } = await oidcApi.startOidcFlow("CAMP1");`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`api.startOidcFlow("CAMP1");`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.OIDCRespondentFlowAPI.StartOidcFlow(ctx, "CAMP1").Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.start_oidc_flow("CAMP1")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Response Submissions Section -->
    <section id="submissions" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <UserCircle class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Response Submissions</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Collect data from your users. We support multi-part answer payloads with built-in validation 
          against the original survey schema.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Pushing Answers</h3>
          <p class="text-muted-foreground leading-relaxed">Submit a flat array of question ID/value pairs for a campaign session.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Submissions</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { ResponsesApi } from '@survey-engine/sdk';
const respApi = new ResponsesApi(config);
await respApi.submitResponse({ campaignId: "C1", answers: [] });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`ResponsesApi api = new ResponsesApi(client);
api.submitResponse(new ResponseSubmissionRequest().campaignId("C1"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.ResponsesAPI.SubmitResponse(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.submit_response(campaign_id="C1")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Analytics Section -->
    <section id="analytics" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <BarChart3 class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Analytics</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Extract insights from your survey data. Retrieve campaign-level aggregates, respondent distributions, 
          and metadata breakdowns.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Campaign Intelligence</h3>
          <p class="text-muted-foreground leading-relaxed">Retrieve numerical summaries and question-level frequency counts.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Analytics</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`const analytics = await respApi.getCampaignAnalytics("C1");
console.log("Response Rate:", analytics.responseRate);`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`CampaignAnalyticsResponse res = api.getCampaignAnalytics("C1");`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.ResponsesAPI.GetCampaignAnalytics(ctx, "C1").Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`res = api.get_campaign_analytics("C1")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Scoring Engine Section -->
    <section id="scoring" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Activity class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Scoring Engine</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Perform complex numerical analysis on responses. Configure weight profiles to calculate 
          weighted scores based on respondent inputs across multiple questions.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Weight Profiling</h3>
          <p class="text-muted-foreground leading-relaxed">Map questions to coefficients to compute real-time scores for every response.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Scoring</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { ScoringApi } from '@survey-engine/sdk';
const scoreApi = new ScoringApi(config);
await scoreApi.createWeightProfile({ name: "Global NPS", weights: [...] });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`ScoringApi api = new ScoringApi(client);
api.createWeightProfile(new WeightProfileRequest().name("NPS"));`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.ScoringAPI.CreateWeightProfile(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.create_weight_profile(name="NPS")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Platform Governance Section -->
    <section id="governance" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <Shield class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Platform Governance</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed italic">Super-Admin Visibility</p>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Global lifecycle management for all tenants. Monitor platform-wide health, manage tenant activation states, 
          and perform security impersonation.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Operations</h3>
          <p class="text-muted-foreground leading-relaxed">Manage tenant suspension, activation, and trial overrides.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Governance</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { SuperAdminApi } from '@survey-engine/sdk';
const superApi = new SuperAdminApi(config);
await superApi.suspendTenant("TENANT_ID");`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`api.suspendTenant("TENANT_ID");`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.SuperAdminAPI.SuspendTenant(ctx, "ID").Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.suspend_tenant("ID")`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Compliance Logs Section -->
    <section id="audit" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary">
          <ShieldCheck class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Compliance Logs</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Maintain a full audit trail of administrative activities. Track every configuration change 
          and data export for compliance and security forensics.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-4">
          <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">Forensics</h3>
          <p class="text-muted-foreground leading-relaxed">Retrieve paginated audit logs for any tenant or global event.</p>
        </div>
        <div class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit">
          <div class="mb-4 flex items-center justify-between border-b border-white/10 pb-4">
            <span class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest">{activeTab} Compliance</span>
          </div>
          {#if activeTab === 'typescript'}
            <SimpleCodeBlock language="typescript" code={`import { AuditLogsApi } from '@survey-engine/sdk';
const auditApi = new AuditLogsApi(config);
const logs = await auditApi.getTenantAuditLogs({ page: 0 });`} />
          {:else if activeTab === 'java'}
            <SimpleCodeBlock language="java" code={`AuditLogsApi api = new AuditLogsApi(client);
api.getTenantAuditLogs(0, 20, null);`} />
          {:else if activeTab === 'go'}
            <SimpleCodeBlock language="go" code={`client.AuditLogsAPI.GetTenantAuditLogs(ctx).Execute()`} />
          {:else if activeTab === 'python'}
            <SimpleCodeBlock language="python" code={`api.get_tenant_audit_logs(page=0)`} />
          {/if}
        </div>
      </div>
    </section>

    <!-- Best Practices Section -->
    <section id="error-handling" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
         <div class="flex items-center gap-3 text-primary text-green-500">
          <GraduationCap class="w-8 h-8" />
          <h2 class="text-3xl font-bold tracking-tight">Best Practices</h2>
        </div>
        <p class="text-muted-foreground leading-relaxed">
          Guidelines for building resilient integrations with the Survey Engine ecosystem.
        </p>
      </div>
      <div class="grid gap-8">
        <div class="p-6 rounded-2xl bg-muted/30 border border-border/50">
          <h4 class="font-bold mb-2 flex items-center gap-2">
            <AlertCircle class="w-4 h-4 text-primary" />
            Resilient Error Handling
          </h4>
          <p class="text-xs text-muted-foreground italic">Always wrap calls in try-catch/if-err. Use structured exceptions to handle 429 Rate Limits and 400 Validation errors gracefully.</p>
        </div>
        <div class="p-6 rounded-2xl bg-muted/30 border border-border/50">
          <h4 class="font-bold mb-2 flex items-center gap-2">
            <CheckCircle2 class="w-4 h-4 text-green-500" />
            Type Safety
          </h4>
          <p class="text-xs text-muted-foreground italic">Avoid raw maps/dictionaries. Use generated DTOs to benefit from IDE autocomplete and early contract change detection.</p>
        </div>
      </div>
    </section>

    <footer class="pt-20 border-t border-border mt-40">
       <h2 class="text-4xl font-black tracking-tight mb-4">Enterprise Grade by Default</h2>
       <p class="text-muted-foreground max-w-xl">
         Survey Engine SDKs are maintained for production scale. For further assistance, contact your 
         dedicated account manager or visit our Developer Forum.
       </p>
    </footer>
  </div>
</div>

<style>
  .scrollbar-hide::-webkit-scrollbar {
    display: none;
  }
</style>
