# FitTracker

FitTracker is organized as two applications: a Spring Boot API in `backend/` and an Angular 22 web app in `frontend/`, with PostgreSQL storage.

## Requirements

- Java 21
- Node.js 22.22.3 or newer on the Node 22 line (or another version supported by Angular 22)
- npm 10 or newer
- Docker Compose

## Start PostgreSQL

Copy `.env.example` to `.env` (change the local credentials if you like), then start the database. Spring Boot imports this local properties file when launched from the repository root.

```powershell
Copy-Item .env.example .env
docker compose up -d postgres
```

For a local Spring Boot process, configure `DB_URL=jdbc:postgresql://localhost:5432/fittracker`, `DB_USER`, and `DB_PWD` in the process environment. Flyway creates the schema from `backend/src/main/resources/db/migration`; Hibernate validates the migrated schema at startup.

The Compose service uses `fittracker` as the development database/user unless overridden. The checked-in `.env.example` is only for local development. Set a private random `APP_JWT_SECRET`, `APP_AUTH_COOKIE_SECURE=true`, and your OpenAI API key in production. Never commit `.env` or production credentials.

## Start the API

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API is available on `http://localhost:8080`. Swagger UI is at `/swagger-ui.html`.

## Start the Angular app

```powershell
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. Angular proxies `/api` to the Spring Boot server so the authentication cookies and CSRF token remain same-origin from the browser's perspective.

## Authentication and roles

Public registration always creates a `USER`. The first `ADMIN` should be provisioned by an operator after registration, for example with a one-time database update:

```sql
UPDATE fitness_user SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

Sign out and sign in again after changing a role so a new role claim is issued. Admin API routes are restricted to `ADMIN`; the first web experience is focused on regular users.

## Recommendations

Set `OPENAI_API_KEY` for AI-generated advice. The key stays on the backend. Without it, recommendation generation returns a service-unavailable response; activity tracking and the rest of the dashboard continue to work.

## Production deployment: Neon, Render, and Vercel

The Angular app is hosted as a static site on Vercel. The Spring Boot API runs as a Docker web service on Render. Neon supplies PostgreSQL. Vercel forwards `/api/*` requests to Render, so the browser continues to use same-origin URLs and the existing HTTP-only auth and CSRF cookies work without cross-origin cookie settings.

### 1. Create the Neon database

1. Create a Neon project and database, then open **Connect** in the Neon console.
2. Copy the pooled connection details (the hostname containing `-pooler` is appropriate for a web service) and keep the database name, username, and password.
3. Form the JDBC URL as `jdbc:postgresql://HOST/DB_NAME?sslmode=require`, using the Neon hostname and database name exactly. Set this plus the username and password on Render in the next step. Flyway applies the checked-in migrations on first startup.

### 2. Create the Render API service

1. Push this repository to GitHub and choose **New + → Blueprint** in Render. Connect the repository and select `main`; Render reads [`render.yaml`](render.yaml).
2. When prompted, enter Neon values for `DB_URL`, `DB_USER`, and `DB_PWD`. Keep the generated `APP_JWT_SECRET` and `APP_AUTH_COOKIE_SECURE=true`. Add `OPENAI_API_KEY` in Render's Environment page only if you want AI recommendations.
3. Deploy the Blueprint. It builds from [`backend/Dockerfile`](backend/Dockerfile), runs database migrations, and checks `/actuator/health`.
4. Note the service URL, normally `https://fittracker-api.onrender.com`. If Render assigns a different host, update `destination` in [`frontend/vercel.json`](frontend/vercel.json) to `https://YOUR-RENDER-HOST/api/:path*` before deploying the frontend.

Keep Neon and Render in nearby regions to reduce database latency. Do not use the local development credentials or JWT secret in production.

### 3. Create the Vercel frontend project

1. Import the same GitHub repository into Vercel.
2. Set **Root Directory** to `frontend`. Use Node.js 22. Vercel reads `frontend/vercel.json`; the build command is `npm run build` and output is `dist/fittracker-web/browser`.
3. Deploy. Confirm the Vercel site can reach `/api/auth/csrf` through the rewrite and then test registration, login, and logout. The Render origin is proxied behind the Vercel domain, so no backend CORS allowlist is needed.

### 4. Turn on merge-to-main CI/CD

The workflow in [`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs the backend integration tests against a temporary PostgreSQL service and builds the Angular app for pull requests and pushes to `main`.

1. In GitHub, open **Settings → Branches** (or the repository ruleset) and protect `main`: require pull requests and require the `Backend tests` and `Frontend build` checks before merging.
2. Keep the Render service connected to `main`. Its Blueprint uses `autoDeployTrigger: checksPass`, so Render deploys a main commit after GitHub checks pass.
3. In Vercel project **Settings → Git**, set `main` as the Production Branch and leave Git deployments enabled. Vercel will build/deploy production on main updates and make preview deployments for branches/PRs.

After these steps, merge a PR into `main`: CI validates both apps, Render deploys the API, and Vercel publishes the Angular frontend. If your Vercel plan or repository integration does not wait on GitHub checks, the branch protection rule still prevents unvalidated changes from reaching `main`.
