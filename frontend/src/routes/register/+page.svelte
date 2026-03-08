<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import * as Card from "$lib/components/ui/card";
    import { UserPlus } from "lucide-svelte";
    import { z } from "zod";

    const registerSchema = z
        .object({
            fullName: z
                .string()
                .trim()
                .min(2, "Full name must be at least 2 characters")
                .max(120, "Full name must be at most 120 characters"),
            email: z
                .string()
                .trim()
                .email("Enter a valid email address")
                .max(255, "Email must be at most 255 characters"),
            password: z
                .string()
                .min(8, "Password must be at least 8 characters")
                .max(64, "Password must be at most 64 characters")
                .regex(/[a-z]/, "Password must include a lowercase letter")
                .regex(/[A-Z]/, "Password must include an uppercase letter")
                .regex(/\d/, "Password must include a number")
                .regex(
                    /[^A-Za-z\d]/,
                    "Password must include a special character",
                ),
            confirmPassword: z
                .string()
                .min(1, "Confirm password is required")
                .max(64, "Confirm password must be at most 64 characters"),
        })
        .superRefine((data, ctx) => {
            if (data.password !== data.confirmPassword) {
                ctx.addIssue({
                    code: "custom",
                    path: ["confirmPassword"],
                    message: "Passwords do not match",
                });
            }
        });

    type RegisterFormInput = z.input<typeof registerSchema>;
    type RegisterFormData = z.output<typeof registerSchema>;
    type FieldName = keyof RegisterFormInput;
    type FieldErrors = Partial<Record<FieldName, string>>;

    let fullName = $state("");
    let email = $state("");
    let password = $state("");
    let confirmPassword = $state("");
    let submitAttempted = $state(false);
    let errors = $state<FieldErrors>({});
    let touched = $state<Record<FieldName, boolean>>({
        fullName: false,
        email: false,
        password: false,
        confirmPassword: false,
    });

    const passwordScore = $derived.by(() => {
        let score = 0;
        if (password.length >= 8 && password.length <= 64) score++;
        if (/[a-z]/.test(password)) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/\d/.test(password)) score++;
        if (/[^A-Za-z\d]/.test(password)) score++;
        return score;
    });

    const passwordStrengthLabel = $derived.by(() => {
        if (passwordScore <= 1) return "Very weak";
        if (passwordScore <= 2) return "Weak";
        if (passwordScore <= 3) return "Fair";
        if (passwordScore <= 4) return "Strong";
        return "Very strong";
    });

    const passwordStrengthBarClass = $derived.by(() => {
        if (passwordScore <= 1) return "bg-destructive";
        if (passwordScore <= 3) return "bg-amber-500";
        return "bg-emerald-500";
    });

    const passwordStrengthWidth = $derived(`${(passwordScore / 5) * 100}%`);

    const passwordRules = $derived.by(() => [
        {
            label: "8-64 characters",
            valid: password.length >= 8 && password.length <= 64,
        },
        { label: "Uppercase letter", valid: /[A-Z]/.test(password) },
        { label: "Lowercase letter", valid: /[a-z]/.test(password) },
        { label: "Number", valid: /\d/.test(password) },
        { label: "Special character", valid: /[^A-Za-z\d]/.test(password) },
    ]);

    function currentInput(): RegisterFormInput {
        return {
            fullName,
            email,
            password,
            confirmPassword,
        };
    }

    function validateForm(
        input: RegisterFormInput,
    ): { data?: RegisterFormData; errors: FieldErrors } {
        const parsed = registerSchema.safeParse(input);
        if (parsed.success) {
            return { data: parsed.data, errors: {} };
        }

        const next: FieldErrors = {};
        for (const issue of parsed.error.issues) {
            const field = issue.path[0];
            if (typeof field !== "string") continue;
            const key = field as FieldName;
            if (!next[key]) {
                next[key] = issue.message;
            }
        }
        return { errors: next };
    }

    function validateAndSetErrors() {
        const result = validateForm(currentInput());
        errors = result.errors;
        return result;
    }

    function markTouched(field: FieldName) {
        touched[field] = true;
        validateAndSetErrors();
    }

    function fieldError(field: FieldName): string | undefined {
        if (!submitAttempted && !touched[field]) return undefined;
        return errors[field];
    }

    async function handleRegister(e: Event) {
        e.preventDefault();
        submitAttempted = true;

        const validation = validateAndSetErrors();
        if (!validation.data) {
            return;
        }

        const success = await auth.register({
            fullName: validation.data.fullName,
            email: validation.data.email,
            password: validation.data.password,
            confirmPassword: validation.data.confirmPassword,
        });
        if (success) {
            goto("/onboarding/plan");
        }
    }
