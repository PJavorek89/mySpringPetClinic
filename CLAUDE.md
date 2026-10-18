# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Goal

Learning project: a veterinary clinic backend (owners, pets, visits, vets).
Goal: convert the original Spring MVC + Thymeleaf app into a pure REST backend.
The full specification is in `docs/SPEC.md`. Follow it; ask before deviating.

## Repository layout

Personal learning project (Udemy "Spring Framework 5: Beginner to Guru" Pet Clinic), used to practice Claude Code. The repo root is **not** a build root; the whole project is the multi-module Maven build in `petclinic/`. Older snapshots were removed in a cleanup and remain reachable via the git tag `pre-cleanup`.

## Commands

Run from `petclinic/` (Spring Boot 3.5, Java 21). Use the Maven wrapper (`mvnw`, no local Maven needed).

**On a clean clone run `./mvnw install` first.** `-pl pet-clinic-web` builds only that module, so `pet-clinic-data` must already be in the local Maven repository, otherwise `spring-boot:run` fails to resolve it.

```
./mvnw install                         # FIRST on a clean clone: build + install all modules
./mvnw clean install                   # rebuild all modules
./mvnw test                            # all tests
./mvnw test -pl pet-clinic-web -Dtest=PetclinicApplicationTests   # single test class
./mvnw spring-boot:run -pl pet-clinic-web # run the app (main class lives in pet-clinic-web)
```

Note: `pet-clinic-web` depends on `pet-clinic-data` at a hard-coded version `0.0.2` -> replaced with `${project.version}`, so it follows the parent version automatically. The `<parent>` blocks in both modules still need a literal version, so when bumping versions change it in `petclinic/pom.xml` and in the `<parent>` of `pet-clinic-data` and `pet-clinic-web`.

## Architecture

Multi-module Maven build (`groupId com.petClinic.javorek`, package root `com.petClinic.javorek.petclinic`):

- **`pet-clinic-data`** — domain and service layer, no web code.
  - `model/`: `BaseEntity` → `Person` → `Owner`/`Vet`, plus `Pet`, `PetType`, `Specialty`, `Visit` (JPA entities).
  - `repositories/`: Spring Data `JpaRepository` interfaces. Owner/Pet repositories use `@EntityGraph` on `findById`/`findAll` so lazy collections are loaded inside the transaction (open-in-view is off).
  - `services/` + `services/impl/`: `CrudService<T, ID>` (adds `getById`, which throws `NotFoundException` -> 404) and `AbstractJpaService` (transactional, delegates to the repository). `PetService.addToOwner` / `VisitService.addToPet` create children and set the owning side.
- **`pet-clinic-web`** — Spring Boot app (`PetclinicApplication`), no UI (Thymeleaf removed). `rest/` has `@RestController`s under `/api` and the single `GlobalExceptionHandler` (`ProblemDetail`); `dto/` has records (request records carry Bean Validation); `mapper/ClinicMapper` maps entities <-> DTOs by hand. `DataLoader` seeds sample data in the `dev` profile. Swagger UI via springdoc at `/swagger-ui.html`.

## Tech stack (target)

- Java 21, Spring Boot 3.x, Maven
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
