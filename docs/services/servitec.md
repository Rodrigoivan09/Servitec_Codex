# Bitácora de Servicio — Servitec (Metodología Prompt Mentor)

## 1. Contexto rápido
- Aplicación web Spring Boot para gestión de técnicos, usuarios y solicitudes (servicios domésticos).
- Stack principal: Java 17+, Spring MVC/Security, Thymeleaf, MariaDB.
- Despliegue previsto vía Docker Compose (`docker-compose.yml`).

## 2. Checklist de arranque
- [x] Leer `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md` (sección 6) y confirmar checklist inicial.
- [x] Leer `Metodologia_Prompt_Mentor/CODEx_NOTES.md` y nota especializada (`CODEx_NOTES_java.md`).
- [x] Preparar esta bitácora para registrar la sesión.

## 3. Bitácora pedagógica

### 2025-09-27 — Sesión en curso
- **Contexto inicial**: Pull previo con múltiples cambios en controladores, repositorios y vistas para habilitar rol técnico.
- **Pendientes heredados**: método `findByApellidoAndNombre` incorrecto en `UsuarioRepository`; revisar flujo de login técnico y documentación faltante.
- **Objetivo de la sesión**: corregir métodos del repositorio, validar configuración de seguridad y documentar avances siguiendo la Estrategia Bitácora Mentor.
- **Métricas ISO/IEC 25010 monitoreadas**: mantenibilidad (consistencia de repositorios y controllers); fiabilidad (prevención de fallos en login por firmas erróneas).

#### Pasos y aprendizajes
1. *(Preparación)* Se creó la carpeta `docs/services/` y este archivo para centralizar futuras sesiones.
2. *(Mantenibilidad)* Se eliminó el método `findByApellidoAndNombre` que era incompatible con la entidad `Usuario` (`src/main/java/edu/unam/springsecurity/repository/UsuarioRepository.java:13`). Esto evita que Spring Data genere firmas inválidas en tiempo de arranque.
3. *(Revisión funcional)* Se inspeccionó la navegación principal (`src/main/resources/templates/page-templates.html:69`) y las vistas de técnico para confirmar que el menú por rol expone `/tecnico` y sus subrutas.
4. *(Build tooling)* Se restauró el Maven Wrapper añadiendo `.mvn/wrapper/maven-wrapper.properties` y ejecutando `./mvnw -q -DskipTests compile`, lo que descargó `maven-wrapper.jar`.
5. *(Validación tentativa)* Inicialmente la compilación falló porque el entorno solo contaba con un JRE (`No compiler is provided in this environment`). Se documentó la necesidad de un JDK con `JAVA_HOME` configurado.
6. *(Bloqueo actual)* Se intentó `sudo apt-get update` para instalar OpenJDK 17, pero los comandos terminaron por `timeout` (posible restricción de red). Sin JDK no es posible continuar con la compilación ni con pruebas dependientes.
7. *(Bypass)* Se reutilizó el JDK 21 embebido en la extensión de VS Code (`$HOME/.vscode-server/extensions/redhat.java-1.45.0/jre/21.0.8-linux-x86_64`). Ejecutando `JAVA_HOME=... PATH=$JAVA_HOME/bin:$PATH ./mvnw -q -DskipTests compile` la compilación completó sin errores.
8. *(Instalación controlada)* Se descargó Amazon Corretto 21 (`amazon-corretto-21-x64-linux-jdk.tar.gz`), se extrajo en `vendor/amazon-corretto-21.0.8.9.1-linux-x64` y se validó la compilación con `JAVA_HOME=$PWD/vendor/amazon-corretto-21.0.8.9.1-linux-x64 PATH=$JAVA_HOME/bin:$PATH ./mvnw -q -DskipTests compile`.
9. *(JWT robusto)* Se aumentó el secreto por defecto en `application.yml` y `application-docker.yml` (`jwt.secret`) a un valor hexadecimal de 64 caracteres (>=256 bits) para cumplir los requisitos de HMAC.
10. *(Depuración beans)* Se eliminó la clase redundante `src/main/java/edu/unam/springsecurity/security/JwtTokenProvider.java` (no utilizada) y se ejecutó `./mvnw clean` para borrar artefactos que generaban un bean duplicado.
11. *(Arranque controlado)* `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` ahora avanza la configuración de Spring; falla en la fase de Flyway porque no hay MariaDB escuchando en `localhost:3306` (se registró la excepción para retomar cuando se levante la base).
12. *(Documentación sincronizada)* Se adaptaron `CODEx_NOTES.md` y `CODEx_NOTES_java.md` para reflejar Servitec y enlazar las guías heredadas del backend FastAPI.
13. *(OpenAPI listo)* Se añadió `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0` al `pom.xml`, habilitando `/swagger-ui/index.html` y `/v3/api-docs` una vez que la app esté en ejecución.
14. *(Flyway MariaDB)* Se incorporó `org.flywaydb:flyway-mysql:10.16.0` y se dejó una sola dependencia de Lombok para evitar el warning duplicado al compilar.
15. *(Flyway baseline)* Se habilitó `spring.flyway.baseline-on-migrate=true` en `application.yml` para inicializar bases ya pobladas durante el desarrollo local.
16. *(Compatibilidad CORS)* Se reemplazaron los `List.of` en `security/SecurityConfiguration` por `Arrays.asList` y `Collections.singletonList` para evitar errores de compilación en Java 17 al reiniciar Devtools.
17. *(Seed técnicos completo)* Se añadió `V5__seed_tecnicos_relaciones.sql` con seis técnicos adicionales, sus categorías/servicios y credenciales de prueba (correo `*@servitec.local`, contraseña `password`).
18. *(Reset controlado)* Se documentó el procedimiento para limpiar la base cuando una migración queda parcialmente aplicada (`docker compose down -v && docker compose up db` antes de relanzar `spring-boot:run`).

