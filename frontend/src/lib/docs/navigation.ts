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
    children: [
      { 
        title: 'Developer SDK', 
        href: '/docs/sdk',
        children: [
          { title: 'Foundations', href: '/docs/sdk#foundations' },
          { title: 'Identity & Access', href: '/docs/sdk#id-access' },
          { title: 'Auth Profiles', href: '/docs/sdk#auth-profiles' },
          { title: 'Plans & Catalog', href: '/docs/sdk#plans-catalog' },
          { title: 'Subscriptions', href: '/docs/sdk#subscriptions' },
          { title: 'Question Bank', href: '/docs/sdk#question-bank' },
          { title: 'Categories & Content', href: '/docs/sdk#categories' },
          { title: 'Survey Lifecycle', href: '/docs/sdk#surveys' },
          { title: 'Campaigns', href: '/docs/sdk#campaigns' },
          { title: 'Respondent Experience', href: '/docs/sdk#respondent' },
          { title: 'Response Submission', href: '/docs/sdk#submissions' },
          { title: 'Analytics & Scoring', href: '/docs/sdk#analytics' },
          { title: 'Management & Audit', href: '/docs/sdk#governance' },
          { title: 'Error Handling', href: '/docs/sdk#error-handling' }
        ]
      }
    ]
  }
];
