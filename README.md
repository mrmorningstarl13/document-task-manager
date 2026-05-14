# Document & Task Management Platform

A Spring Boot backend application for managing documents, tasks, and projects within a company. Built as an internship evaluation project.

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.x**
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Flyway** — database migrations
- **MinIO** — file storage
- **Docker + Docker Compose**
- **Lombok**

---

## Prerequisites

- Docker Desktop installed and running
- Java 21
- Maven

---

## Setup & Running

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/document-task-manager.git
cd document-task-manager
```

### 2. Create the `.env` file in the project root

Copy the `.env.example` file:

```bash
cp .env.example .env
```

Then edit `.env` with your own values.

### 3. Run with Docker Compose

```bash
docker compose up --build
```

The app will start on `http://localhost:8080`

MinIO Console is available at `http://localhost:9001` (login with your `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY`)

### 4. Seed data

The following are inserted automatically on first run:
- Admin account: `admin@gmail.com` / `admin`
- Sample project with 4 tasks

---

## Authentication

All protected endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Obtain a token by calling `POST /api/auth/login` or `POST /api/auth/register`.

---

## API Endpoints

### Auth — public

| Method | Endpoint | Description                                 |
|--------|----------|---------------------------------------------|
| POST | `/api/auth/register` | Register a new user and receive a JWT token |
| POST | `/api/auth/login` | Login and receive a JWT token               |

**Register request:**
```json
{
    "email": "john@gmail.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
}
```

**Login request:**
```json
{
    "email": "john@gmail.com",
    "password": "password123"
}
```

**Auth response:**
```json
{
    "token": "eyJhbGc..."
}
```

---

### Users — authenticated

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get own profile |
| PATCH | `/api/users/me` | Update own profile |
| GET | `/api/users/me/projects` | Get projects the user belongs to |

**Update profile request (all fields optional):**
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "newemail@gmail.com",
    "password": "newpassword123"
}
```

### Users — admin only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/admin` | List all users |
| PUT | `/api/users/admin/{id}/role` | Change a user's role |
| PUT | `/api/users/admin/{id}/deactivate` | Deactivate a user |

**Change role request:**
```json
{
    "role": "ADMIN"
}
```

---

### Projects — authenticated

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects` | Create a new project |
| PATCH | `/api/projects/{id}` | Update a project (owner only) |
| DELETE | `/api/projects/{id}` | Soft delete a project (owner only) |
| GET | `/api/projects/my` | Get my projects (owned + member of) |
| POST | `/api/projects/{id}/members` | Add a member to a project (owner only) |

### Projects — admin only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List all active projects |
| GET | `/api/projects?includeDeleted=true` | List all projects including deleted |

**Create project request:**
```json
{
    "name": "Website Redesign",
    "description": "Redesign the company website"
}
```

**Update project request (all fields optional):**
```json
{
    "name": "New Name",
    "description": "New description",
    "status": "ARCHIVED"
}
```

Available statuses: `ACTIVE`, `ARCHIVED`, `COMPLETED`

**Add member request:**
```json
{
    "userId": 2
}
```

---

### Tasks — authenticated

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects/{projectId}/tasks` | Create a task |
| PATCH | `/api/projects/{projectId}/tasks/{taskId}` | Update a task |
| DELETE | `/api/projects/{projectId}/tasks/{taskId}` | Delete a task |
| GET | `/api/projects/{projectId}/tasks` | Get all tasks for a project |
| GET | `/api/projects/{projectId}/tasks?status=NEW` | Filter tasks by status |
| GET | `/api/projects/{projectId}/tasks?priority=HIGH` | Filter tasks by priority |
| GET | `/api/projects/{projectId}/tasks?status=NEW&priority=HIGH` | Filter by both |
| GET | `/api/projects/{projectId}/tasks/my` | Get tasks assigned to me |
| PUT | `/api/projects/{projectId}/tasks/{taskId}/assign/{userId}` | Assign a user to a task |

**Create task request:**
```json
{
    "title": "Set up CI/CD pipeline",
    "description": "Configure GitHub Actions",
    "priority": "HIGH",
    "deadline": "2026-06-01T12:00:00",
    "assignedToId": 1
}
```

**Update task request (all fields optional):**
```json
{
    "title": "Updated title",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "deadline": "2026-06-15T12:00:00",
    "assignedToId": 2
}
```

Available priorities: `LOW`, `MEDIUM`, `HIGH`

Available statuses: `NEW`, `IN_PROGRESS`, `DONE`

---

