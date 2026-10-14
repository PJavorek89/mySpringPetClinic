# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Goal

Learning project: a veterinary clinic backend (owners, pets, visits, vets).
Goal: convert the original Spring MVC + Thymeleaf app into a pure REST backend.
The full specification is in `docs/SPEC.md`. Follow it; ask before deviating.

## Repository layout

Personal learning project (Udemy "Spring Framework 5: Beginner to Guru" Pet Clinic), used to practice Claude Code. The repo root is **not** a build root and contains several overlapping copies of the project:

- `petclinic/` — the active multi-module Maven project (work here).
- `petclinic/togithub/`, `togithub/`, `project/`, `refractoring/`, `petclinic/petclinic/`, `petclinic.zip` — older snapshots / earlier single-module or partial versions. Don't edit them unless asked.

## Commands

Run from `petclinic/` (uses the Maven wrapper; Spring Boot 2.5.6 parent):

```
./mvnw clean install                      # build all modules
./mvnw test                               # all tests
./mvnw test -pl pet-clinic-web -Dtest=PetclinicApplicationTests   # single test class
./mvnw spring-boot:run -pl pet-clinic-web # run the app (main class lives in pet-clinic-web)
```

Note: `pet-clinic-web` depends on `pet-clinic-data` at a hard-coded version `0.0.2`, matching the parent version, so keep them in sync when bumping versions (`release.properties` / `pom.xml.releaseBackup` come from maven-release-plugin).

## Architecture

Multi-module Maven build (`groupId com.petClinic.javorek`, package root `com.petClinic.javorek.petclinic`):

- **`pet-clinic-data`** — domain and service layer, no web code.
  - `model/`: `BaseEntity` → `Person` → `Owner`/`Vet`, plus `Pet`, `PetType`, `Speciality`, `Visit`.
  - `services/`: interfaces (`CrudService<T, ID>`, `OwnerService`, `PetService`, `PetTypeService`, `VetService`).
  - `services/map/`: in-memory `HashMap` implementations. `AbstractMapService<T, ID>` provides the generic storage (its methods are package-private); concrete `*ServiceMap` classes extend it and implement the service interface, exposing the methods publicly and deriving the key from `object.getId()` on save.
  - Dependencies include JPA, Lombok, and H2/MySQL/PostgreSQL drivers (runtime), but persistence is currently map-based.
- **`pet-clinic-web`** — Spring Boot app (`PetclinicApplication`), Thymeleaf/web/actuator/devtools, and the controllers (`indexController`, `OwnerController`, `VetController`). Depends on `pet-clinic-data`.

The parent `pom.xml` sets `java.version` 1.8, while the modules override the compiler source/target to 11 — Java 11 is what is actually needed.

## Tech stack (target)

- Java 21, Spring Boot 3.x, Maven (use the wrapper: `./mvnw`)
- Spring Web, Spring Data JPA, H2 (dev), Bean Validation (`jakarta.*`, never `javax.*`)
- springdoc-openapi for Swagger UI
- Lombok is allowed for entities and DTOs

## Conventions

- Layers: controller → service → repository. Controllers never use repositories directly.
- Controllers work with DTOs only; never return JPA entities from the API.
- REST paths start with `/api`. Return 201 + Location on create, 204 on delete, 404 when not found.
- Errors use Spring `ProblemDetail` via a single `@RestControllerAdvice`.
- Constructor injection only, no field `@Autowired`.
- Every new endpoint gets a `@WebMvcTest` test; every service method gets a unit test.

## Workflow rules

- Work in small steps. After each step run `./mvnw verify` and make sure it passes.
- Do not commit; I review the diff and commit myself.
- Explain non-obvious changes briefly – I am learning, so tell me *why*, not just *what*.
