# FitTracker

FitTracker is a workout logging web app with a Spring Boot API and an Angular frontend.

**Live site:** [Open FitTracker](https://fit-tracker-phi-weld.vercel.app)

**Dashboard:** [Open dashboard](https://fit-tracker-phi-weld.vercel.app/app) (sign-in required)

## Current features

- Register, sign in, and manage a personal account.
- Manually log an activity type, duration, start time, and optional calories.
- View recent activities and weekly duration, activity, and calorie summaries.
- Request activity-based recommendations when the backend has an OpenAI API key.

Activity logging is manual in the current version. The site does not currently read GPS, phone sensors, Gmail, an Amazfit watch, or other health platforms.

## How activity data is saved

1. A signed-in user submits the activity form on the Angular dashboard.
2. Angular sends the activity to `POST /api/activities` with its type, duration, start time, optional calories, and any additional metrics.
3. Vercel forwards `/api/*` requests to the Spring Boot API on Render. The API authenticates the request using the secure JWT cookie, associates the activity with that user, and saves it to PostgreSQL on Neon.
4. When the dashboard reloads, the API returns that user’s activities and computes the weekly summary from the saved entries. Activities are ordered by start time, newest first.

The backend runs Flyway migrations at startup and Hibernate validates the database schema. Activity metrics are stored in a PostgreSQL `jsonb` field.

## Technology

- **Frontend:** Angular 22, TypeScript, RxJS, SCSS
- **Backend:** Java 21, Spring Boot 4, Spring MVC, Spring Security, Spring Data JPA, Hibernate, Flyway
- **Database:** PostgreSQL; Neon in production and Docker Compose for local development
- **Hosting:** Vercel for the Angular site; Render for the Spring Boot API
- **CI:** GitHub Actions builds the frontend and runs backend tests for pull requests and pushes to `main`

## Run locally

Requirements: Java 21, Node.js 22, npm, and Docker Compose.

From the repository root, create the local environment file and start PostgreSQL:

```powershell
Copy-Item .env.example .env
docker compose up -d postgres
```

Start the API in a second terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Start the Angular app in another terminal:

```powershell
cd frontend
npm ci
npm start
```

Open `http://localhost:4200`. The Angular development server proxies `/api` to `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

The local `.env.example` values are for development only. Never commit `.env` or production credentials.

## Deployment

The repository includes [`render.yaml`](render.yaml), [`backend/Dockerfile`](backend/Dockerfile), and [`frontend/vercel.json`](frontend/vercel.json) for deployment configuration.

- **Neon:** Create a PostgreSQL database. Set the Render service variables `DB_URL`, `DB_USER`, and `DB_PWD` using Neon’s connection details. Keep the credentials separate from the JDBC URL; use `jdbc:postgresql://HOST/DATABASE?sslmode=require` for `DB_URL`.
- **Render:** Deploy the API from the repository root using `backend/Dockerfile`. Set `APP_JWT_SECRET` to a private secret and `APP_AUTH_COOKIE_SECURE=true`. Flyway creates or updates the schema at startup; `/actuator/health` is the health check.
- **Vercel:** Set the project root directory to `frontend`. The build command is `npm run build` and the output directory is `dist/fittracker-web/browser`. The `/api` rewrite currently targets `https://fittracker-wmrp.onrender.com`.
- **CI/CD:** The workflow in [`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs backend tests with a temporary PostgreSQL database and builds Angular. Connect Render and Vercel to the Git repository’s `main` branch for production deployments. Protect `main` with required CI checks before merging.

Set `OPENAI_API_KEY` in Render only if you want AI-generated recommendations. The key stays on the backend.
