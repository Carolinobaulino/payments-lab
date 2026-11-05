# ADR-0002: PostgreSQL with a Flyway-managed schema

- Status: Accepted
- Date: 2026-11-05

## Context

The ledger stores money. Its invariants must hold even when the application
code has a bug or two requests arrive at the same time: every journal entry
balances to zero, balances never drift, and the same request is never booked
twice.

The forces that shape the choice:

- A transfer writes several rows, the entry and its lines, that must be
  committed atomically.
- Integrity rules should be enforced by the database as well as by the code:
  foreign keys, unique idempotency keys, valid currencies and amounts.
- Concurrent transfers on the same account need reliable row locking and
  well-defined isolation levels.
- Balances, statements and reconciliations are aggregations over many rows.
- The schema will change in every phase, so its evolution must be versioned,
  reviewed and reproducible on every machine and in CI.
- Tests must behave like production.

We need to choose the database, how its schema evolves and what the tests run
against.

## Decision

- **Database:** PostgreSQL, major version 18, pinned as `postgres:18` in both
  `compose.yaml` and the Testcontainers configuration.
- **Schema:** owned by Flyway. Every change is a versioned SQL migration in
  `src/main/resources/db/migration` (`V1__create_accounts.sql`, and so on),
  reviewed in a pull request like any other code. Applied migrations are never
  edited: mistakes are fixed with a new migration.
- **Hibernate:** `spring.jpa.hibernate.ddl-auto: validate`. At startup Hibernate
  checks that the entities match the schema, and it never changes the schema.
  `spring.jpa.open-in-view: false`, so data is loaded inside transactions in the
  service layer, not while the HTTP response is being written.
- **Tests:** integration tests run against a real PostgreSQL in a container
  (Testcontainers), the same version used locally.

## Alternatives considered

- **MongoDB.** It supports multi-document ACID transactions, but the ledger is
  relational by nature (accounts, entries, lines), and declarative constraints,
  row-level locking and SQL aggregations are first-class features of a
  relational database.
- **Hibernate `ddl-auto: update`.** No history, no data migrations, unsafe
  renames and drops, and two owners of the schema. Acceptable for a prototype,
  not for a ledger.
- **Liquibase.** A valid alternative to Flyway. Its changelogs (XML, YAML or
  annotated SQL) add a layer of abstraction this project does not need; Flyway's
  plain SQL files are exactly what the database runs.
- **H2 in-memory database for tests.** It starts faster, but its SQL dialect,
  locking and constraint behaviour differ from PostgreSQL's: tests could pass
  where production fails, and concurrency tests would prove nothing.
- **Floating image tag (`postgres:latest`).** A new major version would arrive
  unannounced. PostgreSQL 18 itself changed where the official image stores its
  data, breaking containers that relied on the old path.

## Consequences

Positive:

- Invariants are protected twice: by the domain code and by database
  constraints.
- The migrations in the repository fully describe the schema of any
  environment.
- Startup fails fast if entities and schema diverge.
- Tests exercise the real database engine, including its locking.

Negative:

- Tests and local runs need Docker.
- Every schema change requires a migration, and applied migrations cannot be
  rewritten.
- Upgrading PostgreSQL to a new major version is a deliberate change, with its
  own pull request.
- PostgreSQL-specific SQL ties the project to PostgreSQL.