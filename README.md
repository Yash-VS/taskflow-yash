# TaskFlow — Engineering Take-Home Assignment

TaskFlow is a minimal but powerful task management system designed to streamline project tracking and collaboration. Built with a focus on polished UI, robust security, and scalable architecture.

## 1. Overview
TaskFlow allows users to register, log in, create projects, and manage tasks. It features a modern, responsive interface inspired by Zomato's design language.

- **Frontend**: React 18, Vite, TypeScript, Vanilla CSS (Custom Design System).
- **Backend**: Java 21, Spring Boot 3.3, Spring Security, JWT.
- **Database**: PostgreSQL 16.
- **Migration**: Liquibase.
- **Infrastucture**: Docker & Docker Compose.

## 2. Architecture Decisions

### **Backend: Spring Boot + Java 21**
- **Decision**: Used Spring Boot instead of Go (referenced in requirements) to leverage the mature ecosystem of Spring Security and JPA.
- **Pattern**: Followed **Layered Architecture** (Controller -> Service -> Repository) to ensure Separation of Concerns (SoC).
- **Security**: Implemented stateless **JWT authentication**. Passwords are hashed using **BCrypt with a cost of 12**, exceeding the minimum requirement.
- **Validation**: Centralized error handling using `@RestControllerAdvice` to provide structured 400/401/403/404 responses.

### **Frontend: React + Vanilla CSS**
- **Decision**: Built a custom design system using **Vanilla CSS Variables** instead of a component library. This allowed for 100% control over the "Zomato-style" aesthetics (vibrant reds, soft shadows, glassmorphism).
- **State Management**: Used **React Query (TanStack Query)** for server-state management, providing out-of-the-box caching and optimistic UI updates.
- **Responsiveness**: Fully responsive layout using CSS Grid and Flexbox, optimized for both mobile (375px) and desktop (1280px).

### **Database: Liquibase Migrations**
- **Decision**: Used Liquibase for schema management. This ensures every developer (and the Docker container) starts with exactly the same database schema without manual SQL imports.

## 3. Running Locally
The entire stack is containerized for a zero-install experience.

**Prerequisites**: Docker Desktop installed.

```powershell
# 1. Clone the repository
git clone https://github.com/yashv/taskflow.git
cd taskflow

# 2. Setup environment (optional - defaults are provided)
cp .env.example .env

# 3. Start the full stack
docker compose up --build
```

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **API Server**: [http://localhost:8080](http://localhost:8080)
- **Swagger Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 4. Running Migrations
Migrations run **automatically** on container startup. No manual steps are required. 
If you need to run them manually for local development without Docker:
`mvn liquibase:update` (Requires local Postgres on port 5433).

## 5. Test Credentials
The database is pre-seeded with Zomato-themed test data. You can log in immediately:

- **Email**: `deepinder@zomato.com`
- **Password**: `password123`

*(Other users like `akriti@zomato.com` and `albinder@blinkit.com` are also available with the same password).*

## 6. API Reference
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Create a new account |
| `POST` | `/auth/login` | Get JWT token |
| `GET` | `/projects` | List accessible projects |
| `POST` | `/projects` | Create a new project |
| `GET` | `/projects/{id}` | Get project details + tasks |
| `PATCH` | `/tasks/{id}` | Update task status/assignee/etc. |
| `DELETE` | `/tasks/{id}` | Remove a task |

## 7. What I'd Do With More Time
1. **Real-time Updates**: Implement WebSockets or SSE to notify users when a task they are assigned to is updated.
2. **Pagination**: Add proper paging to the `/projects` and `/tasks` endpoints for better performance with large datasets.
3. **Enhanced Testing**: Increase test coverage to 80%+, including E2E tests using Playwright.
4. **Drag and Drop**: Although some logic is present, I'd fully polish the Kanban board drag-and-drop experience.
5. **Caching**: Implement Redis for JWT blacklisting or high-frequency query caching.
