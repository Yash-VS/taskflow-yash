# TaskFlow — Engineering Take-Home Assignment

TaskFlow is a minimal but powerful task management system designed to streamline project tracking and collaboration. Built with a focus on polished UI, robust security, and scalable architecture.
Bonus Points
## 1. I have added integration tests using in-memory h2 DB.
## 2. Drag and Drop kanban board for tickets.
## 3. Session storage for token for better UI/UX.
## 4. Swagger has been integrated as well.
## 5. 

## 1. Overview
TaskFlow allows users to register, log in, create projects, and manage tasks. It features a modern, responsive interface inspired by Zomato's design language.

- **Frontend**: React 18, Vite, TypeScript, Vanilla CSS (Custom Design System).
- **Backend**: Java 21, Spring Boot 3.3, Spring Security, JWT.
- **Database**: PostgreSQL 16.
- **Migration**: Liquibase.
- **Infrastructure**: Docker & Docker Compose.

---

## 2. Setup & Running Locally

### **Case 1: Using Docker (Recommended)**
This is the fastest way to get the full stack running with zero local installations dependencies (besides Docker).

**Prerequisites**: [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.

1. **Start the Stack**:
   ```powershell
   docker compose up --build
   ```
2. **Access the Application**:
   - **Frontend UI**: [http://localhost:3000](http://localhost:3000)
   - **Swagger API Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. **Database Info**:
   - **Host**: `localhost`
   - **Port**: `5432`
   - **User/DB/Pass**: `taskflow` / `taskflow` / `taskflow123`

---

### **Case 2: Manual Setup (No Docker)**
Use this if you prefer running services directly on your host machine.

#### **Pre-requisites**
- **Java**: JDK 21+
- **Database**: PostgreSQL 16+ (Running locally on port `5432`)
- **Build Tool**: Maven 3.9+
- **Node.js**: v20+ & npm

#### **Step 1: Database Setup**
1. Create a database named `taskflow`.
2. Ensure a user `taskflow` with password `taskflow123` has permissions.
3. Migrations will run automatically when the backend starts.

#### **Step 2: Start Backend**
```powershell
cd backend
mvn clean spring-boot:run
```
*API will be available at http://localhost:8080*

#### **Step 3: Start Frontend**
```powershell
cd frontend
npm install
npm run dev
```
*UI will be available at http://localhost:5173 (Vite default dev port)*

---

## 3. Test Credentials
The database is pre-seeded with test data. You can log in immediately:

- **Email**: `test@example.com`
- **Password**: `password123`

*(Other users like `akriti@zomato.com` and `albinder@blinkit.com` are also available with the same password).*

---

## 4. Architecture Decisions
- **Backend: Spring Boot + Java 21**: Leveraging mature ecosystem of Spring Security and JPA.
- **Frontend: React + Vanilla CSS**: Built a custom design system using CSS Variables for 100% control over aesthetics (vibrant reds, soft shadows).
- **State Management**: React Query for server-state, providing caching and optimistic UI updates.
- **Database**: Liquibase for schema management to ensure consistency across environments.

## 5. API Reference
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Create a new account |
| `POST` | `/auth/login` | Get JWT token |
| `GET` | `/projects` | List accessible projects |
| `POST` | `/projects` | Create a new project |
| `GET` | `/projects/{id}` | Get project details + tasks |
| `PATCH` | `/tasks/{id}` | Update task status/assignee/etc. |
| `DELETE` | `/tasks/{id}` | Remove a task |

## 6. What I'd Do With More Time
1. **Real-time Updates**: Implement WebSockets or SSE for task updates.
2. **Pagination**: Add proper paging to list endpoints.
3. **Enhanced Testing**: Increase test coverage with unit and E2E tests.
