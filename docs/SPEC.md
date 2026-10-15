# Zadání: PetClinic REST API

Specifikace backendu pro veterinární kliniku. Vychází z oficiálního projektu
[spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest)
(jeho [README](https://github.com/spring-petclinic/spring-petclinic-rest#readme) a
[openapi.yml](https://github.com/spring-petclinic/spring-petclinic-rest/blob/master/src/main/resources/openapi.yml)),
zjednodušená pro tvůj projekt.

**Jak to použít s Claude Code:** ulož tento soubor do repa jako `docs/SPEC.md` a do
`CLAUDE.md` napiš „Zadání aplikace je v docs/SPEC.md, drž se ho.“ Pak můžeš zadávat
krátce, např. „Implementuj sekci 4.1 Majitelé podle SPEC.md“.

---

## 1. Cíl

Klinika potřebuje evidovat majitele, jejich zvířata, návštěvy u veterináře
a veterináře s jejich specializacemi. Aplikace je **čistý REST backend bez UI**
(frontend může být později samostatná aplikace).

## 2. Doménový model

| Entita | Atributy | Vztahy |
|---|---|---|
| **Owner** (majitel) | id, firstName, lastName, address, city, telephone | má 0..N Pet |
| **Pet** (zvíře) | id, name, birthDate | patří 1 Owner, má 1 PetType, má 0..N Visit |
| **PetType** (druh) | id, name (např. cat, dog, hamster) | číselník |
| **Visit** (návštěva) | id, date, description | patří 1 Pet |
| **Vet** (veterinář) | id, firstName, lastName | má 0..N Specialty |
| **Specialty** (specializace) | id, name (např. radiology, surgery, dentistry) | číselník, M:N s Vet |

Owner a Vet sdílejí společné atributy osoby (`Person`: firstName, lastName),
všechny entity dědí `id` z `BaseEntity`. Tohle už máš v kódu.

## 3. Uživatelské scénáře

1. Recepční **vyhledá majitele podle příjmení** (i části), nebo vypíše všechny.
2. Recepční **zaregistruje nového majitele** a **upraví jeho údaje**.
3. Recepční **přidá majiteli zvíře** (jméno, datum narození, druh) a upraví ho.
4. Recepční **zapíše návštěvu** ke zvířeti (datum, popis).
5. Kdokoli **zobrazí detail majitele** včetně jeho zvířat a jejich návštěv.
6. Kdokoli **vypíše veterináře** i s jejich specializacemi.
7. Správce **spravuje číselníky** druhů zvířat a specializací a veterináře.

## 4. REST API

Základní cesta `/api`. Formát JSON. ID jsou celá čísla.

### 4.1 Majitelé
| Metoda | Cesta | Popis |
|---|---|---|
| GET | `/api/owners?lastName=` | seznam, volitelný filtr podle začátku příjmení |
| GET | `/api/owners/{ownerId}` | detail včetně zvířat a návštěv |
| POST | `/api/owners` | vytvoří majitele, vrací **201** + `Location` |
| PUT | `/api/owners/{ownerId}` | upraví majitele |
| DELETE | `/api/owners/{ownerId}` | smaže majitele |

### 4.2 Zvířata
| Metoda | Cesta | Popis |
|---|---|---|
| POST | `/api/owners/{ownerId}/pets` | přidá zvíře majiteli (201) |
| GET | `/api/owners/{ownerId}/pets/{petId}` | detail zvířete daného majitele |
| PUT | `/api/owners/{ownerId}/pets/{petId}` | upraví zvíře |
| GET | `/api/pets` | všechna zvířata |
| GET / DELETE | `/api/pets/{petId}` | detail / smazání |

### 4.3 Návštěvy
| Metoda | Cesta | Popis |
|---|---|---|
| POST | `/api/owners/{ownerId}/pets/{petId}/visits` | zapíše návštěvu (201) |
| GET | `/api/visits`, `/api/visits/{visitId}` | seznam / detail |
| PUT / DELETE | `/api/visits/{visitId}` | úprava / smazání |

### 4.4 Veterináři, druhy, specializace
Plné CRUD (GET seznam, GET detail, POST, PUT, DELETE) na:
`/api/vets`, `/api/pettypes`, `/api/specialties`.
Veterinář se vrací i se seznamem specializací.

## 5. Validace (vrací 400 s popisem chyb)

- **Owner:** všechna pole povinná. firstName a lastName 1–30 znaků, jen písmena
  (povolena mezera, apostrof a pomlčka). address max 255, city max 80,
  telephone jen číslice, max 20.
- **Pet:** name povinné, max 30. birthDate povinné, nesmí být v budoucnosti.
  type povinný a musí existovat.
- **Visit:** description povinný, 1–255 znaků. date nepovinné, výchozí je dnešek.
- **PetType / Specialty:** name povinné, max 80, unikátní.

## 6. Chování a chyby

- Neexistující ID vrací **404**.
- Chybný vstup vrací **400** s jednotným tělem chyby, ideálně formát
  [RFC 7807 Problem Details](https://www.rfc-editor.org/rfc/rfc7807)
  (Spring 6 ho umí přes `ProblemDetail`).
- Úspěšné vytvoření vrací **201**, smazání **204**.
- API pracuje s **DTO**, entity se přímo neserializují (kvůli cyklům Owner ↔ Pet).

## 7. Technické požadavky

- **Monolitická architektura:** jedna spustitelná aplikace, uvnitř rozdělená do vrstev
  (controller → service → repository) a případně do Maven modulů (data, web).
- Java 21, Spring Boot 3.x, Maven.
- Spring Data JPA, H2 pro vývoj s ukázkovými daty (pár majitelů, zvířat, veterinářů).
  Volitelně profil pro PostgreSQL.
- Bean Validation (`jakarta.validation`).
- OpenAPI dokumentace přes springdoc, Swagger UI na `/swagger-ui.html`.
- Testy: unit testy služeb, `@WebMvcTest` testy controllerů, aspoň jeden
  integrační test `@SpringBootTest`.
- Bez Thymeleaf a bez UI.

## 8. Mimo rozsah (pro teď)

- Přihlašování a role (Spring Security). Hodí se jako pozdější rozšíření.
- Frontend.
- Stránkování a řazení. Volitelné rozšíření pro `/api/owners`.

## 9. Hotovo, když

- [ ] `./mvnw verify` projde a všechny testy jsou zelené.
- [ ] Všechny endpointy ze sekce 4 fungují a jsou vidět ve Swagger UI.
- [ ] Validace ze sekce 5 vrací 400 se srozumitelnou chybou.
- [ ] Po startu jsou v databázi ukázková data.
- [ ] README popisuje, jak aplikaci spustit a vyzkoušet.
