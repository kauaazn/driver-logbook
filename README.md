# Driver Logbook

Driver Logbook is a full-stack app for recording fleet vehicles, driver shifts, vehicle inspections, and damage reports. A Next.js interface communicates with a Java Spring Boot REST API, and PostgreSQL stores the records.

## Features

- Register fleet vehicles by number, model, and license plate.
- Start and end shifts with odometer readings and departure/return inspections.
- Enforce one active shift at a time and prevent an ending mileage below the starting mileage.
- Record vehicle damage and track its status.
- Browse shift history, filter by vehicle and starting date, summarize hours, and export CSV.
- Responsive interface with loading, empty, and API-offline states.

## Stack

- Frontend: Next.js, React, TypeScript, and CSS
- Backend: Java, Spring Boot, Spring Data JPA, and Maven
- Database: PostgreSQL
- Local database: Docker Compose

## Run locally

1. Start PostgreSQL with `docker compose up -d` from the project root.
2. Start the API from `backend` with `mvn spring-boot:run`. It listens on port 8080.
3. In another terminal, open `frontend`, run `npm install`, then `npm run dev`.
4. Open `http://localhost:3000`. The frontend expects the API at `http://localhost:8080/api` by default.

Set `DATABASE_URL`, `DATABASE_USER`, or `DATABASE_PASSWORD` to override local database settings. Set `NEXT_PUBLIC_API_URL` to point the frontend at another API URL.

## Free portfolio deployment

The deployment setup uses Vercel for the Next.js frontend, Render for the Spring Boot API, and Neon for managed PostgreSQL. The backend is packaged with [backend/Dockerfile](backend/Dockerfile). No deployment URLs or database credentials are committed to this repository.

### 1. Create the Neon database

Create a PostgreSQL project in [Neon](https://neon.tech/), then copy its connection details. In Render, use the Neon host and database name to set `DATABASE_URL` in JDBC form, for example `jdbc:postgresql://HOST/DATABASE?sslmode=require`; set `DATABASE_USER` and `DATABASE_PASSWORD` from Neon. Keep these credentials private and enter them only in the Render environment settings.

### 2. Deploy the backend to Render

Create a Web Service from this GitHub repository. Set **Root Directory** to `backend`, select **Docker** as the runtime, and use `Dockerfile` as the Dockerfile path. Set the health check path to `/api/health` and add:

| Variable | Value |
| --- | --- |
| `DATABASE_URL` | Neon JDBC URL, including `?sslmode=require` |
| `DATABASE_USER` | Neon database user |
| `DATABASE_PASSWORD` | Neon database password |
| `CORS_ALLOWED_ORIGIN` | The exact Vercel production origin, such as `https://your-app.vercel.app` (no trailing slash) |

Render provides `PORT`; Spring Boot reads it automatically. The free web service may sleep when idle, so the first request after inactivity can take longer. See [Render Web Services](https://render.com/docs/web-services), [Docker on Render](https://render.com/docs/docker), and [Render environment variables](https://render.com/docs/configure-environment-variables).

### 3. Deploy the frontend to Vercel

Import the repository in [Vercel](https://vercel.com/), set the project **Root Directory** to `frontend`, and deploy with the default Next.js settings. Add this production environment variable, replacing the hostname with the Render service URL:

| Variable | Value |
| --- | --- |
| `NEXT_PUBLIC_API_URL` | `https://your-api.onrender.com/api` |

Redeploy after changing environment variables. Then copy the Vercel production origin (scheme and host only, no path or trailing slash) into Render's `CORS_ALLOWED_ORIGIN` and redeploy the backend. The API permits browser requests only from that one configured origin; local development defaults to `http://localhost:3000`. Preview deployments use different origins and are not allowed by this production setting.

For Vercel's root directory and environment settings, see [Project Settings](https://vercel.com/docs/project-configuration/project-settings) and [Environment Variables](https://vercel.com/docs/environment-variables). Neon connection details are documented in [Connect to your database](https://neon.tech/docs/connect/connect-from-any-app).

After both services are live, add their real URLs to this README and the repository's GitHub About section. The free tiers have usage limits; check current provider plans before relying on the app for ongoing use.

See [Study Guide](docs/STUDY-GUIDE.md) for the request flow, domain model, API routes, and study exercises.

## Current scope

This portfolio version has no authentication or per-driver data separation. The single-open-shift rule applies across the application. Hibernate updates the local schema for convenience; a production deployment should use versioned migrations, authentication, and role-based access.
