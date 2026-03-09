export interface DocsNavItem {
  title: string;
  href?: string;
  children?: DocsNavItem[];
}

export const docsNavItems: DocsNavItem[] = [
  {
    title: 'API Reference',
    children: [
      { title: 'Overview', href: '/docs/api' },
      { title: 'Authentication', href: '/docs/api/authentication' },
      { title: 'Surveys', href: '/docs/api/surveys' },
      { title: 'Campaigns', href: '/docs/api/campaigns' },
      { title: 'Responses', href: '/docs/api/responses' },
      { title: 'Analytics', href: '/docs/api/analytics' },
      { title: 'Scoring', href: '/docs/api/scoring' },
      { title: 'Subscriptions', href: '/docs/api/subscriptions' }
    ]
  },
  {
    title: 'SDK',
    children: [{ title: 'Client SDK', href: '/docs/sdk' }]
  }
];