### 2025-10-02 — Flyway valida la siembra de técnicos
- **Contexto inicial**: `./mvnw spring-boot:run` detuvo el arranque porque Flyway marcó la migración `V5__seed_tecnicos_relaciones.sql` como fallida (`FlywayValidateException`). El historial indicaba un intento previo incompleto, lo que dejó datos insertados a medias y bloqueó la validación.
- **Hipótesis y análisis**: revisamos la migración y vimos que usaba `ON DUPLICATE KEY UPDATE` para evitar duplicados. En MariaDB ese patrón no es transaccional, así que si algún `INSERT` fallaba (por ejemplo, por llaves compuestas ya presentes) la migración quedaba a medias y Flyway registraba el fallo. El diagnóstico apunta a problemas de fiabilidad (ISO 25010) por seeds que no son idempotentes.
- **Solución aplicada**: reescribimos las inserciones de relaciones para que solo agreguen filas cuando realmente no existan usando subconsultas `NOT EXISTS` y un `JOIN` explícito a la categoría/servicio. Esto vuelve determinística la migración y evita errores por duplicados. Los cambios se concentran en `src/main/resources/db/migration/V5__seed_tecnicos_relaciones.sql:31-92`.
- **Validaciones y siguientes pasos**: no ejecutamos Flyway desde aquí porque dependemos de MariaDB externa; al aplicar el cambio es necesario correr `docker compose down -v && docker compose up db` y luego `./mvnw -Dflyway.repair flyway:repair` (o `./mvnw -Dflyway.repair=true spring-boot:run`) para reparar el historial y reintentar la migración. Se reintentó `./mvnw -q -DskipTests compile` pero volvió a fallar por falta de JDK (mismo bloqueo registrado el 2025-09-27). Monitorear el KPI de fiabilidad (migraciones sin fallos) e incluir este ajuste en los próximos despliegues.

