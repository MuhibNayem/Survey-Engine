import { openApiTags } from './openapi';
import { slugifyTag } from './openapi';

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
      ...openApiTags.map(tag => ({
        title: tag.name,
        href: `/docs/api/${slugifyTag(tag.name)}`
      }))
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
          { title: 'OIDC Respondent Flow', href: '/docs/sdk#oidc-flow' },
          { title: 'Plans & Catalog', href: '/docs/sdk#plans-catalog' },
          { title: 'Subscriptions', href: '/docs/sdk#subscriptions' },
          { title: 'Question Bank', href: '/docs/sdk#question-bank' },
          { title: 'Categories', href: '/docs/sdk#categories' },
          { title: 'Survey Authoring', href: '/docs/sdk#surveys' },
          { title: 'Campaigns', href: '/docs/sdk#campaigns' },
          { title: 'Response Participation', href: '/docs/sdk#response-submission' },
          { title: 'Response Operations', href: '/docs/sdk#response-ops' },
          { title: 'Scoring Intelligence', href: '/docs/sdk#scoring' },
          { title: 'Analytics & Insights', href: '/docs/sdk#analytics' },
          { title: 'Compliance Auditing', href: '/docs/sdk#compliance' },
          { title: 'Platform Governance', href: '/docs/sdk#platform-admin' },
          { title: 'Feature Management', href: '/docs/sdk#features' },
          { title: 'Error Handling', href: '/docs/sdk#error-handling' }
        ]
      }
    ]
  }
];