### Documents — authenticated

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects/{projectId}/documents` | Upload a document |
| GET | `/api/projects/{projectId}/documents` | List documents in a project |
| GET | `/api/projects/{projectId}/documents/{documentId}/download` | Download a document |
| DELETE | `/api/projects/{projectId}/documents/{documentId}` | Delete a document |

**Upload:** Send as `multipart/form-data` with key `file`.

Allowed file types: `pdf`, `png`, `jpeg`, `doc`, `docx`

Max file size: `10MB`

---

### Audit Logs — admin only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/audit-logs` | Get all audit logs |
| GET | `/api/admin/audit-logs?action=USER_LOGIN` | Filter logs by action |
| GET | `/api/admin/audit-logs/users/{userId}` | Get logs for a specific user |

Available actions: `USER_LOGIN`, `DOCUMENT_UPLOAD`, `DOCUMENT_DELETE`, `PROJECT_DELETE`, `USER_DEACTIVATE`

---

## Edge Cases & Business Rules

### Authentication
- Passwords must be at least 8 characters
- Registering with an already used email returns `400 Bad Request`
- Login with a non-existent email returns `404 Not Found`
- Login with wrong password returns `401 Unauthorized`
- Deactivated users cannot log in — `403 Forbidden`
- Deactivated users with an existing token cannot make requests — `403 Forbidden`

### Users
- Only the admin can change user roles or deactivate users
- A user can update their own email — duplicate email check is enforced

### Projects
- Any authenticated user can create a project and becomes its owner
- Only the project owner can add members, update or delete the project
- Deleted projects use soft delete — `deleted_at` is set instead of removing the row
- Admins can view all projects including soft deleted ones via `?includeDeleted=true`

### Tasks
- Only project members and the project owner can create tasks
- Tasks can be updated or deleted by the task owner, project owner or admin
- Assigning a user to a task is done via `PATCH /api/projects/{projectId}/tasks/{taskId}` using the `assignedToId` field
- Tasks can only be assigned to users who are members of the project
- New tasks always start with status `NEW`

### Documents
- Files are stored in MinIO, only metadata is saved in the database
- Only project members and the project owner can upload or view documents
- Only the document owner, project owner or admin can delete a document
- Maximum file size is 10MB
- Allowed types: PDF, PNG, JPEG, DOC, DOCX

### Audit Logging
- The following operations are automatically logged: login, register, document upload, document deletion, project deletion, user deactivation
- Login and Register events include the IP address of the request
- Only admins can view audit logs

---

## Project Structure

```
src/main/java/leo/dev/doc_task_management/
├── config/
│   ├── SecurityConfig.java
│   ├── MinioConfig.java
│   └── JwtAuthenticationFilter.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   ├── DocumentController.java
│   └── AuditLogController.java
├── dto/
│   ├── request/
│   └── response/
├── entity/
│   ├── User.java
│   ├── Project.java
│   ├── Task.java
│   ├── Document.java
│   ├── AuditLog.java
│   ├── Role.java
│   ├── TaskStatus.java
│   └── TaskPriority.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── UserNotFoundException.java
│   ├── ProjectNotFoundException.java
│   ├── TaskNotFoundException.java
│   ├── DocumentNotFoundException.java
│   ├── EmailAlreadyInUseException.java
│   └── ForbiddenException.java
├── repository/
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   ├── TaskRepository.java
│   ├── DocumentRepository.java
│   └── AuditLogRepository.java
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── ProjectService.java
    ├── TaskService.java
    ├── DocumentService.java
    ├── AuditLogService.java
    ├── JwtService.java
    ├── MinioService.java
    └── UserDetailsServiceImpl.java

src/main/resources/
├── db/migration/
│   ├── V1__create_users_table.sql
│   ├── V2__create_projects_table.sql
│   ├── V3__create_project_members_table.sql
│   ├── V4__create_audit_log_table.sql
│   ├── V5__create_tasks_table.sql
│   ├── V6__create_documents_table.sql
│   ├── V7__insert_admin.sql
│   └── V8__insert_sample_data.sql
└── application.properties
```

---

## Database Schema

```
users
projects        → references users (owner_id)
project_members → references users + projects
tasks           → references users (created_by, assigned_to) + projects
documents       → references users (owner_id) + projects
audit_log       → references users
```

---

## Notes

- JWT tokens expire after 24 hours
- Logout is handled client-side by discarding the token — JWT is stateless so no server-side invalidation is needed
- Project deletion is soft delete — data is preserved for audit purposes
- Task deletion is hard delete
- All timestamps are in UTC