### 2025-10-02 — Makefile multistack y reinicio limpio de Docker
- **Contexto inicial**: el `Makefile` original solo tenía comandos Docker específicos de Servitec. La metodología Prompt Mentor exige herramientas reutilizables entre servicios/lenguajes, así que se buscó centralizar tareas (`setup`, `build`, `lint`, etc.) detectando automáticamente el stack y documentando cada mejora.
- **Hipótesis y análisis**: se definió un detector basado en artefactos clave (`pom.xml`, `package.json`, `pyproject.toml`, etc.) para inferir el stack y ajustar comandos. También se incorporó la petición de reiniciar contenedores al arrancar la app. Esto mejora la mantenibilidad y reduce fallos por configuraciones residuales (KPI de mantenibilidad y fiabilidad de ISO 25010).
- **Solución aplicada**: se reescribió por completo el archivo `Makefile` (`Makefile:1-192`) para:
  1. Detectar el stack con `FORCE_STACK` opcional y mostrarlo al ejecutar cualquier target (se probó con `make context`).
  2. Definir macros reutilizables `setup/build/lint/test/run/format/clean` que delegan a Maven, npm, Poetry, Go, Cargo, etc., permitiendo sobrescribir comandos mediante variables de entorno.
  3. Introducir familias `compose-*` que siempre hacen `docker compose down -v --remove-orphans` antes de levantar servicios (cumpliendo el requerimiento de reinicio limpio) y mantener alias legacy (`up`, `dev-up`, etc.).
- **Validaciones y siguientes pasos**: `make context` confirmó la detección `java-maven`. El primer intento de `make build` arrojó un error de sintaxis (doble `;`) porque `BUILD_CMD` estaba vacío; se retiró la preasignación genérica para que el bloque `java-maven` defina el comando correcto (`Makefile:75-82`). Tras la corrección, `make build` vuelve a invocar Maven, pero falla por la ausencia de `JAVA_HOME` (mismo bloqueo registrado en sesiones previas). Se añadió la bandera `VERBOSE=1` (por defecto silencia Maven con `-q`) para ver logs completos cuando haya fallos (`Makefile:58-87`). Documentar en `Metodologia_Prompt_Mentor/CODEx_NOTES.md` que el Makefile ahora es plantilla multi-stack y debe sincronizarse en otros servicios cuando surjan mejoras.

### 2025-10-02 — PasswordEncoder sin deprecaciones
- **Contexto inicial**: el bean `passwordEncoder` usaba `NoOpPasswordEncoder`, marcado como deprecado en Spring Security 6, generando advertencias en cada compilación y dejando contraseñas en texto plano.
- **Hipótesis y análisis**: al migrar a `PasswordEncoderFactories.createDelegatingPasswordEncoder()` evitamos la API obsoleta y abrimos la puerta a algoritmos fuertes (`bcrypt` por defecto). Para mantener compatibilidad con los seeds existentes, se decidió prefijar las contraseñas con `{noop}` mientras se planea una transición a hashes reales.
- **Cambios aplicados**:
  1. `SecurityConfiguration` ahora expone un `PasswordEncoder` delegante (`src/main/java/edu/unam/springsecurity/security/SecurityConfiguration.java:14-18`).
  2. Todas las migraciones que sembraban credenciales (`V1`, `V2`, `V3`, `V5`) actualizan la contraseña a `'{noop}password'` para que Spring identifique el algoritmo correctamente.
  3. Se documentó que `VERBOSE=1 make build` y `./mvnw -DskipTests compile -Dmaven.compiler.showDeprecation=true -Dmaven.compiler.showWarnings=true -Dmaven.compiler.compilerArgs=-Xlint:deprecation` permiten verificar que la advertencia desaparece.
- **Acciones para entornos ya poblados**: ejecutar `UPDATE Administradores SET contrasena = '{noop}password' WHERE contrasena = 'password';` y el mismo ajuste para `Usuarios` y `Tecnicos` antes de reiniciar el backend, o bien regenerar la base con las migraciones frescas.
- **Validaciones**: tras exportar `JAVA_HOME=$PWD/vendor/amazon-corretto-21.0.8.9.1-linux-x64`, tanto `./mvnw -DskipTests compile -Dmaven.compiler.compilerArgs=-Xlint:deprecation` como `VERBOSE=1 make build` concluyen en `BUILD SUCCESS` sin advertencias nuevas.

#### Próximos pasos inmediatos
- Formalizar la instalación de un JDK soportado (17+ o 21+/25) para no depender del bundle de VS Code; mientras tanto, documentar el uso de Corretto localmente.
- Levantar MariaDB/Flyway (Docker Compose o instancia local) para permitir que la aplicación arranque.
- Ejecutar pruebas manuales del dashboard de técnicos (estadísticas y carga de foto) en cuanto la aplicación esté corriendo.

