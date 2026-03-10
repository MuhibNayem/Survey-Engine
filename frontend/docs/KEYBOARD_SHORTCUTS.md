# Keyboard Shortcuts Guide

## Global Shortcuts

These shortcuts work throughout the application:

| Shortcut | Action | Context |
|----------|--------|---------|
| `Cmd+K` / `Ctrl+K` | Open Command Palette | Anywhere |
| `Cmd+S` / `Ctrl+S` | Save current form | Edit forms |
| `Cmd+N` / `Ctrl+N` | Create new item | List pages |
| `Shift+?` | Show keyboard shortcuts help | Anywhere |
| `Escape` | Close dialogs/modals | When modal open |

## Navigation Shortcuts

| Shortcut | Action |
|----------|--------|
| `G` then `D` | Go to Dashboard |
| `G` then `S` | Go to Surveys |
| `G` then `C` | Go to Campaigns |
| `Shift+S` | Go to Surveys (direct) |
| `Shift+C` | Go to Campaigns (direct) |

## Command Palette Shortcuts

When the Command Palette (`Cmd+K`) is open:

| Key | Action |
|-----|--------|
| `↑` / `↓` | Navigate through commands |
| `Enter` | Execute selected command |
| `Esc` | Close palette |
| Type | Filter commands in real-time |

## Page-Specific Shortcuts

### Surveys Page

| Shortcut | Action |
|----------|--------|
| `Cmd+N` / `Ctrl+N` | Create new survey |
| `Cmd+K` / `Ctrl+K` | Focus search input |

### Campaigns Page

| Shortcut | Action |
|----------|--------|
| `Cmd+N` / `Ctrl+N` | Create new campaign |
| `Cmd+K` / `Ctrl+K` | Focus search input |

### Questions Page

| Shortcut | Action |
|----------|--------|
| `Cmd+N` / `Ctrl+N` | Create new question |
| `Cmd+K` / `Ctrl+K` | Focus search input |

### Categories Page

| Shortcut | Action |
|----------|--------|
| `Cmd+N` / `Ctrl+N` | Create new category |
| `Cmd+K` / `Ctrl+K` | Focus search input |

## Using the Command Palette

The **Command Palette** is the fastest way to navigate and execute actions:

1. Press `Cmd+K` (Mac) or `Ctrl+K` (Windows/Linux)
2. Type to search for commands
3. Use arrow keys to navigate
4. Press `Enter` to execute

See [Command Palette Documentation](./COMMAND_PALETTE.md) for full details.

## Technical Implementation

### Adding Shortcuts to a Page

```svelte
<script lang="ts">
	import { useKeyboardShortcuts, commonShortcuts } from '$lib/hooks/useKeyboardShortcuts.svelte';

	// Use the hook
	useKeyboardShortcuts([
		commonShortcuts.createNew(() => {
			// Handle create new action
		}),
		commonShortcuts.search(() => {
			// Handle search focus
		})
	]);
</script>
```

### Custom Shortcuts

```svelte
<script lang="ts">
	import { registerKeyboardShortcuts } from '$lib/hooks/useKeyboardShortcuts.svelte';
	import { onMount } from 'svelte';

	onMount(() => {
		return registerKeyboardShortcuts([
			{
				key: 's',
				ctrl: true,
				action: () => handleSave(),
				description: 'Save current form',
				preventDefault: true
			},
			{
				key: 'd',
				shift: true,
				action: () => handleDelete(),
				description: 'Delete selected item',
				preventDefault: true
			}
		]);
	});
</script>
```

### Shortcut Configuration Options

```typescript
interface Shortcut {
	key: string;              // Key to listen for (e.g., 's', 'k', 'enter')
	ctrl?: boolean;           // Require Ctrl/Cmd key
	shift?: boolean;          // Require Shift key
	alt?: boolean;            // Require Alt/Option key
	action: () => void;       // Action to execute
	description: string;      // Description for help docs
	preventDefault?: boolean; // Prevent default browser behavior
	scope?: HTMLElement;      // Only trigger when this element is focused
}
```

## Accessibility

- All shortcuts are discoverable via `Shift+?` (future enhancement)
- Shortcuts don't interfere with form input
- `Cmd+K` works even in input fields (with modifier)
- Screen reader announcements for palette open/close (future)

## Browser Support

| Browser | Support | Notes |
|---------|---------|-------|
| Chrome/Edge | ✅ Full | Recommended |
| Firefox | ✅ Full | |
| Safari | ✅ Full | |
| Opera | ✅ Full | |

## Conflicts & Limitations

### When Shortcuts Don't Work

- In `input`, `textarea`, `contenteditable` elements (except `Cmd+K`)
- When browser has reserved the shortcut
- When another app has focus

### Browser Reserved Shortcuts

These browser shortcuts take precedence:
- `Cmd+W` / `Ctrl+W` - Close tab
- `Cmd+T` / `Ctrl+T` - New tab
- `Cmd+R` / `Ctrl+R` - Refresh
- `Cmd+L` / `Ctrl+L` - Focus address bar

## Future Enhancements

Planned improvements:

- [ ] Visual shortcut cheat sheet (`Shift+?`)
- [ ] Customizable shortcuts (user preferences)
- [ ] Context-aware shortcuts (page-specific)
- [ ] Vim-style navigation (`j`, `k`, `g g`)
- [ ] Chord shortcuts (`g` then `s` for "go to surveys")
- [ ] Shortcut conflict detection
- [ ] Per-user shortcut preferences

## Troubleshooting

### Shortcuts Not Working?

1. Check if you're in an input field
2. Ensure no browser extensions are intercepting
3. Try a different browser
4. Check for OS-level shortcut conflicts

### Cmd+K Not Opening Palette?

1. Ensure you're on an authenticated page
2. Check browser console for errors
3. Verify JavaScript is enabled
4. Try `Ctrl+K` on Windows/Linux

## Related Documentation

- [Command Palette](./COMMAND_PALETTE.md)
- [UI Components](./UI_COMPONENTS.md)
- [Accessibility Guide](./ACCESSIBILITY.md)
