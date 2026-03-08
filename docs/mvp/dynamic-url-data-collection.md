# Dynamic URL Metadata Injection

## 1. Executive Summary
The Survey Engine provides a powerful, domain-agnostic mechanism to track contextual information about a respondent (e.g., what class they are evaluating, which location they visited) without hardcoding those concepts into the database. 

This is achieved through **Dynamic URL Metadata Injection** combined with the Survey Engine's generic Data Collection fields.

---

## 2. The End-to-End Flow (Word for Word)

Here is exactly how an external system (like a University USIS Portal) can distribute customized survey URLs that automatically pre-fill backend data.

### Step 1: The Admin Creates a Single Survey Campaign
The Administrator creates a single, generic `Faculty Evaluation` campaign in the Survey Engine.
Inside the **Data Collection** settings tab for that Campaign, they add arbitrary custom fields. They can explicitly toggle these fields to be **Hidden** from the responder if they don't want the user to see or edit the tracking data.

*   Key: `course_section` (Type: TEXT, Hidden: true)
*   Key: `faculty_id` (Type: TEXT, Hidden: true)

*(Note: The `hidden` UI toggle is pending frontend UI implementation, but the backend and URL pipeline fully supports the data structure).*

### Step 2: The University Software Distributes the Links
The Survey Engine itself does *not* manage the university class roster. Your custom University Application (e.g., USIS) knows that Student Alice is in `CSE101 (Section 3)` taught by `FAC-99`.

The USIS application automatically injects this data into the survey URL when generating the "Evaluate Faculty" button on Alice's student dashboard:

```text
https://surveys.university.edu/s/fac-eval-campaign-id?metadata.course_section=CSE101-SEC3&metadata.faculty_id=FAC-99
```

### Step 3: The Survey Engine Captures the Context (The Svelte Magic)
When Alice clicks that link, she takes the single generic "Faculty Evaluation" survey. 

Because of the URL parsing logic implemented in the `s/[id]/+page.svelte` application, the following happens the exact second the page loads:
1. The Svelte application loops through the URL query string and spots the two parameters starting with the `metadata.` prefix.
2. It strips the prefix and injects the payload `{ "course_section": "CSE101-SEC3", "faculty_id": "FAC-99" }` directly into the live form's reactive memory state.
3. Because the Data Collection `<Input>` tags are two-way bound to that state, the inputs are instantly typed out and pre-filled for Alice.
4. If those fields are configured as "Hidden", Alice doesn't even see them. They just live securely in the JavaScript memory.
5. When Alice clicks submit, those values are sent straight up to the PostgreSQL JSONB row (`respondent_metadata`).

### Step 4: Grouping & Segregation (The Analytics Dashboard)
When the Admin loads the Campaign Analytics or Responses table in your Survey Engine frontend, they use the dynamic filters.

Under the "Segment Data By:" inputs at the top of the analytics page, they simply type `FAC-99` into the `faculty_id` filter box. The engine executes a native JSON-extraction query (using PostgreSQL's `->>` operator):

```sql
SELECT * FROM survey_response 
WHERE respondent_metadata ->> 'faculty_id' = 'FAC-99';
```

---

## 3. Resolving "Ugly IDs" for Administrators

Relying on an admin to memorize `FAC-99` or UUIDs to filter analytics is terrible UX. To solve this while keeping the Engine 100% generic, the Survey Engine uses **External Lookup Dictionaries** (The Data Dictionary Webhook approach).

**How it works seamlessly:**
1. In the Campaign Settings, when the Admin creates the Custom Field `faculty_id`, they define an optional **External Lookup URL**: `https://api.university.app/lookup/faculty`.
2. When the Admin loads the Analytics Page in the Survey Engine:
    * The Survey Engine reaches out to that University URL.
    * The University URL returns a JSON dictionary mapping the ugly IDs to pretty names: 
      `[{"id": "FAC-99", "label": "Dr. Alan Smith"}, {"id": "FAC-12", "label": "Dr. Sarah Jones"}]`.
3. The Survey Engine Svelte UI renders a beautiful searchable dropdown: `[ Select Faculty ▼ ]`.
4. It displays "Dr. Alan Smith", but filters the database by "FAC-99" under the hood.

This keeps the Engine completely blind to domain logic while providing a flawless, native-feeling UX for the Administrator. If a hospital buys the software, they simply plug in their `https://hospital.app/lookup/doctors` endpoint, and it instantly works for them too without a single line of Java or Svelte changing!

---

## 4. Enterprise Grade Private Auth State Handoff

If a campaign requires **Private Authentication (OIDC)**, respondents cannot immediately view the survey. They must be redirected to the University SSO and back. Standard redirects drop all `?metadata.*` URL parameters, and passing them blindly in the explicit callback URI (`redirect_uri`) is a security risk (vulnerable to tampering, HTTP Referer leaks, and URL length limits).

To securely persist dynamic contextual metadata across the login jump, the Survey Engine uses the **Enterprise OIDC `state` Parameter Pattern**.

### The Secure Handoff Flow:

1. **The Svelte App Captures Data**
   Alice clicks: `https://surveys.university.edu/s/fac-eval?metadata.faculty_id=FAC-99`
   Svelte places `{ "faculty_id": "FAC-99" }` directly into memory.

2. **The Svelte App Calls the Survey Backend**
   Instead of appending the metadata to a callback URL, Svelte sends the metadata as a JSON body to the Survey Engine backend to request a login URL:
   ```json
   POST /api/v1/auth/respondent/oidc/start
   {
       "campaignId": "fac-eval",
       "respondentMetadata": { "faculty_id": "FAC-99" }
   }
   ```

3. **The Survey Backend Encrypts the State**
   The Survey Engine Java Backend takes that JSON payload and generates a secure, short-lived **JWE (JSON Web Encryption) token** or stores it in a cache keyed by a random UUID. This becomes the secure state hash: `STATE_HASH = "eyJSZ...encrypted...string"`.
   
   The Java Backend returns the SSO Authorization URL to Svelte, utilizing the hash:
   `https://sso.university.edu/authorize?client_id=survey-engine&redirect_uri=...&state=eyJSZ...encrypted...string`

4. **The User Authenticates at the SSO**
   Alice logs into the University SSO. The Survey Engine is completely out of the picture. The external SSO knows *nothing* about the underlying metadata; it just strictly holds onto that opaque `state` string.

5. **The SSO Redirects Back with the Token and State**
   Alice logs in successfully. The SSO redirects her back exactly as the OIDC spec demands:
   `https://surveys.university.edu/api/v1/auth/callback?code=AUTH_CODE_123&state=eyJSZ...encrypted...string`

6. **The Survey Backend Decodes and Rehydrates**
   The Survey Engine Backend receives the `auth_code` and the `state`. 
   It cryptographically cracks open the `state` hash, retrieves the original metadata `{ "faculty_id": "FAC-99" }`, and attaches it directly to Alice's active server-side session.

7. **The Svelte App Loads Flawlessly**
   The Backend redirects Alice's browser one final time to the clean survey URL:
   `https://surveys.university.edu/s/fac-eval` (No messy query parameters needed!)
   When she completes the survey, the Backend automatically merges the `faculty_id` from her secure session into her immutable database submission row.