</script>

<svelte:head>
    <title>Register — Survey Engine</title>
</svelte:head>

<div
    class="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-background to-muted/30 p-4"
>
    <div class="w-full max-w-md">
        <!-- Brand -->
        <div class="mb-8 text-center">
            <div
                class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground text-xl font-bold shadow-lg"
            >
                SE
            </div>
            <h1 class="text-2xl font-bold text-foreground">
                Create your account
            </h1>
            <p class="mt-1 text-sm text-muted-foreground">
                Start building surveys in minutes
            </p>
        </div>

        <Card.Root class="border-border/50 shadow-xl backdrop-blur">
            <Card.Content class="pt-6">
                <form onsubmit={handleRegister} class="space-y-4">
                    {#if auth.error}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {auth.error}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="reg-fullname">Full Name</Label>
                        <Input
                            id="reg-fullname"
                            type="text"
                            placeholder="Jane Doe"
                            bind:value={fullName}
                            oninput={() => markTouched("fullName")}
                            required
                            autocomplete="name"
                            class={fieldError("fullName")
                                ? "border-destructive focus-visible:ring-destructive/40"
                                : ""}
                        />
                        {#if fieldError("fullName")}
                            <p class="text-xs text-destructive">
                                {fieldError("fullName")}
                            </p>
                        {/if}
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-email">Email</Label>
                        <Input
                            id="reg-email"
                            type="email"
                            placeholder="you@company.com"
                            bind:value={email}
                            oninput={() => markTouched("email")}
                            required
                            autocomplete="email"
                            class={fieldError("email")
                                ? "border-destructive focus-visible:ring-destructive/40"
                                : ""}
                        />
                        {#if fieldError("email")}
                            <p class="text-xs text-destructive">
                                {fieldError("email")}
                            </p>
                        {/if}
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-password">Password</Label>
                        <Input
                            id="reg-password"
                            type="password"
                            placeholder="••••••••"
                            bind:value={password}
                            oninput={() => markTouched("password")}
                            required
                            autocomplete="new-password"
                            class={fieldError("password")
                                ? "border-destructive focus-visible:ring-destructive/40"
                                : ""}
                        />
                        <div class="space-y-2">
                            <div
                                class="h-2 w-full overflow-hidden rounded-full bg-muted"
                            >
                                <div
                                    class="h-full transition-all duration-300 {passwordStrengthBarClass}"
                                    style:width={passwordStrengthWidth}
                                ></div>
                            </div>
                            <p class="text-xs text-muted-foreground">
                                Password strength:
                                <span class="font-medium text-foreground">
                                    {passwordStrengthLabel}
                                </span>
                            </p>
                            <ul class="space-y-1 text-xs text-muted-foreground">
                                {#each passwordRules as rule}
                                    <li
                                        class={rule.valid
                                            ? "text-emerald-600"
                                            : "text-muted-foreground"}
                                    >
                                        {rule.valid ? "✓" : "•"} {rule.label}
                                    </li>
                                {/each}
                            </ul>
                        </div>
                        {#if fieldError("password")}
                            <p class="text-xs text-destructive">
                                {fieldError("password")}
                            </p>
                        {/if}
                    </div>

                    <div class="space-y-2">
                        <Label for="reg-confirm">Confirm Password</Label>
                        <Input
                            id="reg-confirm"
                            type="password"
                            placeholder="••••••••"
                            bind:value={confirmPassword}
                            oninput={() => markTouched("confirmPassword")}
                            required
                            autocomplete="new-password"
                            class={fieldError("confirmPassword")
                                ? "border-destructive focus-visible:ring-destructive/40"
                                : ""}
                        />
                        {#if fieldError("confirmPassword")}
                            <p class="text-xs text-destructive">
                                {fieldError("confirmPassword")}
                            </p>
                        {/if}
                    </div>

                    <Button
                        type="submit"
                        class="w-full"
                        disabled={auth.isLoading}
                    >
                        {#if auth.isLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                            Creating account...
                        {:else}
                            <UserPlus class="mr-2 h-4 w-4" />
                            Create account
                        {/if}
                    </Button>
                </form>
            </Card.Content>
            <Card.Footer class="justify-center">
                <p class="text-sm text-muted-foreground">
                    Already have an account?
                    <a
                        href="/login"
                        class="font-medium text-primary hover:underline"
                        >Sign in</a
                    >
                </p>
            </Card.Footer>
        </Card.Root>
    </div>
</div>
