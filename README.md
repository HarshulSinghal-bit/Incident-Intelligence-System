# Incident Intelligence Platform

## Overview

This project represents my transition from QA engineering to backend development.

While working as a QA engineer, I spent years analyzing failures, edge cases, and production issues. Instead of only testing systems, I wanted to understand how such systems are actually built — especially monitoring and incident management platforms.

Rather than building a simple CRUD application, I designed and implemented a backend system that simulates how modern incident intelligence platforms operate internally.

This project focuses on backend architecture, domain modeling, lifecycle management, and analytics.

---

## Problem Statement

In real-world systems, failures are rarely isolated. The same error may occur multiple times across environments, builds, or services.

A monitoring platform must:

- Accept incidents from external systems
- Detect duplicate failures intelligently
- Track lifecycle transitions
- Escalate recurring issues
- Provide analytics and trends
- Maintain audit history

This project attempts to solve these problems at the backend level.

---

## Architecture

The system follows a layered architecture:
Controller
↓
Service
↓
Domain Logic
↓
Repository
↓
PostgreSQL Database

### Design Principles

- Clear separation between DTOs and Entities
- Business logic inside service/domain layer
- Transaction-safe lifecycle updates
- SHA-256 based fingerprint deduplication
- Aggregation-driven analytics
- Extensible structure for future AI integration

---

## Development Phases

### Phase 1 — Core Foundation
- Entity modeling
- DTO separation
- Validation
- Global exception handling
- Clean layered structure

---

### Phase 2 — Deduplication Engine
- SHA-256 fingerprint generation
- Unique fingerprint constraint
- Occurrence counter tracking
- Last-seen timestamp updates

Recurring failures increment occurrence count instead of creating new rows.

---

### Phase 3 — Lifecycle Management
- State machine for status transitions
- Transition validation
- Status history tracking
- Resolved and reopened handling

Simulates real production incident workflow.

---

### Phase 4 — Intelligence Layer
- Rule-based severity scoring
- Severity mapping (LOW / MEDIUM / HIGH / CRITICAL)
- Confidence score
- Severity escalation based on recurrence

Introduces intelligence beyond simple storage.

---

### Phase 5 — Analytics Engine
- Pagination and filtering
- Sorting
- Search API
- Dashboard statistics
- Status distribution
- Severity distribution
- Environment distribution
- Time-series trend API (zero-filled days)

At this stage, the system behaves like a monitoring analytics backend.

---

## Current Capabilities

- Intelligent deduplication
- Lifecycle tracking with audit history
- Severity scoring engine
- Aggregated dashboard metrics
- Time-based trend analysis
- Dynamic filtering and pagination
- Transaction-safe updates

---

## Tech Stack

- Java 17  
- Spring Boot 3  
- Spring Data JPA  
- Hibernate  
- PostgreSQL  
- Lombok  

---

## Example API Endpoints

### Create Incident
### Change Status
### Search Incidents
### Dashboard Stats
### Trend Analytics

---

## Why This Project Matters

This is not a tutorial-based project.

It is a structured backend system built phase by phase to understand:

- How monitoring systems group failures
- How lifecycle transitions are enforced
- How analytics are generated from raw incident data
- How backend systems evolve from CRUD to intelligence

As someone transitioning from QA to backend development, this project reflects my focus on system design, data consistency, and production-oriented thinking.

---

## Planned Improvements

- MTTR (Mean Time To Resolve) metrics
- SLA breach detection
- Top failing services analytics
- Log-based classification
- Authentication and RBAC
- Webhook ingestion (Jenkins / GitHub)

---

## Author

Harshul Singhal  
Backend-focused developer transitioning from QA  
Java | Spring Boot | System Design
