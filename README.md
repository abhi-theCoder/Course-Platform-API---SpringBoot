# Backend Intern Assignment – Course Platform API

## Goal

Build a backend service for a learning platform where users can:

* Browse and search course content (public)
* Enroll in courses (authenticated)
* Track learning progress by marking subtopics as completed (authenticated)

The focus is on **clean backend design, search, and business logic**, not UI.

---

## Time Expectation

This assignment should take approximately **8 hours** to complete. If you find yourself spending significantly more time, you may be over-engineering. Focus on core functionality first, then add bonuses if time permits.

---

## Tech Stack (Required)
* Java 17+
* Spring Boot
* PostgreSQL
* JPA / Hibernate
* Spring Security (JWT)
* Swagger / OpenAPI
* **Public deployment with Swagger UI (mandatory)**

Optional (bonus):
* Elasticsearch

---

## Core Domain Model (Conceptual)

* **Course**

  * title, description
  * contains Topics

* **Topic**

  * title
  * belongs to a Course
  * contains Subtopics

* **Subtopic**

  * title
  * content in **Markdown**
  * belongs to a Topic

* **User**

  * email, password

* **Enrollment**

  * user ↔ course

* **Subtopic Progress**

  * user ↔ subtopic
  * completed / completedAt

---

## Seed Data (Mandatory)

* Course data is provided as JSON (you will add this to the repo)
* On application startup:

  * If the database is empty → load seed data
  * If data exists → do nothing
* Seed data must include:

  * Courses
  * Topics
  * Subtopics
  * Markdown content for each subtopic

Courses and content are **read-only** (no creation or updates via APIs).

---

## Authentication

* Users can:

  * Register
  * Log in and receive a JWT
* JWT is required for all **user-specific actions**

### Example: Register

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "student@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "id": 1,
  "email": "student@example.com",
  "message": "User registered successfully"
}
```

### Example: Login

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "student@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "student@example.com",
  "expiresIn": 86400
}
```

---

## Public vs Authenticated APIs

### Public (No Authentication Required)

These must work immediately in Swagger:

* View all courses
* View a course by ID
* Search courses and content

This allows reviewers to test search first.

---

### Authenticated (JWT Required)

Authentication is required for:

* Enrolling in a course
* Marking subtopics as completed
* Viewing enrollments and progress

---

## Course Browsing

* List all courses
* View a single course with:

  * topics
  * subtopics
  * markdown content

### Example: List All Courses

**Request:**
```http
GET /api/courses
```

**Response:**
```json
{
  "courses": [
    {
      "id": "physics-101",
      "title": "Introduction to Physics",
      "description": "Fundamental concepts of motion, forces, and energy.",
      "topicCount": 3,
      "subtopicCount": 9
    },
    {
      "id": "math-101",
      "title": "Basic Mathematics for Problem Solving",
      "description": "Core mathematical concepts used in science and everyday problem solving.",
      "topicCount": 3,
      "subtopicCount": 9
    }
  ]
}
```

### Example: Get Course by ID

**Request:**
```http
GET /api/courses/physics-101
```

**Response:**
```json
{
  "id": "physics-101",
  "title": "Introduction to Physics",
  "description": "Fundamental concepts of motion, forces, and energy.",
  "topics": [
    {
      "id": "kinematics",
      "title": "Kinematics",
      "subtopics": [
        {
          "id": "speed",
          "title": "Speed",
          "content": "Speed is the distance travelled per unit time.\n\nIt is a scalar quantity..."
        },
        {
          "id": "velocity",
          "title": "Velocity",
          "content": "Velocity is the rate of change of displacement..."
        }
      ]
    }
  ]
}
```

---

## Search Functionality (Core Feature)

Users should be able to search using free-text queries.

Search should match against:

* Course titles and descriptions
* Topic titles
* Subtopic titles
* Subtopic content (Markdown text)

### Required Search Behavior

* **Case-insensitive matching** (mandatory)
* **Partial matches** (e.g. `velo` → `velocity`) (mandatory)
* Return courses that contain matching content

### Bonus Search Features

* **Fuzzy matching** to tolerate spelling mistakes (e.g. `physcs` → `physics`)
* **Ranking/scoring** to prefer matches in titles over matches in content

### Implementation Options

* **PostgreSQL full-text search** (acceptable baseline)
* **Elasticsearch** (bonus - evaluated more favorably)

Search endpoints must be **public**.

### Example Search Queries

* `"velocity"` → should return Physics course (matches subtopic title and content)
* `"Newton"` → should return Physics course (matches in dynamics topic)
* `"rate of change"` → should return Math course (matches in functions topic)

### Example Search API

**Request:**
```http
GET /api/search?q=velocity
```

**Response:**
```json
{
  "query": "velocity",
  "results": [
    {
      "courseId": "physics-101",
      "courseTitle": "Introduction to Physics",
      "matches": [
        {
          "type": "subtopic",
          "topicTitle": "Kinematics",
          "subtopicId": "velocity",
          "subtopicTitle": "Velocity",
          "snippet": "Velocity is the rate of change of displacement..."
        },
        {
          "type": "content",
          "topicTitle": "Work and Energy",
          "subtopicId": "kinetic-energy",
          "subtopicTitle": "Kinetic Energy",
          "snippet": "...depends on the mass and the square of the velocity..."
        }
      ]
    }
  ]
}
```

---

## Course Enrollment

* A user can enroll in a course
* A user cannot enroll in the same course more than once
* Enrollment is required before tracking progress

**Example Request:**
```http
POST /api/courses/{courseId}/enroll
Authorization: Bearer <jwt-token>
```

