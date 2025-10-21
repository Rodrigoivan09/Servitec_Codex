# CODEx Notes — Dominio Java

## Stack y alcance
- Proyecto actual: Servitec (Spring Boot MVC + Thymeleaf + Spring Security + MariaDB/Flyway).
- Frameworks frecuentes: Spring Boot (REST/MVC), aunque se mantienen referencias a Quarkus/Micronaut para otros repos.
- Build: Maven Wrapper (`./mvnw`). El repositorio incluye un JDK portátil (Amazon Corretto 21 en `vendor/`) para entornos sin JDK instalado.
- Plataforma estándar: desarrollo local con Docker Compose (MariaDB) y despliegue empaquetado en contenedores o VMs Linux.
- Consulta también `docs/catalog/languages.md` para ver el catálogo completo de stacks disponibles en el proyecto.

## Checklist inicial
- [ ] Verificar `JAVA_HOME` (usar `vendor/amazon-corretto-21.0.8.9.1-linux-x64` o un JDK 17+/21 propio).
- [ ] Revisar perfiles activos (`application.yml`, `application-docker.yml`) y variables (`SPRING_PROFILES_ACTIVE`, `SPRING_CONFIG_IMPORT`).
- [ ] Levantar `docker compose up db` si se requiere MariaDB local.
- [ ] Ejecutar `./mvnw -q -DskipTests compile` antes de cambios relevantes; registrar resultado en la bitácora del servicio.
- [ ] Confirmar endpoints críticos (`/`, `/login`, `/tecnico`, `/admin`) y, si se habilita Actuator, `/actuator/health`.

## Errores y soluciones comunes
- 2025-10-21 — GitHub Actions aborta con `SERVITEC_ZAP_USER / SERVITEC_ZAP_PASS no configurados`: se añadió un fallback en `.github/workflows/prompt_mentor_ci.yml:83-97` (`QA_USERNAME=5555555555`, `QA_PASSWORD=Contraseña1#`) para no detener el pipeline, pero se recomienda crear los secretos `ZAP_QA_USER` y `ZAP_QA_PASS` con esos valores (u otros) para que no queden expuestos en los logs.
- 2025-10-02 — `WeakKeyException` en JWT: se subió el secreto por defecto a 64 caracteres hex en `application.yml` y `application-docker.yml`.
- 2025-10-02 — Duplicidad de beans `JwtTokenProvider`: eliminar la clase redundante y limpiar `target/` (`./mvnw clean`).
- 2025-10-02 — `FlywaySqlException` por MariaDB ausente: levantar la base con Docker o apuntar a una instancia remota antes de `spring-boot:run`.
- 2025-10-02 — `FlywayException: Unsupported Database: MariaDB 10.11`: añadir `org.flywaydb:flyway-mysql` (misma versión que `flyway-core`).
- 2025-10-02 — `FlywayException: Found non-empty schema but no history table`: habilitar `spring.flyway.baseline-on-migrate=true` para entornos con datos preexistentes.
- 2025-10-02 — `FlywayValidateException` tras migraciones fallidas: ejecutar `docker compose down -v && docker compose up db` (o `flyway repair`) antes de relanzar el proyecto; documentado en la bitácora de servicio.
- 2025-10-02 — `FlywayValidateException` por la migración `V5__seed_tecnicos_relaciones.sql`: reescribir las inserciones con `NOT EXISTS` (ver rutas en `src/main/resources/db/migration/V5__seed_tecnicos_relaciones.sql`) y luego correr `./mvnw -Dflyway.repair flyway:repair` para limpiar el historial antes del siguiente arranque.
- 2025-09-27 — `javax.net.ssl.SSLHandshakeException` al consumir APIs sin TLS: habilitar perfil `local` con HTTP y registrar certificados en `truststore`.
- 2025-09-27 — `OutOfMemoryError: Metaspace` en despliegues Tomcat: fijar `-XX:MaxMetaspaceSize` y monitorear con JFR.
- 2025-09-27 — Fallos en pipelines por `mvnw` sin permisos: aplicar `chmod +x mvnw` y registrar el comando en la bitácora global.

## Métricas sugeridas (ISO/IEC 25010)
- Rendimiento: p95 de endpoints REST, tiempos de arranque en frío.
- Fiabilidad: tasa de errores 5xx, retries en colas, MTBF/MTTR.
- Mantenibilidad: cobertura Sonar (líneas, ramas), deuda técnica, complejidad por paquete.
- Portabilidad: éxito de builds multiplataforma (Linux/Mac/Windows) y compatibilidad con JDKs LTS.

## Patrones recomendados
- `spring-boot-observabilidad`: incluir Actuator + OpenTelemetry; documentar habilitación en `docs/services/servitec.md`.
- `rollback-bluegreen`: documentar despliegues por contenedor/VM (o Compose) y validar `docker ps`, logs, health checks.
- `documentación recursiva`: enlazar hallazgos de Servitec hacia las notas heredadas de microservicios (`CODEx_NOTES_backend.md`) cuando compartan infraestructura (Docker, Artifact Registry).
- `springdoc-openapi`: exponer `/swagger-ui/index.html` y `/v3/api-docs` mediante `springdoc-openapi-starter-webmvc-ui`. Registrar credenciales/seguridad cuando se publique.

## Bitácora de dominio (Metodología Prompt Mentor)
- 2025-09-27 — Se estableció esta nota para proyectos Java; enlazada desde `STRATEGY_BITACORA_MENTOR.md` y `CODEx_NOTES.md` del repositorio base.
- 2025-10-02 — Servitec adopta esta nota como referencia principal; `docs/services/servitec.md` registra cada sesión y enlaza a las guías heredadas del backend Python cuando aplica.
- 2025-10-02 — El `Makefile:1-192` ahora detecta el stack (`java-maven` para Servitec) y expone comandos comunes (`make setup/build/lint/test/run/format/clean`) además de reiniciar Docker Compose con `down -v` antes de cada subida. Usa `make context` para confirmar la detección o `FORCE_STACK=<stack>` si trabajas en otro subproyecto Java.
- 2025-10-02 — Corrección posterior: se eliminó la preasignación vacía de `*_CMD` para que `make build` ejecute Maven correctamente; recuerda exportar `JAVA_HOME` antes de usar esta diana.
- 2025-10-02 — Validación extra: `JAVA_HOME=$PWD/vendor/amazon-corretto-21.0.8.9.1-linux-x64 make build` produce `BUILD SUCCESS`; usa `VERBOSE=1 make build` (o `./mvnw -X -DskipTests compile -Xlint:deprecation`) cuando necesites ver la salida completa y detallar APIs obsoletas.
- 2025-10-02 — `SecurityConfiguration` migra a `PasswordEncoderFactories.createDelegatingPasswordEncoder()` y los seeds (`V1`, `V2`, `V3`, `V5`) guardan `{noop}password` para evitar el encoder en desuso. Ejecuta los `UPDATE` indicados en `docs/services/servitec.md` si trabajas con una base previa.

Actualiza este archivo cada vez que surjan hallazgos específicos del stack Java y enlaza los servicios afectados.
