# Bug Fix: Command Palette Infinite Loop

## Issue
```
Svelte error: effect_update_depth_exceeded
Maximum update depth exceeded. This typically indicates that an effect reads 
and writes the same piece of state
```

## Root Cause

The original implementation had a `$effect` that both **read** and **wrote** to the same `items` state:

```typescript
// ❌ BAD: Reads and writes same state
let items = $state<CommandItem[]>([]);

$effect(() => {
	if (!query.trim()) {
		items = allCommands;  // ← WRITE
	} else {
		const filtered = allCommands.filter(...);
		items = filtered;     // ← WRITE
	}
	selectedId = items[0]?.id ?? null;  // ← READ
});
```

This created an infinite reactive loop:
1. `query` changes
2. `$effect` runs
3. `items` is updated
4. `$effect` re-runs (because it reads `items`)
5. Go to step 3 → **Infinite loop**

## Solution

Use **derived state** instead of mutable state for computed values:

```typescript
// ✅ GOOD: Computed value, not mutable state
let query = $state('');
let selectedId = $state<string | null>(null);

// Pure function (no side effects)
function getFilteredItems(): CommandItem[] {
	if (!query.trim()) {
		return allCommands;
	}
	const q = query.toLowerCase();
	return allCommands
		.filter(cmd => cmd.label.toLowerCase().includes(q) || ...)
		.sort((a, b) => ...);
}

// Effect only reads, doesn't write to items
$effect(() => {
	const filtered = getFilteredItems();  // ← READ only
	selectedId = filtered[0]?.id ?? null;
});

// Derived state (automatically updates when query changes)
let items = $derived(getFilteredItems());
```

## Key Principles

### ✅ Do:
- Use `$derived()` for computed values
- Use `$effect()` only for side effects (API calls, DOM manipulation)
- Keep effects read-only when possible
- Use pure functions for transformations

### ❌ Don't:
- Read and write the same state in one effect
- Create circular dependencies between states
- Use effects for synchronous computations

## Svelte 5 Reactivity Patterns

### Pattern 1: Derived State (Recommended)
```typescript
let count = $state(0);
let doubled = $derived(count * 2);
```

### Pattern 2: Effect for Side Effects Only
```typescript
let userId = $state(null);
let userData = $state(null);

$effect(() => {
	if (userId) {
		// Side effect: API call
		fetchUser(userId).then(data => userData = data);
	}
});
```

### Pattern 3: Computed Function
```typescript
let filter = $state('');
let items = $state([]);

function getFilteredItems() {
	if (!filter) return items;
	return items.filter(i => i.name.includes(filter));
}

// Use in template: {#each getFilteredItems() as item}
```

## Files Fixed

- `frontend/src/lib/components/CommandPalette.svelte`

## Testing

1. Open app
2. Press `Cmd+K`
3. Type in search box
4. Verify no console errors
5. Verify filtering works
6. Verify keyboard navigation works

## Prevention

To prevent similar issues in the future:

1. **Ask**: "Does this effect need to write state?"
2. **Consider**: "Can this be a `$derived` instead?"
3. **Check**: "Am I reading and writing the same state?"
4. **Prefer**: Pure functions over effects for computations

## Related Svelte Documentation

- [Svelte 5 Reactivity](https://svelte.dev/docs/svelte/$derived)
- [Effects](https://svelte.dev/docs/svelte/$effect)
- [Common Pitfalls](https://svelte.dev/docs/svelte/effect-update-depth-exceeded)

## Lesson Learned

**Computed values should be derived, not stored.**

If a value can be computed from other state, use `$derived()` or a pure function. Only use `$state()` when you need to store independent data.

---

**Fixed:** March 10, 2026  
**Status:** ✅ Resolved  
**Build:** ✅ Passing
