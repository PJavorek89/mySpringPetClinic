# mySpringPetClinic

pokus o mySpringPet clinic z udemy curzu sf5: beginner to guru

Pouzito pro projekt naucit se s claude code

## Spusteni

Projekt je v adresari `petclinic/` (Java 21, Spring Boot 3.5, Maven).

```
cd petclinic
mvn verify                              # build + testy
mvn spring-boot:run -pl pet-clinic-web  # start na http://localhost:8080
```

Po startu (profil `dev`) jsou v H2 databazi ukazkova data (`DataLoader`).

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 konzole: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:petclinic`)

## Vyzkouseni API

```
curl http://localhost:8080/api/owners
curl "http://localhost:8080/api/owners?lastName=nov"
curl -i -X POST http://localhost:8080/api/owners -H "Content-Type: application/json" \
  -d '{"firstName":"Karel","lastName":"Test","address":"Ulice 1","city":"Praha","telephone":"123456"}'
curl http://localhost:8080/api/vets
curl -i http://localhost:8080/api/owners/999   # 404 jako ProblemDetail
```

Chyby maji tvar RFC 7807 `ProblemDetail`; validacni chyby (400) obsahuji pole `errors`.
