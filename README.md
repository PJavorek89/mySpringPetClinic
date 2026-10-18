# mySpringPetClinic

An attempt at the Pet Clinic from the Udemy course "Spring Framework 5: Beginner to Guru".

Used as a project for learning Claude Code.

## Running

The project lives in the `petclinic/` directory (Java 21, Spring Boot 3.5, Maven).

```
cd petclinic
./mvnw install                          # first (clean clone): build + tests + install modules
mvnw spring-boot:run -pl pet-clinic-web # starts on http://localhost:8080
```

`install` must come first: `-pl pet-clinic-web` builds only one module, and that module needs `pet-clinic-data` in the local Maven repository. In PowerShell use `.\mvnw`.

After startup (profile `dev`) the H2 database contains sample data (`DataLoader`).

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:petclinic`)

## Trying the API

```
curl http://localhost:8080/api/owners
curl "http://localhost:8080/api/owners?lastName=nov"
curl -i -X POST http://localhost:8080/api/owners -H "Content-Type: application/json" \
  -d '{"firstName":"Karel","lastName":"Test","address":"Ulice 1","city":"Praha","telephone":"123456"}'
curl http://localhost:8080/api/vets
curl -i http://localhost:8080/api/owners/999   # 404 as a ProblemDetail
```

Errors use the RFC 7807 `ProblemDetail` shape; validation errors (400) contain an `errors` field.