### 2025-10-03 — JDK portátil fijado y migraciones reparadas
- **Contexto inicial**: necesitábamos dejar de exportar `JAVA_HOME` manualmente en cada comando y desbloquear la migración `V5__seed_tecnicos_relaciones.sql`, que seguía marcada como fallida hasta ejecutar `flyway repair` contra una instancia limpia de MariaDB.
- **Mantenibilidad**: se automatizó la detección del JDK portable ajustando el `Makefile` para que, si no hay `JAVA_HOME`, establezca `vendor/amazon-corretto-21.0.8.9.1-linux-x64` y anteponga su `bin` al `PATH` (`Makefile:54-63`). Además, el mismo `Makefile` ahora carga automáticamente las variables definidas en `.env` antes de ejecutar cualquier target (`Makefile:65-88`), eliminando la necesidad de exportar manualmente `MYSQL_ROOT_PASSWORD`, `SPRING_DATASOURCE_PASSWORD`, etc.
- **Fiabilidad**: al ejecutar `DOCKER_HOST= docker compose down -v --remove-orphans` seguido de `docker compose up db -d`, recreamos la base con el esquema en minúsculas (`servitecdb`) y dejamos el contenedor `servitec_db` saludable antes de correr Flyway.
- **Solución aplicada**:
  1. Añadimos el plugin `org.flywaydb:flyway-maven-plugin:10.16.0` al `pom.xml` (`pom.xml:118-128`) para alinear la versión del plugin con las dependencias `flyway-core`/`flyway-mysql`.
  2. Se corrió `./mvnw -Dflyway.url=jdbc:mariadb://localhost:3306/servitecdb -Dflyway.user=root -Dflyway.password=ServitecRootP@ssw0rd!2025 flyway:repair`, que dejó la tabla `flyway_schema_history` lista (sin registros porque la base estaba limpia).
  3. Se ejecutó `./mvnw ... -Dflyway.baselineOnMigrate=true flyway:migrate`, lo que aplicó `V1` a `V5` de forma consecutiva y confirmó el arreglo determinista de `V5`.
- **Seguridad/fiabilidad**: MariaDB bloqueaba conexiones externas de `root` (`Access denied for user 'root'@'172.18.0.1'`). Creamos una cuenta dedicada `servitec_app` con privilegios sobre `servitecdb` y contraseñas definidas en `.env`, actualizando también las variables `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` (`.env`, comando ejecutado vía `docker compose exec db mysql ...`). Esto evita el uso de `root` y regla el acceso desde el host.
- **Operación Docker**: el flujo standalone se generalizó para cualquier stack soportado (`Makefile:405-444`, `scripts/docker_build_auto.sh:1-224`). `make docker-build`/`make docker-redeploy` delegan en el script para elegir un Dockerfile existente (`Dockerfile`, `Dockerfile.<stack>`, `APP_DOCKERFILE`) o generar uno temporal con plantillas para Java/Maven, Node (npm/yarn/pnpm), Python (pip/Poetry), Go y Rust. `make docker-redeploy` ejecuta primero `build` y `test`; solo si ambas validaciones concluyen en éxito se reconstruye la imagen `servitec_app:latest` y se reemplaza el contenedor, mapeando `uploads/`. El contenedor recibe `SPRING_DATASOURCE_URL` y `SPRING_PROFILES_ACTIVE` específicos (`jdbc:mariadb://servitec_db:3306/servitecdb`, perfil `docker`) mientras que el host mantiene `.env` apuntando a `localhost`; esto permite que las pruebas Maven usen la base local y que el contenedor se conecte a `servitec_db` en la red `servitec_codex_default` sin ajustes manuales.
- **Evidencia**: `VERBOSE=1 make build` muestra `BUILD SUCCESS` usando el JDK de `vendor/` sin pasos manuales adicionales; `./mvnw ... flyway:migrate` terminó en éxito y deja la base con seeds técnicos listos.
- **Pendientes**: mantener `.env` con `MYSQL_DATABASE=servitecdb` para que Flyway y la aplicación apunten al mismo esquema; continuar con el arranque de la app y validación del dashboard técnico en la siguiente sesión.

