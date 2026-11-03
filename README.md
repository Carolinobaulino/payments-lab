# Payments Lab

![CI](https://github.com/Carolinobaulino/payments-lab/actions/workflows/ci.yml/badge.svg)

Phase 1 - The ledger

Phase 2 — Multi-scheme instant payment gateway

Phase 3 — Swiss QR-invoice

Phase 4 — deterministic AI Module with human in the loop

## Status

hase 0: foundations"

## Roadmap

1. Double-entry ledger (EUR and CHF accounts)
2. Instant payments gateway: SEPA SCT Inst and Swiss SIC
3. Swiss QR-bill validation
4. AI module
5. Optional extensions

## Tech stack

Java 25, Spring Boot 4.1, Spring MVC, Spring Modulith, PostgreSQL, Flyway, Testcontainers, GitHub Actions.

## Getting started

Requirements: JDK 25 and Docker.

- Run the tests: `./mvnw verify`
- Run the application: `./mvnw spring-boot:run` (starts PostgreSQL with Docker Compose)

## Disclaimer

Personal learning project. Synthetic data only. Not affiliated with any employer.
