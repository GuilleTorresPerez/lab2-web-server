# Lab 2 Web Server — Project Report

## Description of Changes

- **Custom Error Page**
  
  - Created `src/main/resources/templates/error.html` to replace the Whitelabel page. Renders `status`, `error`, `path`, and a timestamp, includes a “Back to home” link, and adds basic no-cache meta tags. Added a small meta “signature” to assert the template in tests.
  - Wrote MVC tests to validate HTML rendering for **404** and JSON content negotiation.  
  - Context: this directly implements “Customize the Whitelabel Error Page.”

- **New `/time` Endpoint**
  
  - Added `TimeComponent.kt` with:
    - `data class TimeDTO(val time: LocalDateTime)`
    - `interface TimeProvider { fun now(): LocalDateTime }`
    - `@Service class TimeService : TimeProvider { override fun now() = LocalDateTime.now() }`
    - `fun LocalDateTime.toDTO(): TimeDTO = TimeDTO(time = this)`
  - Added `TimeController` with `@GetMapping("/time")` returning **200** + JSON via Jackson Kotlin module.
  - Added MVC test to assert **200**, JSON `{"time": "<ISO-8601>"}` and `Content-Type: application/json`.  
  - Context: this implements “Add a New Endpoint.”

- **HTTP/2 + SSL (HTTPS)**
  
  - Generated self-signed cert + key (RSA-2048, SHA-256), created a **PKCS#12** keystore, and enabled SSL + HTTP/2:
    - `openssl req -x509 ...` → `localhost.crt` + `localhost.key`
    - `openssl pkcs12 -export ...` → `localhost.p12` (placed under `src/main/resources/`)
    - `application.yml`:
      
      ```yaml
      server:
        port: 8443
        ssl:
          enabled: true
          key-store: classpath:localhost.p12
          key-store-password: "secret"
          key-store-type: PKCS12
        http2:
          enabled: true
      ```
  - Manual checks with `curl -k --http2 -i https://127.0.0.1:8443/` (404 + custom HTML) and `curl -k -i https://127.0.0.1:8443/time` (200 + JSON).  
  - Context: this implements “Enable HTTP/2 and SSL Support” and the “Manual Verification” steps.

- **Ktlint & Build**
  
  - Ensured code style with **ktlint** (formatting before build), as recommended in the brief.

---

## Technical Decisions

- **DTO + Mapper (extension function)**
  
  - We keep the internal time type (`LocalDateTime`) separate from the API model (`TimeDTO`). The `toDTO()` helper converts between them in one place, so the API stays easy to change later. **Status codes:** `/time` returns **200** on success.

- **Interface-driven design + DI**
  
  - Injecting the `TimeProvider` interface into `TimeController` makes testing easier (we can fake the clock) and keeps the code loosely coupled, as the lab suggests.

- **MVC Testing Strategy**
  
  - Used **MockMvc** with explicit `Accept` headers to assert HTML vs JSON content negotiation.
  - For error template validation, dispatched to `/error` with `RequestDispatcher.ERROR_*` attributes to simulate container error handling and ensure **Content-Type: text/html** is set by the error view.
  - Applied `@Timeout(5)` per test to avoid hangs; asserted **404** for the error view and **200** for `/time`.

- **TLS & HTTP/2 Choices**
  
  - We use a PKCS#12 keystore and run HTTPS on port **8443** with an RSA-2048/SHA-256 cert. HTTP/2 is enabled in Spring Boot. To verify, `curl --http2` confirms the protocol; with a self-signed cert we use `-k` for local dev only.

---

## Learning Outcomes

- **Clean Architecture essentials in Spring Boot**
  - Appreciated the value of interfaces and constructor injection to isolate the controller from time retrieval logic. Better grasp of how domain, service, and adapters interact.
- **Kotlin idioms**
  - Understood **extension functions** as a clean mapping layer (`LocalDateTime.toDTO()`), and when/why DTOs matter even if the initial shape seems identical.
- **Spring MVC serialization**
  - Saw how Jackson (with Kotlin module) serializes data classes to JSON automatically for `@RestController`.
- **TLS/HTTP-2 in practice**
  - Hands-on with **OpenSSL**, **PKCS#12**, Boot’s SSL config, and cURL’s HTTP/2 negotiation. Also learned to read the protocol in the response status line and why `-k` is necessary with self-signed certs.
- **Tooling discipline**
  - Formatted code with **ktlint** to pass the build consistently.

---

## AI Disclosure

### AI Tools Used

- **ChatGPT Plus (GPT-5 Thinking)**

### AI-Assisted Work

- **Conceptual explanations**: SSL/TLS vs HTTP/2, DI interfaces, DTO vs domain types, and Kotlin extension functions (used to clarify intent before coding).  
- **Testing guidance**: Proposed MockMvc tests for the custom error page (HTML + JSON negotiation), including the correct `/error` dispatch with `RequestDispatcher.ERROR_*`, and diagnosing the initial `Content type not set` and `NO-SOURCE` issues (test path).  
- **Command & config snippets**: OpenSSL commands, minimal `application.yml` for SSL + HTTP/2, and cURL verification flow from the handout (re-checked against the brief).
- **Help with making this report**

**Estimated split**: ~**30% AI-assisted** (guidance + initial test skeletons + config tips), **70% original** (final implementation, debugging, adjustments, and verification).

**Modifications to AI output**:

- Adapted test names, assertions, and meta “signature” checks to my actual HTML.
- Adjusted paths to `src/test/kotlin/...`, added `@Timeout(5)`, and aligned content-type/Accept usage.
- Integrated SSL settings and local keystore handling into my project structure.

### Original Work

- Implemented the `/time` endpoint (DTO, interface, service, controller) and verified JSON output.
- Built the custom error page and iterated until tests validated both HTML and JSON responses with correct status codes (**404** / **200**) and content types.
- Generated the cert + PKCS#12, configured SSL/HTTP-2, and performed all manual cURL verifications locally.
- Resolved build/test issues (pathing, request dispatch) and ensured style checks pass with ktlint.

---

**Appendix — Minimal Repro Snippets (for reviewers)**

- **Manual checks**
  
  ```bash
  ./gradlew bootRun
  curl -k --http2 -i https://127.0.0.1:8443/       # 404 + custom HTML (HTTP/2)
  curl -k -i https://127.0.0.1:8443/time           # 200 + JSON
  ```

- **Test idea (HTML error page)**
  
  ```kotlin
  // Uses RequestDispatcher.ERROR_* to trigger template rendering
  mvc.perform(
      get("/error")
        .accept(MediaType.TEXT_HTML)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/does-not-exist")
        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Not Found")
  )
  .andExpect(status().isNotFound)
  .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
  ```
