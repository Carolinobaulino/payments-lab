# ADR-0001: Modular monolith with Spring Modulith

- Status: Accepted
- Date: 2026-11-04

## Context

Payments Lab is the back end of a payments system: a double-entry ledger with
accounts in EUR and CHF, an instant payments gateway for SEPA Instant Credit
Transfer (SCT Inst) and Swiss instant payments on SIC, Swiss QR-bill support
and, later, an AI module.

The forces that shape its structure:

- A payment must update the gateway and the ledger consistently: reserving
  funds and recording the payment belong to the same business operation.
- A single developer builds and runs the system, a few hours a week.
- The domain is still being learned, so module boundaries will move.
- The ledger is the most critical part and must stay independent of the
  products that use it: payments today, possibly others tomorrow.

We need to decide how to structure the code and how to deploy it.

## Decision

We build a modular monolith: a single Spring Boot application whose modules are
the direct sub-packages of `io.github.carolinobaulino.paymentslab`:

- `ledger`: accounts, journal entries, balances. Depends only on `shared`.
- `payments`: instant payments gateway (SCT Inst, SIC). Depends on `ledger` and `shared`.
- `shared`: small, stable building blocks such as `Money`. Depends on nothing.

Each module exposes its API in its base package; sub-packages are internal.
Boundaries and allowed dependencies are declared with Spring Modulith
(`@ApplicationModule` in each `package-info.java`) and verified by a test
that runs in CI on every pull request.
Each module owns its database tables; other modules reach that data only
through the module's API.
Modules added in later phases, such as the AI module, follow the same rules.

## Alternatives considered

- **Microservices.** A payment would become a distributed transaction: a saga
  across services, with partial failures and idempotency to handle everywhere.
  Microservices let several teams deploy independently; with a single developer
  there is no such benefit, only the cost of more deployments, monitoring and
  versioned APIs.
- **Maven multi-module build.** It enforces the direction of dependencies at
  compile time, but public classes stay visible to every dependent module
  unless Java modules (JPMS) are added. Moving a class between modules means
  moving it between build projects and editing build files: too costly while
  the boundaries are still moving.
- **Monolith without enforced boundaries.** Nothing stops the boundaries from
  eroding: over time the code turns into a big ball of mud, and separating it
  later is expensive.

## Consequences

Positive:

- Each step of a payment can update the ledger and the gateway in a single
  ACID transaction.
- Moving a boundary is a package refactoring.
- Spring Modulith also provides module tests, application events and an event
  publication registry, a candidate for the outbox in phase 2.
- A module with clean boundaries and its own tables can later be extracted
  into a service.

Negative:

- Boundaries are enforced only at test time: the verification test must run in
  CI on every pull request and must never be disabled.
- Modules cannot be deployed or scaled independently, and a failure in one
  module can affect the whole application.
- The modules share one database: table ownership is a discipline that only
  reviews and tests protect.
- The project depends on Spring Modulith, although the dependency is small.