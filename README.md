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