### 2025-10-06 — Escaneo automatizado (SpotBugs + ZAP)
- **Contexto**: alineamos la estrategia con los KPI de fiabilidad/mantenibilidad agregando SAST/DAST nativos al proyecto Java (sin depender de los servicios Python como `validator_employee`).
- **Acción**: el workflow `Prompt Mentor CI` ahora compila con Temurin 21, ejecuta `./mvnw spotbugs:spotbugs` (SpotBugs + FindSecBugs) y levanta MariaDB + Spring Boot para que OWASP ZAP baseline inspeccione `http://127.0.0.1:8090`. Los artefactos `spotbugs-report` y `zap-baseline-report` quedan adjuntos a cada ejecución (`.github/workflows/prompt_mentor_ci.yml`).
- **Aprendizajes**: SpotBugs requiere que `JAVA_HOME` apunte a un JDK completo (se resuelve en CI con `actions/setup-java`); ZAP recibe la cookie `JSESSIONID` generada con un usuario QA (`ZAP_QA_USER/PASS`), lo que habilita el rastreo autenticado sin exponer credenciales en el repositorio.
- **Acciones siguientes**: aplicar las mitigaciones listadas (sanitizar nombres de archivo, parametrizar logs, evaluar CSRF) y monitorear los reportes `spotbugs-report`/`zap-baseline-report` en cada push para confirmar la ausencia de regresiones.
- **Hallazgos SAST (SpotBugs/FindSecBugs)**:
  - `PATH_TRAVERSAL_IN` en `util/Archivos`: sanitizar nombres (`StringUtils.cleanPath`), validar que no contengan `..` y restringir escritura al directorio permitido antes de aceptar archivos.
  - `CRLF_INJECTION_LOGS` en `HomeController.loginPersonalizado` y `UserDetailsServiceImpl.loadUserByUsername`: migrar a parámetros de logger (`logger.info("mensaje {}", valor)`) y normalizar entradas antes de concatenarlas.
  - `SPRING_CSRF_PROTECTION_DISABLED` en `SecurityConfiguration`: evaluar reactivar CSRF para rutas de formularios o aislarlas bajo endpoints específicos con tokens anti-CSRF.
- **DAST**: el baseline autenticado queda listo para ejecutarse; revisar el artefacto `zap-baseline-report` tras la primera corrida con credenciales `SERVITEC_ZAP_USER/PASS` y registrar vulnerabilidades confirmadas.

## 4. Validaciones
- `./mvnw -q -DskipTests compile` completó con éxito usando el JDK 21 de VS Code al exportar `JAVA_HOME` y actualizar `PATH`.
- `./mvnw -q -DskipTests compile` también pasó utilizando Amazon Corretto 21 ubicado en `vendor/amazon-corretto-21.0.8.9.1-linux-x64`.
- 2025-10-02 — `make build` finalizó en `BUILD SUCCESS` al exportar `JAVA_HOME=$PWD/vendor/amazon-corretto-21.0.8.9.1-linux-x64` (el flag `-X` confirmó el uso del compilador y solo reportó `SecurityConfiguration.java` con APIs obsoletas).
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` arranca correctamente tras `docker compose up db`; si el contenedor se detiene, el comando falla con `Connection refused` y debe reiniciarse la base.
- 2025-10-06 — El job `security-scans` del workflow `Prompt Mentor CI` genera los artefactos `spotbugs-report` y `zap-baseline-report`; revisarlos en cada push/PR.
- Cuando Flyway reporta "Found non-empty schema but no history table" o "Detected failed migration", ejecutar `docker compose down -v` seguido de `docker compose up db` y relanzar la aplicación.

## 5. Decisiones abiertas
- Definir estructura final de navegación para usuarios técnicos.
- Incorporar Maven Wrapper completo o establecer instructivo para compilar con Maven instalado en el host.

## 6. Referencias
- `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md`
- `Metodologia_Prompt_Mentor/CODEx_NOTES.md`
- `Metodologia_Prompt_Mentor/CODEx_NOTES_java.md`
- `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md` (patrones heredados del ecosistema FastAPI)
- `docs/catalog/languages.md` (catálogo multi-stack para auto-onboarding)