**Example Response:**
```json
{
  "enrollmentId": 123,
  "courseId": "physics-101",
  "courseTitle": "Introduction to Physics",
  "enrolledAt": "2025-12-21T09:00:00Z"
}
```

**Error Response (already enrolled):**
```json
{
  "error": "Already enrolled",
  "message": "You are already enrolled in this course"
}
```

---

## Progress Tracking

### Mark Subtopic as Completed

* Users can mark a subtopic as completed
* Allowed only if the user is enrolled in the parent course
* Operation must be idempotent (safe to repeat)

**Example Request:**
```http
POST /api/subtopics/{subtopicId}/complete
Authorization: Bearer <jwt-token>
```

**Example Response:**
```json
{
  "subtopicId": "velocity",
  "completed": true,
  "completedAt": "2025-12-21T10:30:00Z"
}
```

---

### View Progress

Users should be able to view their progress for a specific enrollment.

**Endpoint:** `GET /api/enrollments/{enrollmentId}/progress`

**Response should include:**

* Course details (id, title)
* Total subtopics count
* Completed subtopics count
* Completion percentage
* List of completed subtopics with completion timestamps

**Example Response:**
```json
{
  "enrollmentId": 123,
  "courseId": "physics-101",
  "courseTitle": "Introduction to Physics",
  "totalSubtopics": 9,
  "completedSubtopics": 5,
  "completionPercentage": 55.56,
  "completedItems": [
    {
      "subtopicId": "speed",
      "subtopicTitle": "Speed",
      "completedAt": "2025-12-20T14:20:00Z"
    },
    {
      "subtopicId": "velocity",
      "subtopicTitle": "Velocity",
      "completedAt": "2025-12-21T10:30:00Z"
    }
  ]
}
```

---

## API Documentation (Mandatory)

* All APIs must be documented using Swagger / OpenAPI
* Swagger UI must be enabled in the deployed application
* A reviewer must be able to test **everything using Swagger alone**

---

## Deployment & Demo (Mandatory)

Your submission **must** include a publicly accessible deployment.

The deployed application must:

* Be reachable via a public URL
* Have Swagger UI enabled
* Automatically load seed data
* Allow a reviewer to:

  1. Search courses (without login)
  2. Register and log in
  3. Authorize using JWT in Swagger
  4. Enroll in a course
  5. Mark subtopics as completed
  6. View progress

Submissions that do not meet these requirements **will not be evaluated**.

### Recommended Deployment Platforms (Free Tier)

* **Railway** - Easy PostgreSQL + Spring Boot deployment
* **Render** - Supports PostgreSQL and Java applications
* **Fly.io** - Good for containerized applications
* **Heroku** - Classic option (limited free tier)

Choose any platform that allows public access to your Swagger UI.

---

## Bonus (Optional)

The following are optional:

### Elasticsearch

* Use Elasticsearch for search
* Rank title matches higher than content matches
* Improve relevance

### Semantic Search Using Embeddings (Advanced Bonus – Optional)

As an advanced bonus, you may implement semantic search using text embeddings.

A simple and acceptable approach is to:

* Use a pre-trained local sentence embedding model
* Generate embeddings for subtopic content after seed data is loaded
* Generate an embedding for the search query
* Compare embeddings using cosine similarity
* Return the most relevant results

You do not need to:

* Train any models
* Use paid APIs
* Use vector databases
* Optimize for performance

A simple, demonstrative implementation is sufficient.

---

## What NOT to Implement

To keep the scope manageable, **do not implement** the following:

* ❌ User profile management (beyond basic registration/login)
* ❌ Course/topic/subtopic CRUD operations (content is read-only from seed data)
* ❌ Admin panel or admin users
* ❌ Email verification or password reset
* ❌ User roles beyond basic authentication

Focus on the core features: **browsing, searching, enrolling, and progress tracking**.

---

## Error Handling Requirements

Your API should handle errors gracefully and return appropriate HTTP status codes:

### Required Status Codes

* `200 OK` - Successful GET requests
* `201 Created` - Successful POST requests (enrollment, registration)
* `400 Bad Request` - Invalid input (missing fields, invalid format)
* `401 Unauthorized` - Missing or invalid JWT token
* `403 Forbidden` - Valid token but insufficient permissions
* `404 Not Found` - Resource doesn't exist
* `409 Conflict` - Duplicate enrollment, email already exists

### Error Response Format

All errors should return a consistent JSON structure:

```json
{
  "error": "Error Type",
  "message": "Human-readable description of what went wrong",
  "timestamp": "2025-12-21T10:30:00Z"
}
```

### Example Error Scenarios

**Enrolling without authentication:**
```json
{
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid",
  "timestamp": "2025-12-21T10:30:00Z"
}
```

**Marking subtopic complete without enrollment:**
```json
{
  "error": "Forbidden",
  "message": "You must be enrolled in this course to mark subtopics as complete",
  "timestamp": "2025-12-21T10:30:00Z"
}
```

**Course not found:**
```json
{
  "error": "Not Found",
  "message": "Course with id 'invalid-course' does not exist",
  "timestamp": "2025-12-21T10:30:00Z"
}
```

---

## Evaluation Criteria

We will evaluate:

* Data modeling & relationships
* Search behavior and correctness
* Authentication & authorization
* Business logic (enrollment & progress)
* Code structure and readability
* Ease of testing via Swagger
* Clarity of README

---

## Submission

Please provide:

1. A GitHub repository
2. A link to the deployed application
3. Any notes or explanations in the README

---
