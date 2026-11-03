# Payments Lab

![CI](https://github.com/Carolinobaulino/payments-lab/actions/workflows/ci.yml/badge.svg)

A personal portfolio project that models the core of a payments back end: a double-entry ledger and an instant payments gateway for SEPA SCT Inst and the Swiss SIC system. Built to practise production-grade Java and Spring on a realistic banking domain.

## Status

Phase 0: foundations"

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
