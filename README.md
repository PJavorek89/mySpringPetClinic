# mySpringPetClinic

An attempt at the Pet Clinic from the Udemy course "Spring Framework 5: Beginner to Guru".

Used as a project for learning Claude Code.

## Running

The project lives in the `petclinic/` directory (Java 21, Spring Boot 3.5, Maven).

```
cd petclinic
./mvnw install                          # first (clean clone): build + tests + install modules
./mvnw spring-boot:run -pl pet-clinic-web # starts on http://localhost:8080
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

## MCP server (AI agents)

The same application also runs an MCP server (Spring AI, Streamable HTTP) at `http://localhost:8080/mcp`. It exposes the clinic services as tools for AI agents. Currently available: `find_owners` (search owners by the beginning of their last name; returns owner ids and pet ids). There is deliberately no delete tool.

### Connecting from Claude Code

Start the app first, then register the server:

```
claude mcp add --transport http pet-clinic http://localhost:8080/mcp
```

Inside Claude Code check it with `/mcp`, then ask e.g. "Find the owner Novák and list his pets".

### Trying it with curl

```
# 1) initialize a session and note the Mcp-Session-Id response header
curl -i -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"curl","version":"1.0"}}}'

# 2) confirm initialization, then list and call tools (use your session id)
curl -X POST http://localhost:8080/mcp -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" -H "Mcp-Session-Id: <session-id>" \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'
curl -X POST http://localhost:8080/mcp -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" -H "Mcp-Session-Id: <session-id>" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"find_owners","arguments":{"lastName":"nov"}}}'
```

You can also use the MCP Inspector: `npx @modelcontextprotocol/inspector` (transport: Streamable HTTP, URL `http://localhost:8080/mcp`).
