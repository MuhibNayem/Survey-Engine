<script lang="ts">
  import SimpleCodeBlock from "$lib/components/ApiDocs/SimpleCodeBlock.svelte";
  import {
    Terminal,
    Key,
    Database,
    Zap,
    UserCircle,
    BarChart3,
    ShieldCheck,
    ShoppingBag,
    History,
    Info,
    AlertCircle,
    CheckCircle2,
    Library,
    BookOpen,
    Shield,
    Lock,
    CreditCard,
    Layout,
    Globe,
    Activity,
    ListOrdered,
    FileJson,
    GraduationCap,
    ChevronRight,
    HelpCircle,
    Tag,
    FileText,
    Send,
    MousePointerClick,
    PieChart,
    Target,
    ShieldAlert,
  } from "lucide-svelte";
  import { Button } from "$lib/components/ui/button";
  import { onMount } from "svelte";

  let activeTab = $state("typescript");

  const languages = [
    { id: "typescript", name: "TypeScript", icon: "TS" },
    { id: "java", name: "Java", icon: "Java" },
    { id: "go", name: "Go", icon: "Go" },
    { id: "python", name: "Python", icon: "Py" },
  ];

  // We rely on the parent DocsSidebar now.
</script>

<div class="bg-background min-h-screen">
  <!-- 1. CENTER: DESCRIPTION & INLINE CODE (FLUID) -->
  <div class="px-8 md:px-12 py-12 space-y-32">
    <!-- Global Language Selector (Sticky) -->
    <div
      class="sticky top-0 z-50 py-4 bg-background/80 backdrop-blur-md border-b border-border/50 flex items-center justify-between"
    >
      <div class="flex items-center gap-4">
        <span
          class="text-xs font-bold text-muted-foreground uppercase tracking-widest"
          >Select Language:</span
        >
        <div
          class="flex items-center gap-1 p-1 bg-muted rounded-xl border border-border"
        >
          {#each languages as lang}
            <button
              onclick={() => (activeTab = lang.id)}
              class="px-4 py-2 text-xs font-bold rounded-lg transition-all {activeTab ===
              lang.id
                ? 'bg-primary text-primary-foreground shadow-md'
                : 'text-muted-foreground hover:text-foreground hover:bg-background'}"
            >
              {lang.name}
            </button>
          {/each}
        </div>
      </div>
    </div>

    <!-- Hero Section -->
    <header id="foundations" class="space-y-6 border-b border-border/50 pb-16">
      <h1 class="text-6xl font-black tracking-tighter leading-[0.8] mb-8">
        Enterprise <br />
        <span class="text-primary">SDK Reference</span>
      </h1>
      <p
        class="text-xl text-muted-foreground leading-relaxed max-w-2xl font-medium"
      >
        Integrate full-scale survey orchestration, advanced category-based
        analytics, and multi-tenant governance into your ecosystem with native
        type safety.
      </p>
      <div class="flex items-center gap-4">
        <Button
          variant="outline"
          class="rounded-full h-11 px-8 font-bold shadow-sm"
          href="/docs/api"
          >View API Spec</Button
        >
        <Button
          variant="ghost"
          class="rounded-full h-11 px-8 font-bold group text-muted-foreground hover:text-primary"
          href="https://github.com/MuhibNayem/Survey-Engine"
          target="_blank"
          rel="noreferrer"
        >
          GitHub Repos
          <ChevronRight
            class="ml-1 w-4 h-4 group-hover:translate-x-1 transition-transform"
          />
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
          Survey Engine SDKs are built for performance and type safety. We
          provide official clients for modern stacks, handling complex token
          rotation, CSRF protection, and serialization internally.
        </p>
      </div>

      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-8">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Installation
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Add the core SDK package to your project. Our packages include
              built-in TypeScript types, Pydantic models for Python, and native
              POJOs for Java.
            </p>
          </div>

          <div class="space-y-4 mt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Client Initialization
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Initialize the `ApiClient` with your tenant's base path. You can
              provide credentials during initialization or lazily after
              performing a login request.
            </p>
          </div>
        </div>

        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Setup</span
            >
            <div class="flex gap-1.5">
              <div class="w-2.5 h-2.5 rounded-full bg-red-500/50"></div>
              <div class="w-2.5 h-2.5 rounded-full bg-yellow-500/50"></div>
              <div class="w-2.5 h-2.5 rounded-full bg-green-500/50"></div>
            </div>
          </div>
          {#if activeTab === "typescript"}
            <div class="flex flex-col gap-6">
              <SimpleCodeBlock
                language="bash"
                code={`npm install @survey-engine/sdk --save`}
              />
              <SimpleCodeBlock
                language="typescript"
                code={`import { Configuration, ApiClient } from '@survey-engine/sdk';

const config = new Configuration({
    basePath: "https://api.yourdomain.com",
    accessToken: "YOUR_JWT_TOKEN"
});
const client = new ApiClient(config);`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="flex flex-col gap-6">
              <SimpleCodeBlock
                language="bash"
                code={`<!-- Maven -->
<dependency>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-java-client</artifactId>
  <version>1.1.0</version>
</dependency>`}
              />
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

ApiClient client = Configuration.getDefaultApiClient();
client.setBasePath("https://api.yourdomain.com");
client.setAccessToken("YOUR_JWT_TOKEN");`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="bash"
                code={`go get github.com/muhibnayem/survey-engine-sdk-go`}
              />
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

cfg := sdk.NewConfiguration()
cfg.Host = "api.yourdomain.com"
client := sdk.NewAPIClient(cfg)`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="bash"
                code={`pip install openapi-client`}
              />
              <SimpleCodeBlock
                language="python"
                code={`import openapi_client

conf = openapi_client.Configuration(host="https://api.yourdomain.com")
conf.access_token = "YOUR_JWT_TOKEN"
client = openapi_client.ApiClient(conf)`}
              />
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
          The Identity service manages the full lifecycle of tenant
          administrators. Our SDKs support dual-mode authentication:
        </p>
        <div class="grid md:grid-cols-2 gap-6">
          <div class="p-6 rounded-2xl bg-muted/30 border border-border/50">
            <h4 class="font-bold mb-2 flex items-center gap-2">
              <Globe class="w-4 h-4 text-primary" />
              Secure Cookie Mode
            </h4>
            <p class="text-xs text-muted-foreground">
              Standard for browser-based dashboards. Uses HttpOnly cookies,
              refresh-token rotation, and CSRF protection.
            </p>
          </div>
          <div class="p-6 rounded-2xl bg-muted/30 border border-border/50">
            <h4 class="font-bold mb-2 flex items-center gap-2">
              <Zap class="w-4 h-4 text-yellow-500" />
              Headless Token Mode
            </h4>
            <p class="text-xs text-muted-foreground">
              Standard for API integrations and CLI tools. Returns explicit JWT
              payloads in response bodies.
            </p>
          </div>
        </div>
      </div>

      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Registration & Onboarding
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Provision a new admin account. This bootstraps the tenant context
              and entitles a trial subscription automatically.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Authentication Flows
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Log in administrators via email/password. In Cookie Mode, tokens
              are set via headers; in Token Mode, they are returned in the
              result.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              3. Session Management
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Handle session renewal, CSRF bootstrapping (for browsers), and
              explicit logout.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Identity API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { AdminAuthenticationApi, RegisterRequest, LoginRequest } from '@survey-engine/sdk';

const authApi = new AdminAuthenticationApi(config);

// Registration
const admin = await authApi.registerAdmin({
  registerRequest: { email: "owner@tenant.com", password: "...", tenantName: "Acme" }
});

// Login (Token Mode)
const tokens = await authApi.loginAdminTokenMode({
  loginRequest: { email: "...", password: "..." }
});

// Session Lifecycle
await authApi.refreshAdminToken();
await authApi.getCsrfToken();
await authApi.logoutAdmin();`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.AdminAuthenticationApi;
import org.openapitools.client.model.RegisterRequest;
import org.openapitools.client.model.LoginRequest;
import org.openapitools.client.model.AuthUserResponse;
import org.openapitools.client.model.AuthResponse;

AdminAuthenticationApi api = new AdminAuthenticationApi(client);

// Registration
AuthUserResponse admin = api.registerAdmin(new RegisterRequest()
    .email("owner@tenant.com").password("...").tenantName("Acme"));

// Login (Token Mode)
AuthResponse tokens = api.loginAdminTokenMode(new LoginRequest()
    .email("...").password("..."));

// Session Lifecycle
api.refreshAdminToken();
api.getCsrfToken();
api.logoutAdmin();`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// Registration
admin, _, _ := client.AdminAuthenticationAPI.RegisterAdmin(ctx).
    RegisterRequest(sdk.RegisterRequest{Email: "owner@tenant.com"}).Execute()

// Login (Token Mode)
tokens, _, _ := client.AdminAuthenticationAPI.LoginAdminTokenMode(ctx).
    LoginRequest(sdk.LoginRequest{Email: "..."}).Execute()

// Session Lifecycle
client.AdminAuthenticationAPI.RefreshAdminToken(ctx).Execute()
client.AdminAuthenticationAPI.LogoutAdmin(ctx).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.admin_authentication_api import AdminAuthenticationApi
from openapi_client.models.register_request import RegisterRequest
from openapi_client.models.login_request import LoginRequest

api = AdminAuthenticationApi(client)

# Registration
admin = api.register_admin(register_request=RegisterRequest(
    email="owner@tenant.com", password="...", tenant_name="Acme"))

# Login (Token Mode)
tokens = api.login_admin_token_mode(login_request=LoginRequest(
    email="...", password="..."))

# Session Lifecycle
api.refresh_admin_token()
api.logout_admin()`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Auth Profiles Section -->
    <section
      id="auth-profiles"
      class="space-y-12 pt-16 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Lock class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Auth Profiles</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The Auth Profiles service enables tenant-level responder trust
          policies. Securely configure OIDC providers, manage signing keys, and
          audit all security-sensitive configuration changes.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Profile Management
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Provision and manage auth profiles. Each profile defines how
              respondent identities are verified, including claim mappings and
              default security scopes.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Security Lifecycle
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Perform signing key rotation and monitor configuration audit logs.
              Key rotation is essential for maintaining cryptographic hygiene
              and incident readiness.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              3. IdP Templates
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Accelerate onboarding with pre-defined templates for popular
              identity providers (Okta, Auth0, Azure AD). Templates include
              recommended scopes and mapping defaults.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Profiles API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { AuthProfilesApi, AuthProfileRequest } from '@survey-engine/sdk';

const profileApi = new AuthProfilesApi(config);

// CRUD
const profile = await profileApi.getAuthProfileByTenant({ tenantId: "..." });
await profileApi.updateAuthProfile({ id: "...", authProfileRequest: { ... } });

// Key & Audit
await profileApi.rotateAuthProfileKey({ id: "..." });
const logs = await profileApi.getAuthProfileAudit({ id: "..." });

// Templates
const templates = await profileApi.listProviderTemplates();`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.AuthProfilesApi;
import org.openapitools.client.model.AuthProfileRequest;
import org.openapitools.client.model.AuthProfileResponse;
import org.openapitools.client.model.AuthConfigAudit;
import org.openapitools.client.model.ProviderTemplateResponse;

AuthProfilesApi api = new AuthProfilesApi(client);

// CRUD
AuthProfileResponse profile = api.getAuthProfileByTenant("...");
api.updateAuthProfile(UUID.fromString("..."), new AuthProfileRequest());

// Key & Audit
api.rotateAuthProfileKey(UUID.fromString("..."));
List<AuthConfigAudit> logs = api.getAuthProfileAudit(UUID.fromString("..."));

// Templates
List<ProviderTemplateResponse> templates = api.listProviderTemplates();`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// CRUD
profile, _, _ := client.AuthProfilesAPI.GetAuthProfileByTenant(ctx, "...").Execute()
client.AuthProfilesAPI.UpdateAuthProfile(ctx, "...").
    AuthProfileRequest(sdk.AuthProfileRequest{...}).Execute()

// Key & Audit
client.AuthProfilesAPI.RotateAuthProfileKey(ctx, "...").Execute()
logs, _, _ := client.AuthProfilesAPI.GetAuthProfileAudit(ctx, "...").Execute()

// Templates
templates, _, _ := client.AuthProfilesAPI.ListProviderTemplates(ctx).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.auth_profiles_api import AuthProfilesApi
from openapi_client.models.auth_profile_request import AuthProfileRequest

api = AuthProfilesApi(client)

# CRUD
profile = api.get_auth_profile_by_tenant(tenant_id="...")
api.update_auth_profile(id="...", auth_profile_request=AuthProfileRequest(...))

# Key & Audit
api.rotate_auth_profile_key(id="...")
logs = api.get_auth_profile_audit(id="...")

# Templates
templates = api.list_provider_templates()`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- OIDC Respondent Flow Section -->
    <section id="oidc-flow" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Zap class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            OIDC Respondent Flow
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Integrate external OIDC identity flows for respondents. Our SDKs
          handle the initiation and callback processing to bridge external trust
          into platform-native responder sessions.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Initiation
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Start an OIDC flow. The SDK returns the authorization URL and
              state/nonce context generated by the service.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Callback Completion
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Complete the flow by exchanging the authorization code. On
              success, the service issues a one-time respondent access code.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} OIDC API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { OIDCRespondentFlowApi, OidcStartRequest } from '@survey-engine/sdk';

const oidcApi = new OIDCRespondentFlowApi(config);

// Start Flow
const start = await oidcApi.startRespondentOidc({
  oidcStartRequest: { campaignId: "...", tenantId: "...", redirectUri: "..." }
});

// Complete Callback
const result = await oidcApi.completeRespondentOidc({
  state: "...", code: "..."
});

console.log("Access Code:", result.accessCode);`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.OIDCRespondentFlowApi;
import org.openapitools.client.model.OidcStartRequest;
import org.openapitools.client.model.OidcStartResponse;
import org.openapitools.client.model.OidcCallbackResponse;

OIDCRespondentFlowApi api = new OIDCRespondentFlowApi(client);

// Start Flow
OidcStartResponse start = api.startRespondentOidc(new OidcStartRequest()
    .campaignId("...").tenantId("...").redirectUri("..."));

// Complete Callback
OidcCallbackResponse res = api.completeRespondentOidc("state_val", "code_val");
System.out.println("Access Code: " + res.getAccessCode());`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// Start Flow
start, _, _ := client.OIDCRespondentFlowAPI.StartRespondentOidc(ctx).
    OidcStartRequest(sdk.OidcStartRequest{CampaignId: "..."}).Execute()

// Complete Callback
res, _, _ := client.OIDCRespondentFlowAPI.CompleteRespondentOidc(ctx).
    State("...").Code("...").Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.oidc_respondent_flow_api import OIDCRespondentFlowApi
from openapi_client.models.oidc_start_request import OidcStartRequest

api = OIDCRespondentFlowApi(client)

# Start Flow
start = api.start_respondent_oidc(oidc_start_request=OidcStartRequest(
    campaign_id="...", tenant_id="...", redirect_uri="..."))

# Complete Callback
res = api.complete_respondent_oidc(state="...", code="...")`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Plans & Catalog Section -->
    <section
      id="plans-catalog"
      class="space-y-12 pt-16 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Layout class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Plans & Catalog</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The Plan Catalog defines the commercial boundaries of the platform.
          Super administrators govern global offerings, while tenants discover
          plans that align with their operational needs.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Discovery
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Explore available plans. Each plan definition includes quota
              limits for campaigns, responses, and users.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Governance (Super Admin)
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Mutate the global catalog. The `upsertPlan` operation allows
              platform operators to adjust pricing or limits globally.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Plans API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { PlanCatalogApi, PlanDefinitionRequest } from '@survey-engine/sdk';

const planApi = new PlanCatalogApi(config);

// List Plans
const plans = await planApi.listPlans();

// Upsert Plan (Super Admin)
await planApi.upsertPlan({
  planDefinitionRequest: { code: "ENTERPRISE", maxCampaigns: 50, ... }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.PlanCatalogApi;
import org.openapitools.client.model.PlanDefinitionRequest;
import org.openapitools.client.model.PlanDefinitionResponse;

PlanCatalogApi api = new PlanCatalogApi(client);

// List Plans
List<PlanDefinitionResponse> plans = api.listPlans();

// Upsert Plan (Super Admin)
api.upsertPlan(new PlanDefinitionRequest().code("PRO").maxCampaigns(10));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// List Plans
plans, _, _ := client.PlanCatalogAPI.ListPlans(ctx).Execute()

// Upsert Plan (Super Admin)
client.PlanCatalogAPI.UpsertPlan(ctx).
    PlanDefinitionRequest(sdk.PlanDefinitionRequest{Code: "PRO"}).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.plan_catalog_api import PlanCatalogApi
from openapi_client.models.plan_definition_request import PlanDefinitionRequest

api = PlanCatalogApi(client)

# List Plans
plans = api.list_plans()

# Upsert Plan (Super Admin)
api.upsert_plan(plan_definition_request=PlanDefinitionRequest(
    code="ULTIMATE", max_campaigns=100))`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Subscriptions Section -->
    <section
      id="subscriptions"
      class="space-y-12 pt-16 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <CreditCard class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Subscriptions</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Manage your current plan and billing cycles. Perform plan upgrades and
          monitor usage against quotas in real-time.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Entitlements
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Retrieve the active subscription details for the tenant. Monitor
              exhaustion of quotas for campaigns, responses, and user seats.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Self-Service Checkout
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Initiate an upgrade flow. Provisioning of higher tier quotas is
              executed immediately upon successful checkout processing.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Subscription API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { SubscriptionApi, SubscribeRequest } from '@survey-engine/sdk';

const subApi = new SubscriptionApi(config);

// Get My Subscription
const mySub = await subApi.getMySubscription();

// Checkout / Upgrade
await subApi.checkoutSubscription({
  subscribeRequest: { plan: "GROWTH" }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.SubscriptionApi;
import org.openapitools.client.model.SubscriptionResponse;
import org.openapitools.client.model.SubscribeRequest;

SubscriptionApi api = new SubscriptionApi(client);

// Get My Subscription
SubscriptionResponse mySub = api.getMySubscription();

// Checkout / Upgrade
api.checkoutSubscription(new SubscribeRequest().plan("PRO"));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// Get My Subscription
sub, _, _ := client.SubscriptionAPI.GetMySubscription(ctx).Execute()

// Upgrade
client.SubscriptionAPI.CheckoutSubscription(ctx).
    SubscribeRequest(sdk.SubscribeRequest{Plan: "PRO"}).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.subscription_api import SubscriptionApi
from openapi_client.models.subscribe_request import SubscribeRequest

api = SubscriptionApi(client)

# Get My Subscription
sub = api.get_my_subscription()

# Upgrade
api.checkout_subscription(subscribe_request=SubscribeRequest(plan="GROWTH"))`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Question Bank Section -->
    <section
      id="question-bank"
      class="space-y-12 pt-16 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Database class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Question Bank</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The Question Bank is the foundational library of reusable inquiry
          assets. Define complex question types, validation rules, and
          internationalized content to be shared across multiple surveys.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Asset Lifecycle
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Create and manage core question definitions. Each question
              supports rich metadata, including variable names and structural
              constraints.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Retirement
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Deactivate obsolete questions without breaking historical response
              data. Deactivation prevents new survey versions from binding the
              asset.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Question API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { QuestionsApi, QuestionRequest } from '@survey-engine/sdk';

const questionApi = new QuestionsApi(config);

// CRUD
const question = await questionApi.createQuestion({
  questionRequest: { code: "NPS_01", type: "RATING", text: "How likely..." }
});

const list = await questionApi.listQuestions();
await questionApi.deactivateQuestion({ id: "..." });`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.QuestionsApi;
import org.openapitools.client.model.QuestionRequest;
import org.openapitools.client.model.QuestionResponse;

QuestionsApi api = new QuestionsApi(client);

// CRUD
QuestionResponse q = api.createQuestion(new QuestionRequest()
    .code("SAT_01").type("BOOLEAN").text("Satisfied?"));

List<QuestionResponse> questions = api.listQuestions();
api.deactivateQuestion(UUID.fromString("..."));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// CRUD
q, _, _ := client.QuestionsAPI.CreateQuestion(ctx).
    QuestionRequest(sdk.QuestionRequest{Code: "Q1"}).Execute()

list, _, _ := client.QuestionsAPI.ListQuestions(ctx).Execute()
client.QuestionsAPI.DeactivateQuestion(ctx, "...").Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.questions_api import QuestionsApi
from openapi_client.models.question_request import QuestionRequest

api = QuestionsApi(client)

# CRUD
q = api.create_question(question_request=QuestionRequest(
    code="Q1", type="TEXT", text="Name?"))
list = api.list_questions()
api.deactivate_question(id="...")`}
              />
            </div>
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
          Categories group question assets into business dimensions. Use
          categories for structural organization, multi-dimensional analytics,
          and complex scoring profile design.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Dimension Management
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Define analytics buckets. Map specific questions to categories to
              enable thematic reporting and weighted score aggregation.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Category API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { CategoriesApi, CategoryRequest } from '@survey-engine/sdk';

const categoryApi = new CategoriesApi(config);

// CRUD
const cat = await categoryApi.createCategory({
  categoryRequest: { name: "Customer Loyalty", questionIds: [...] }
});

const list = await categoryApi.listCategories();
await categoryApi.updateCategory({ id: "...", categoryRequest: { ... } });`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.CategoriesApi;
import org.openapitools.client.model.CategoryRequest;
import org.openapitools.client.model.CategoryResponse;

CategoriesApi api = new CategoriesApi(client);

// CRUD
CategoryResponse cat = api.createCategory(new CategoryRequest()
    .name("Security").questionIds(List.of(...)));

List<CategoryResponse> list = api.listCategories();`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// CRUD
cat, _, _ := client.CategoriesAPI.CreateCategory(ctx).
    CategoryRequest(sdk.CategoryRequest{Name: "Security"}).Execute()

list, _, _ := client.CategoriesAPI.ListCategories(ctx).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.categories_api import CategoriesApi
from openapi_client.models.category_request import CategoryRequest

api = CategoriesApi(client)

# CRUD
cat = api.create_category(category_request=CategoryRequest(
    name="UX", question_ids=["..."]))
list = api.list_categories()`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Survey Authoring Section -->
    <section id="surveys" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <BookOpen class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Survey Authoring</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The Surveys API is the foundation of content creation. Manage complex
          nested structures of pages and questions, and control the governance
          via formal lifecycle transitions.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Draft Assembly
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Create surveys with hierarchical page structures. Link reusable
              questions from your bank to assemble a survey draft.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Lifecycle Governance
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Transition surveys through states (Draft → Published → Closed).
              Formal transitions enforce business logic gates before campaigns
              can be launched.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Surveys API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { SurveysApi, SurveyRequest, LifecycleTransitionRequest } from '@survey-engine/sdk';

const surveysApi = new SurveysApi(config);

// 1. Create a Survey Draft
const survey = await surveysApi.createSurvey({
  surveyRequest: {
    title: "Quarterly Feedback",
    description: "Employee engagement survey",
    pages: [
      {
        title: "Work-Life Balance",
        questions: [{ questionId: "Q-UUID-1", orderIndex: 0 }]
      }
    ]
  }
});

// 2. Publish Survey
await surveysApi.transitionSurveyLifecycle({
  id: survey.id,
  lifecycleTransitionRequest: { 
    action: "PUBLISH", 
    reason: "Ready for Q3 rollout" 
  }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.SurveysApi;
import org.openapitools.client.model.SurveyRequest;
import org.openapitools.client.model.LifecycleTransitionRequest;
import java.util.UUID;

SurveysApi api = new SurveysApi(client);

// 1. Create Draft
SurveyResponse survey = api.createSurvey(new SurveyRequest()
    .title("User Research")
    .addPagesItem(new SurveyPageRequest().title("Intro")));

// 2. Transition State
api.transitionSurveyLifecycle(
    survey.getId(), 
    new LifecycleTransitionRequest().action("PUBLISH")
);`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// 1. Create
survey, _, _ := client.SurveysAPI.CreateSurvey(ctx).
    SurveyRequest(sdk.SurveyRequest{Title: "Go Survey"}).Execute()

// 2. Publish
client.SurveysAPI.TransitionSurveyLifecycle(ctx, survey.Id).
    LifecycleTransitionRequest(sdk.LifecycleTransitionRequest{
        Action: "PUBLISH",
    }).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.surveys_api import SurveysApi
from openapi_client.models.survey_request import SurveyRequest

api = SurveysApi(client)

# 1. Author Survey
survey = api.create_survey(survey_request=SurveyRequest(title="Python Survey"))

# 2. Transition
api.transition_survey_lifecycle(
    id=survey.id, 
    lifecycle_transition_request={"action": "PUBLISH"}
)`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Campaign Orchestration Section -->
    <section id="campaigns" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Send class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            Campaign Orchestration
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Campaigns are the delivery units of the platform. Bind surveys to
          audience policies, manage distribution channels, and control response
          ingestion windows.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Launch & Activation
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Create campaigns from published surveys. Activation pins the
              current survey structure and starts the data collection window.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Distribution Channels
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Generate unique access links and embed codes. Support for public
              anonymous links or private authenticated responder flows.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Campaigns API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { CampaignsApi, CampaignRequest } from '@survey-engine/sdk';

const campaignApi = new CampaignsApi(config);

// 1. Create Campaign
const campaign = await campaignApi.createCampaign({
  campaignRequest: {
    name: "Summer 2024 Outreach",
    surveyId: "SURV-UUID-123",
    accessMode: "PUBLIC"
  }
});

// 2. Activate
await campaignApi.activateCampaign({ id: campaign.id });

// 3. Generate Links
const channels = await campaignApi.generateCampaignChannels({ id: campaign.id });`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.CampaignsApi;
import org.openapitools.client.model.CampaignRequest;

CampaignsApi api = new CampaignsApi(client);

// 1. Launch
CampaignResponse camp = api.createCampaign(new CampaignRequest()
    .name("Enterprise Launch")
    .surveyId(UUID.fromString("...")));

// 2. Activate & Distribute
api.activateCampaign(camp.getId());
List<DistributionChannelResponse> links = api.generateCampaignChannels(camp.getId());`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`// 1. Create
camp, _, _ := client.CampaignsAPI.CreateCampaign(ctx).
    CampaignRequest(sdk.CampaignRequest{Name: "Go Camp"}).Execute()

// 2. Activate
client.CampaignsAPI.ActivateCampaign(ctx, camp.Id).Execute()

// 3. Channels
links, _, _ := client.CampaignsAPI.GenerateCampaignChannels(ctx, camp.Id).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.campaigns_api import CampaignsApi

api = CampaignsApi(client)

# 1. Orchestrate
camp = api.create_campaign(campaign_request={"name": "Growth Survey", "survey_id": "UUID"})

# 2. Launch
api.activate_campaign(id=camp.id)

# 3. Get Distribution Links
channels = api.generate_campaign_channels(id=camp.id)`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Response Participation Section -->
    <section
      id="response-submission"
      class="space-y-12 pt-16 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <MousePointerClick class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            Response Participation
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Capture high-fidelity research data from respondents. The Responses
          service handles complex answer validation, multi-part payloads, and
          maintains data integrity against campaign snapshots.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Data Capture
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Submit structured answer sets. Support for multiple question
              types, scale-based inputs, and long-form text responses within a
              single submission transaction.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Real-time Persistence
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Submissions are validated and persisted against the specific
              campaign runtime. Successfully captured responses are indexed for
              immediate analytics availability.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Participation API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { ResponsesApi, ResponseSubmissionRequest } from '@survey-engine/sdk';

const responseApi = new ResponsesApi(config);

// Submit Answers
await responseApi.submitResponse({
  responseSubmissionRequest: {
    campaignId: "...",
    answers: [
      { questionId: "Q1", value: "Highly Satisfied" },
      { questionId: "Q2", value: "9" }
    ]
  }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.ResponsesApi;
import org.openapitools.client.model.ResponseSubmissionRequest;
import org.openapitools.client.model.ResponseSubmissionRequestAnswersInner;

ResponsesApi api = new ResponsesApi(client);

// Submit Answers
api.submitResponse(new ResponseSubmissionRequest()
    .campaignId("CAMP-01")
    .addAnswersItem(new ResponseSubmissionRequestAnswersInner()
        .questionId("Q1").value("YES")));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// Submit Answers
req := sdk.ResponseSubmissionRequest{
    CampaignId: "CAMP1",
    Answers: []sdk.ResponseSubmissionRequestAnswersInner{
        {QuestionId: "Q1", Value: "YES"},
    },
}
client.ResponsesAPI.SubmitResponse(ctx).
    ResponseSubmissionRequest(req).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.responses_api import ResponsesApi
from openapi_client.models.response_submission_request import ResponseSubmissionRequest

api = ResponsesApi(client)

# Submit Answers
api.submit_response(response_submission_request=ResponseSubmissionRequest(
    campaign_id="...",
    answers=[{"question_id": "Q1", "value": "A1"}]
))`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Response Operations Section -->
    <section id="response-ops" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <ListOrdered class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            Response Operations
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Operational control over individual response assets. Monitor lifecycle
          transitions, perform administrative overrides, and query participation
          metadata for operational excellence.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Search & Retrieval
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Locate specific response records by campaign or identifier. Fetch
              full answer details including respondent IPs, browser metadata,
              and category-level scoring breakdowns.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Governance Toggles
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Protect or reopen data assets. Locked responses are immutable for
              audits, while reopening allows for corrections with a mandatory
              reason trail.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Response Ops API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { ResponsesApi } from '@survey-engine/sdk';

const respApi = new ResponsesApi(config);

// 1. List & Get
const list = await respApi.listResponsesByCampaign({ 
  campaignId: "CAMP-UUID" 
});
const detail = await respApi.getResponse({ id: list[0].id });

// 2. Lifecycle Control
await respApi.lockResponse({ id: detail.id });
await respApi.reopenResponse({ 
  id: detail.id, 
  reopenRequest: { reason: "Data correction required" } 
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.ResponsesApi;
import org.openapitools.client.model.SurveyResponseResponse;
import org.openapitools.client.model.ReopenRequest;
import java.util.UUID;
import java.util.List;

ResponsesApi api = new ResponsesApi(client);

// 1. List
List<SurveyResponseResponse> list = api.listResponsesByCampaign(UUID.fromString("..."));

// 2. Control
api.lockResponse(list.get(0).getId());
api.reopenResponse(list.get(0).getId(), new ReopenRequest().reason("Audit fix"));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// 1. List
resps, _, _ := client.ResponsesAPI.ListResponsesByCampaign(ctx, "CAMP-ID").Execute()

// 2. Lock
client.ResponsesAPI.LockResponse(ctx, resps[0].Id).Execute()

// 3. Reopen
req := sdk.ReopenRequest{Reason: "Operational correction"}
client.ResponsesAPI.ReopenResponse(ctx, resps[0].Id).
    ReopenRequest(req).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.responses_api import ResponsesApi
from openapi_client.models.reopen_request import ReopenRequest

api = ResponsesApi(client)

# 1. Search
resps = api.list_responses_by_campaign(campaign_id="...")

# 2. Mutate
api.lock_response(id=resps[0].id)
api.reopen_response(id=resps[0].id, reopen_request=ReopenRequest(reason="..."))`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Scoring Intelligence Section -->
    <section id="scoring" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <PieChart class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            Scoring Intelligence
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          The Scoring Engine computes complex numerical profiles from raw
          qualitative inputs. Define weight profiles and coefficients to perform
          multi-dimensional arithmetic across thousands of data points.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Weight Profiling
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Create reusable mathematical models. Map question identifiers to
              weights to generate scores for "Customer Sentiment", "Risk Score",
              or "Employee Engagement". Profiles are campaign-scoped and support
              prioritized calculation.
            </p>
          </div>
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Score Computation
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Execute deterministic arithmetic on raw category inputs. The
              engine handles normalization, weighting, and produces a totalized
              ScoreResult with audit-ready breakdowns.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Scoring API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { ScoringApi, WeightProfileRequest } from '@survey-engine/sdk';

const scoreApi = new ScoringApi(config);

// 1. Define Weight Profile
const profile = await scoreApi.createWeightProfile({
  weightProfileRequest: {
    name: "Customer Loyalty Index",
    campaignId: "CAMP-UUID-123",
    categoryWeights: [
      { categoryId: "NPS-CAT", weightPercentage: 80 },
      { categoryId: "CSAT-CAT", weightPercentage: 20 }
    ]
  }
});

// 2. Perform Real-time Calculation
const result = await scoreApi.calculateWeightedScore({
  profileId: profile.id,
  requestBody: {
    "NPS-CAT": 9.0,
    "CSAT-CAT": 4.5
  }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.ScoringApi;
import org.openapitools.client.model.WeightProfileRequest;
import org.openapitools.client.model.CategoryWeightRequest;
import java.util.UUID;
import java.util.Map;

ScoringApi api = new ScoringApi(client);

// 1. Create Profile
WeightProfileResponse profile = api.createWeightProfile(new WeightProfileRequest()
    .name("Security Audit")
    .campaignId(UUID.fromString("..."))
    .addCategoryWeightsItem(new CategoryWeightRequest()
        .categoryId("Q1").weightPercentage(100.0)));

// 2. Calculate
ScoreResult result = api.calculateWeightedScore(
    profile.getId(), 
    Map.of("Q1", 5.0)
);`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// 1. Create Profile
profile, _, _ := client.ScoringAPI.CreateWeightProfile(ctx).
    WeightProfileRequest(sdk.WeightProfileRequest{
        Name: "Risk",
        CampaignId: "UUID",
        CategoryWeights: []sdk.CategoryWeightRequest{
            {CategoryId: "cat-1", WeightPercentage: 100.0},
        },
    }).Execute()

// 2. Calculate
scores := map[string]float64{"cat-1": 4.2}
res, _, _ := client.ScoringAPI.CalculateWeightedScore(ctx, profile.Id).
    RequestBody(scores).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.scoring_api import ScoringApi
from openapi_client.models.weight_profile_request import WeightProfileRequest

api = ScoringApi(client)

# 1. Create Profile
profile = api.create_weight_profile(weight_profile_request=WeightProfileRequest(
    name="Risk Profile",
    campaign_id="UUID",
    category_weights=[{"category_id": "C1", "weight_percentage": 50}]
))

# 2. Calculate
result = api.calculate_weighted_score(
    profile_id=profile.id,
    request_body={"C1": 8.5}
)`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Analytics & Insights Section -->
    <section id="analytics" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <Target class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">
            Analytics & Insights
          </h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Extract high-level intelligence from campaign data. The Analytics
          service provides real-time aggregates, frequency distributions, and
          participation metrics to drive business decisions.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Real-time Aggregates
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Retrieve campaign-level metrics including response rates,
              completion counts, and categorical distributions for rapid
              decision support.
            </p>
          </div>
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Comparative Analytics
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Perform side-by-side segment comparisons. Filter by demographic
              data, question answers, or participation metadata to identify
              trends.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Analytics API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { ResponsesApi, ComparisonRequest } from '@survey-engine/sdk';

const analyticsApi = new ResponsesApi(config);

// 1. Get Campaign Aggregates
const analytics = await analyticsApi.getCampaignAnalytics({ 
  campaignId: "CAMP-123" 
});
console.log("Completion Rate:", analytics.completionCount);

// 2. Multi-Segment Comparison
const comparison = await analyticsApi.compareSegments({
  campaignId: "CAMP-123",
  comparisonRequest: {
    segments: [
      { name: "Millennials", filters: [{ key: "age_group", value: "18-34" }] },
      { name: "Gen X", filters: [{ key: "age_group", value: "35-50" }] }
    ]
  }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.ResponsesApi;
import org.openapitools.client.model.CampaignAnalytics;
import java.util.UUID;

ResponsesApi api = new ResponsesApi(client);

// 1. Get Aggregates
CampaignAnalytics stats = api.getCampaignAnalytics(UUID.fromString("..."));
System.out.println("Responses: " + stats.getResponseCount());

// 2. Get Full Deep Report
FullCampaignAnalyticsResponse report = api.getFullReport(UUID.fromString("..."));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`// 1. Get Intelligence
res, _, _ := client.ResponsesAPI.GetCampaignAnalytics(ctx, "CAMP-UUID").Execute()

// 2. Detailed Report
report, _, _ := client.ResponsesAPI.GetFullReport(ctx, "CAMP-UUID").Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.responses_api import ResponsesApi

api = ResponsesApi(client)

# 1. Get Intelligence
stats = api.get_campaign_analytics(campaign_id="CAMP-UUID")

# 2. Get Full Report
report = api.get_full_report(campaign_id="CAMP-UUID")`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Compliance Auditing Section -->
    <section id="compliance" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <History class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Compliance Auditing</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Access high-fidelity audit trails for regulatory compliance and
          forensics. The Audit service tracks every administrative mutation,
          security event, and data access pattern with full actor traceability.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Tenant Activity Logs
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Query actions performed within the current tenant scope. Filter by
              actor, date range, or action type (e.g., SURVEY_PUBLISHED, 
              AUTH_PROFILE_ROTATED) to maintain a transparent record of change.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Platform-Wide Forensics
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Super-admin level access to cross-tenant event streams. Vital for
              cross-platform investigations and global security monitoring
              sessions.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} Compliance API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { AuditLogsApi } from '@survey-engine/sdk';

const auditApi = new AuditLogsApi(config);

// Current Tenant Logs
const logs = await auditApi.getTenantAuditLogs({ 
  page: 0, 
  size: 50 
});

// Platform Logs (Super Admin)
const platformLogs = await auditApi.getPlatformAuditLogs({ 
  action: "TENANT_SUSPENDED" 
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.AuditLogsApi;
import org.openapitools.client.model.PageAuditLogResponse;

AuditLogsApi api = new AuditLogsApi(client);

// Tenant logs
PageAuditLogResponse logs = api.getTenantAuditLogs(0, 50, "CREATED_AT:DESC");

// Platform logs
api.getPlatformAuditLogs(0, 10, "LOGIN", null, "TENANT_ID", null, null, null);`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// Tenant logs
logs, _, _ := client.AuditLogsAPI.GetTenantAuditLogs(ctx).
    Page(0).Size(50).Execute()

// Platform logs
platform, _, _ := client.AuditLogsAPI.GetPlatformAuditLogs(ctx).
    Action("USER_LOGIN").Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.audit_logs_api import AuditLogsApi

api = AuditLogsApi(client)

# Queries
logs = api.get_tenant_audit_logs(page=0, size=20)
platform = api.get_platform_audit_logs(action="DELETE")`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Platform Governance Section -->
    <section id="platform-admin" class="space-y-12 pt-16 border-t border-border/20">
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <ShieldAlert class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Platform Governance</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Platform-level orchestration for Super Administrators. Manage the
          full multi-tenant ecosystem, monitor global health metrics, and
          oversee commercial entitlements.
        </p>
      </div>
      <div class="grid lg:grid-cols-2 gap-12">
        <div class="space-y-12">
          <div class="space-y-4">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              1. Tenant Lifecycle (SA)
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Suspend or reactivate tenants based on commercial state or policy
              violations. Impersonate tenant admin contexts for white-glove
              support and troubleshooting.
            </p>
          </div>
          <div class="space-y-4 pt-12">
            <h3 class="text-2xl font-bold border-l-4 border-primary pl-6">
              2. Commercial Overrides
            </h3>
            <p class="text-muted-foreground leading-relaxed">
              Modify subscription boundaries manually. Grant custom trial periods
              or override plan features for specific high-value tenants.
            </p>
          </div>
        </div>
        <div
          class="bg-zinc-950 rounded-2xl p-6 shadow-2xl border border-white/5 h-fit"
        >
          <div
            class="mb-4 flex items-center justify-between border-b border-white/10 pb-4"
          >
            <span
              class="text-[10px] font-bold text-zinc-500 uppercase tracking-widest"
              >{activeTab} SuperAdmin API</span
            >
          </div>
          {#if activeTab === "typescript"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="typescript"
                code={`import { SuperAdminApi } from '@survey-engine/sdk';

const superApi = new SuperAdminApi(config);

// 1. Control
await superApi.suspendTenant({ id: "TENANT_UUID" });

// 2. Impersonate
const context = await superApi.impersonateTenant({ id: "TENANT_UUID" });

// 3. Entitlement Override
await superApi.overrideTenantSubscription({
  tenantId: "TENANT_UUID",
  overrideSubscriptionRequest: { planCode: "ENTERPRISE_CUSTOM" }
});`}
              />
            </div>
          {:else if activeTab === "java"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="java"
                code={`import org.openapitools.client.api.SuperAdminApi;
import org.openapitools.client.model.OverrideSubscriptionRequest;

SuperAdminApi api = new SuperAdminApi(client);

// 1. Suspension
api.suspendTenant("T-UUID");

// 2. Subscription Override
api.overrideTenantSubscription("T-UUID", new OverrideSubscriptionRequest()
    .planCode("CUSTOM_VIP"));`}
              />
            </div>
          {:else if activeTab === "go"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="go"
                code={`import sdk "github.com/muhibnayem/survey-engine-sdk-go"

// 1. Lifecycle
client.SuperAdminAPI.SuspendTenant(ctx, "T1").Execute()

// 2. Impersonate
admin, _, _ := client.SuperAdminAPI.ImpersonateTenant(ctx, "T1").Execute()

// 3. Override
ovr := sdk.OverrideSubscriptionRequest{PlanCode: "PRO_MAX"}
client.SuperAdminAPI.OverrideTenantSubscription(ctx, "T1").
    OverrideSubscriptionRequest(ovr).Execute()`}
              />
            </div>
          {:else if activeTab === "python"}
            <div class="space-y-6">
              <SimpleCodeBlock
                language="python"
                code={`from openapi_client.api.super_admin_api import SuperAdminApi
from openapi_client.models.override_subscription_request import OverrideSubscriptionRequest

api = SuperAdminApi(client)

# Orchestration
api.suspend_tenant(id="T1")
api.impersonate_tenant(id="T1")
api.override_tenant_subscription(
    tenant_id="T1", 
    override_subscription_request=OverrideSubscriptionRequest(plan_code="...")
)`}
              />
            </div>
          {/if}
        </div>
      </div>
    </section>

    <!-- Best Practices Footer -->
    <section
      id="error-handling"
      class="space-y-12 pt-32 border-t border-border/20"
    >
      <div class="space-y-4">
        <div class="flex items-center gap-3 text-primary">
          <GraduationCap class="w-10 h-10" />
          <h2 class="text-4xl font-bold tracking-tight">Best Practices</h2>
        </div>
        <p class="text-muted-foreground text-lg leading-relaxed">
          Guidelines for building resilient, enterprise-grade integrations with
          the Survey Engine ecosystem.
        </p>
      </div>
      <div class="grid md:grid-cols-2 gap-8">
        <div
          class="p-8 rounded-2xl bg-muted/30 border border-border/50 space-y-4"
        >
          <div
            class="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center text-primary text-2xl font-bold italic"
          >
            !
          </div>
          <h4 class="text-xl font-bold">Resilient Error Handling</h4>
          <p class="text-sm text-muted-foreground leading-relaxed">
            Always wrap SDK calls in try-catch blocks. Standardized error models
            include field-level validation errors and platform-wide rate limit
            metadata to help you build smart retry logic.
          </p>
        </div>
        <div
          class="p-8 rounded-2xl bg-muted/30 border border-border/50 space-y-4"
        >
          <div
            class="w-12 h-12 rounded-xl bg-green-500/10 flex items-center justify-center text-green-500"
          >
            <CheckCircle2 class="w-6 h-6" />
          </div>
          <h4 class="text-xl font-bold">Type Safety & Contracts</h4>
          <p class="text-sm text-muted-foreground leading-relaxed">
            Leverage native DTOs instead of raw maps. This ensures early
            detection of contract breaking changes and provides elite IDE
            autocomplete during complex payload construction.
          </p>
        </div>
      </div>
    </section>

    <footer class="pt-32 border-t border-border pb-16 opacity-50 text-center">
      <p class="text-sm font-medium">
        Enterprise SDK Documentation &copy; 2026 Survey Engine Platform
      </p>
    </footer>
  </div>
</div>

<style>
  /* Scrollbar utilities if needed */
</style>
