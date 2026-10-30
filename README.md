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

See [Study Guide](STUDY-GUIDE.md) for the request flow, domain model, API routes, and study exercises.

## Current scope

This portfolio version has no authentication or per-driver data separation. The single-open-shift rule applies across the application. Hibernate updates the local schema for convenience; a production deployment should use versioned migrations, authentication, and role-based access.
