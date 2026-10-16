# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Goal

Learning project: a veterinary clinic backend (owners, pets, visits, vets).
Goal: convert the original Spring MVC + Thymeleaf app into a pure REST backend.
The full specification is in `docs/SPEC.md`. Follow it; ask before deviating.

## Repository layout

Personal learning project (Udemy "Spring Framework 5: Beginner to Guru" Pet Clinic), used to practice Claude Code. The repo root is **not** a build root; the whole project is the multi-module Maven build in `petclinic/`. Older snapshots were removed in a cleanup and remain reachable via the git tag `pre-cleanup`.

## Commands

Run from `petclinic/` (Spring Boot 2.5.6 parent). The Maven wrapper is not set up (`mvnw` exists but `.mvn/` is missing); use the locally installed `mvn`. To add the wrapper, run `mvn wrapper:wrapper` in `petclinic/`.

```
mvn clean install                      # build all modules
mvn test                               # all tests
mvn test -pl pet-clinic-web -Dtest=PetclinicApplicationTests   # single test class
mvn spring-boot:run -pl pet-clinic-web # run the app (main class lives in pet-clinic-web)
```

Note: `pet-clinic-web` depends on `pet-clinic-data` at a hard-coded version `0.0.2` -> replaced with `${project.version}`, so it follows the parent version automatically. The `<parent>` blocks in both modules still need a literal version, so when bumping versions change it in `petclinic/pom.xml` and in the `<parent>` of `pet-clinic-data` and `pet-clinic-web`.

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

- Java 21, Spring Boot 3.x, Maven (local `mvn`; wrapper not set up)
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

- Work in small steps. After each step run `mvn verify` and make sure it passes.
- Do not commit; I review the diff and commit myself.
- Explain non-obvious changes briefly – I am learning, so tell me *why*, not just *what*.
