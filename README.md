# Backend Intern Assignment – Course Platform API

## Goal

Build a backend service for a learning platform where users can:

* Browse and search course content (public)
* Enroll in courses (authenticated)
* Track learning progress by marking subtopics as completed (authenticated)

The focus is on **clean backend design, search, and business logic**, not UI.

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

---

## Search Functionality (Core Feature)

Users should be able to search using free-text queries.

Search should match against:

* Course titles and descriptions
* Topic titles
* Subtopic titles
* Subtopic content (Markdown text)

Search behavior should:

* Be case-insensitive
* Support partial matches (e.g. `velo` → `velocity`)
* Tolerate small spelling mistakes (e.g. `physcs` → `physics`)
* Prefer matches in titles over matches deep in content

Implementation options:

* PostgreSQL search (acceptable)
* Elasticsearch (recommended and evaluated more favorably)

Search endpoints must be **public**.

---

## Course Enrollment

* A user can enroll in a course
* A user cannot enroll in the same course more than once
* Enrollment is required before tracking progress

---

## Progress Tracking

### Mark Subtopic as Completed

* Users can mark a subtopic as completed
* Allowed only if the user is enrolled in the parent course
* Operation must be idempotent (safe to repeat)

---

### View Progress

Users should be able to see:

* Total subtopics in a course
* Completed subtopics
* Completion percentage

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

---

## Bonus (Optional)

The following are optional:

### Elasticsearch

* Use Elasticsearch for search
* Rank title matches higher than content matches
* Improve relevance

### Semantic Search

* Return results based on **meaning**, not just keywords
  Examples:

  * “laws of motion” → Newton’s Laws
  * “rate of change” → velocity / acceleration
* A simple embedding-based or heuristic approach is sufficient
* No advanced NLP required
* Must be explained briefly in the README

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
