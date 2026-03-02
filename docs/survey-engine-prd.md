# Product Requirements Document (PRD)
## Product: Survey Engine
## Version: 1.0 (One-Page)
## Date: March 2, 2026
## Owner: Product Team

## 1. Objective
Build a flexible Survey Engine that allows teams to create, publish, and analyze surveys with configurable logic, branding, access controls, and rich reporting.
The platform must remain domain-agnostic and reusable across education, corporate, NGO, and public-sector survey programs.

## 2. Problem Statement
Current survey workflows are fragmented across tools and lack consistency in design, respondent controls, and deep analytics. Teams need one platform to:
- Build surveys quickly with advanced question logic.
- Control audience eligibility and response quality.
- Share surveys across channels.
- Measure outcomes with actionable insights.

## 3. Goals and Success Metrics
### Business Goals
- Increase survey creation and completion across teams.
- Improve response quality through validation and restriction controls.
- Reduce time from survey idea to published survey.

### Success Metrics (MVP)
- Survey publish success rate >= 95%
- Completion rate uplift >= 20% vs baseline
- Drop-off visibility for 100% of published surveys
- Time to create and publish a basic survey <= 10 minutes
- Tenant onboarding completion (subscription to active) <= 30 minutes

## 4. In Scope
### Survey Builder
- Single-page and multi-page surveys
- Question bank per survey with ordering controls
- Mandatory/optional toggle per question
- Skip logic (conditional branching)
- Rich content support: image, video, GIF, MP3, notes/comments
- Question types:
  - Free text
  - Multiple choice
  - Matrix/Likert
  - Rank
  - Rating scale

### Campaign and Assignment (Generic)
- Campaign model with configurable evaluation/collection windows
- Audience and target assignment rules (evaluator -> target patterns)
- Eligibility constraints (for example: one submission per evaluator-target-campaign key)
- Reusable policy templates for domain-specific rollouts (education, corporate, NGO, public surveys)

### Audience/Roster Integration
- Sync audience datasets from external systems (SIS/HRMS/CRM/API/CSV)
- Map external identifiers to survey eligibility and assignment policies
- Scheduled and on-demand sync jobs with validation/error reporting

### Respondent Data Collection
- Form fields: Name, Email, Address, Phone, Date/Time, Number, URL, File Upload

### Styling
- Prebuilt visual templates
- Custom survey styles

### Settings
- Password protection
- CAPTCHA protection
- One response per device/computer
- IP restrictions
- Email restrictions
- Survey closing by date/time or quota
- Show/hide question numbers
- Show/hide progress indicator
- Show/hide back button
- Response session timeout
- Configurable start/finish messages
- Language pack support
- Configurable header/footer
- Anonymous mode with identity masking in reporting while preserving dedup/eligibility enforcement

### Results and Analytics
- Overview dashboard:
  - Traffic by day/time
  - Total completed surveys
  - Completion rate
  - Incomplete responses
  - Demographic analysis
- Reports: question-level answer breakdown
- Participants: completion count + participant metadata
- Analysis: filtered/segmented insights and comparisons
- Hierarchical rollups (for example: department/program/region/branch)
- Campaign comparisons across time windows (term/quarter/year)

### Distribution and Integration
- Direct shareable link
- Embedding via HTML, WordPress, JavaScript/frontend
- Email distribution
- Headless API and webhook integrations for existing portals/apps
- External auth onboarding (OIDC/SAML) with internal RBAC mapping

## 5. Out of Scope (MVP)
- AI-generated question suggestions
- Native mobile apps
- Real-time collaboration in builder
- Third-party analytics marketplace integrations

## 6. Primary Users
- Survey creators (operations, research, marketing, HR)
- Respondents (internal/external participants)
- Analysts/managers reviewing outcomes
- Tenant IT administrators configuring identity and integration

## 7. Key User Flows
1. Creator builds survey -> configures logic/style/settings -> publishes.
2. Respondent opens survey link/embed -> submits response.
3. Manager reviews dashboard/report -> filters segments -> compares performance.

## 8. Functional Requirements (MVP)
- FR1: User can create/edit/delete surveys.
- FR2: User can add/edit/reorder questions and options.
- FR3: User can define mandatory questions and skip logic.
- FR4: User can configure access/security restrictions.
- FR5: System captures complete/incomplete responses and metadata.
- FR6: System shows overview metrics and question-level reports.
- FR7: User can distribute survey via link, embed, and email.
- FR8: Subscriber can onboard via external IdP and bootstrap tenant activation.
- FR9: System supports generic campaign, assignment, and eligibility policies.
- FR10: System supports anonymity-preserving response mode with anti-duplicate enforcement.
- FR11: System supports hierarchical rollup reporting for organizational structures.

## 9. Non-Functional Requirements
- Availability: 99.9% monthly uptime target
- Performance: survey page load <= 2.5s under expected load
- Security: encrypted data in transit and at rest
- Scalability: support concurrent active responses without degradation
- Localization: architecture supports multi-language packs

## 10. Risks and Dependencies
### Risks
- Complex skip logic may introduce edge-case routing bugs
- Restrictions (IP/device/email) may impact legitimate respondents
- File upload and media handling may increase storage/security overhead

### Dependencies
- Email delivery provider
- CAPTCHA provider
- Storage service for media and uploads
- Analytics/data pipeline for reporting
- External identity provider integration (OIDC/SAML)
- Audience data source integrations (API/CSV/SIS/HRMS/CRM)

## 11. Release Plan
### Phase 1 (MVP)
Builder, core question types, logic, settings, campaign and assignment policies, link/embed/email sharing, baseline dashboard/reporting, external-auth onboarding.

### Phase 2
Advanced analysis/segmentation, hierarchical reporting expansion, richer templates, broader connector enhancements.

## 12. Open Questions
- Required data retention period for responses and uploads?
- Exact quota behavior (hard stop vs grace handling)?
- Which demographic dimensions are mandatory at launch?
- Permission model needed for creator/admin roles?
- Default anonymity policy by survey type and tenant policy?
- Minimum supported roster connector set for v1?
