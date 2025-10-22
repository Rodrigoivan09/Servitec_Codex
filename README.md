🛠️ Proyecto Servitec — Plataforma Integral de Gestión de Servicios Técnicos
================================================================================

Bienvenido a **Servitec**, una aplicación web que gestiona técnicos especializados, solicitudes de servicio y flujos administrativos para hogares y pequeños negocios. El proyecto se construye sobre **Spring Boot 3**, **Spring Security** (form login + JWT), **Thymeleaf** para las vistas, **MariaDB** como motor relacional y migraciones **Flyway**. Este README sigue la narrativa pedagógica de la **Metodología Prompt Mentor**, de modo que encontrarás explicaciones orientadas a enseñar el porqué de cada paso y las rutas exactas para profundizar.

---

📚 Índice rápido
----------------
- [Arquitectura y componentes principales](#arquitectura-y-componentes-principales)
- [Árbol de carpetas relevante](#árbol-de-carpetas-relevante)
- [Variables de entorno y configuración](#variables-de-entorno-y-configuración)
- [Ejecución con Docker (stack completo)](#ejecución-con-docker-stack-completo)
- [Modo desarrollador con hot reload](#modo-desarrollador-con-hot-reload)
- [Ejecución local sin Docker](#ejecución-local-sin-docker)
- [Scripts, Makefile y tareas de mantenimiento](#scripts-makefile-y-tareas-de-mantenimiento)
- [Datos semilla, credenciales y roles](#datos-semilla-credenciales-y-roles)
- [API JWT y autenticación híbrida](#api-jwt-y-autenticación-híbrida)
- [Pruebas, calidad y pipelines CI](#pruebas-calidad-y-pipelines-ci)
- [Solución de problemas frecuentes](#solución-de-problemas-frecuentes)
- [Guía de contribución y bitácoras](#guía-de-contribución-y-bitácoras)
- [Licencia y autoría](#licencia-y-autoría)

---

Arquitectura y componentes principales
--------------------------------------
- **Backend**: `src/main/java/` contiene el código fuente Spring Boot. La configuración de seguridad vive en `src/main/java/edu/unam/springsecurity/security/` y los controladores MVC en `controller/`.
- **Frontend servidor**: Vistas Thymeleaf en `src/main/resources/templates/` y estilos/recursos en `src/main/resources/static/`.
- **Persistencia**: Repositorios Spring Data JPA en `src/main/java/edu/unam/springsecurity/repository/`. Flyway migra el esquema desde `src/main/resources/db/migration/`.
- **Base de datos**: MariaDB 10.11, expuesta vía Docker y configurable por `.env`. Las credenciales se parametrizan en `application.yml`.
- **Contenedores**: `docker-compose.yml` define `db`, `app` (runtime empaquetado) y `app-dev` (modo desarrollo que monta el código).
- **Infraestructura auxiliar**:
  - `Makefile` autodetecta el stack e integra comandos (`make build`, `make test`, `make dev-up`, etc.).
  - `scripts/setup_java.sh` instala JDK y dependencias en entornos que carecen de ellas.
  - `docs/services/servitec.md` registra la bitácora pedagógica de cada intervención (revisar antes y después de cambios significativos).

---

Árbol de carpetas relevante
---------------------------
```
Servitec_Codex/
├── docker-compose.yml              # Orquestación Docker (app, db, app-dev)
├── Dockerfile                      # Imagen de la aplicación Spring Boot
├── Makefile                        # Comandos multi-stack con autodetección
├── Metodologia_Prompt_Mentor/      # Estrategia Bitácora Mentor (lectura obligada)
│   ├── STRATEGY_BITACORA_MENTOR.md
│   ├── CODEx_NOTES.md
│   ├── CODEx_NOTES_java.md
│   └── ...
├── docs/
│   └── services/
│       └── servitec.md             # Bitácora del servicio Servitec
├── src/
│   ├── main/
│   │   ├── java/edu/unam/springsecurity/
│   │   │   ├── controller/         # Controladores Thymeleaf + APIs
│   │   │   ├── dto/                # DTOs para vistas y servicios
│   │   │   ├── model/              # Entidades JPA
│   │   │   ├── repository/         # Interfaces Spring Data JPA
│   │   │   └── security/           # Configuración Spring Security + JWT
│   │   ├── resources/
│   │   │   ├── application.yml     # Configuración central (perfiles, JWT, DB)
│   │   │   ├── templates/          # Vistas Thymeleaf
│   │   │   ├── static/             # CSS/JS/Bootstrap
│   │   │   └── db/migration/       # Scripts Flyway (V1__... → V7__...)
│   └── test/                       # Punto de partida para pruebas automatizadas
├── uploads/                        # Carpeta local para archivos de técnicos
└── .env.example                    # Plantilla de variables de entorno
```

---

Variables de entorno y configuración
------------------------------------
1. **Archivo base**: duplica `.env.example` a `.env` y ajusta:
   - `MYSQL_ROOT_PASSWORD`: contraseña del usuario raíz de MariaDB (obligatoria).
   - `MYSQL_DATABASE`: nombre de la base (por defecto `ServitecDB`).
   - `JWT_SECRET`: cadena hex de 64 caracteres (≥256 bits) para firmar tokens.
   - `SERVER_PORT`: puerto de la app (8090 por defecto).
   - Si lo deseas, define `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` para usar credenciales distintas a root.
2. **Perfiles Spring**:
   - `application.yml` carga valores por defecto y lee variables de entorno.
   - El perfil `docker` se activa automáticamente dentro de los contenedores (ver `SPRING_PROFILES_ACTIVE`).
3. **Configuración avanzada**:
   - Activa logs extra de seguridad ajustando `logging.level` en `application.yml` (`src/main/resources/application.yml:1-31`).
   - Para entornos productivos, considera mover los secretos a un gestor seguro (GitHub Secrets, Vault, etc.).

---

Ejecución con Docker (stack completo)
-------------------------------------
> Recomendado para reproducir el entorno oficial con el mínimo esfuerzo.

1. **Prerrequisitos**:
   - Docker 24+ y Docker Compose 2+.
   - Puertos libres: `8090` (aplicación) y `3306` (base de datos, si expones el servicio).
2. **Primer arranque**:
   ```bash
   cp .env.example .env
   # edita .env con tus valores
   docker compose up --build
   ```
3. **Qué sucede en segundo plano**:
   - `db` levanta MariaDB con `--lower_case_table_names=1`, charset UTF-8 y healthcheck (`docker-compose.yml:3-28`).
   - `db-init/` se ejecuta automáticamente para semillas iniciales complementarias.
   - `app` espera a que la base esté saludable, aplica migraciones Flyway (`V1__init.sql` hasta `V7__qa_admin_account.sql`) y arranca Spring Boot con el perfil `docker`.
   - `uploads/` se monta para almacenar firmas/certificaciones de técnicos.
4. **Accesos**:
   - Aplicación: <http://localhost:8090>
   - MariaDB: `localhost:3306` (usuario `root`, contraseña definida en `.env`).
5. **Hot reload automático**:
   - Con Docker Compose v2.22+ ejecuta `docker compose watch` o `make watch`. El servicio `app` se reconstruye cuando cambian `src/`, `pom.xml` o `Dockerfile`.
6. **Recrear la base de datos**:
   ```bash
   docker compose down -v
   docker compose up --build
   ```
   Esto elimina volúmenes y arranca desde cero (útil tras cambios mayores en migraciones).

---

Modo desarrollador con hot reload
---------------------------------
> Usa `app-dev` cuando necesites retroalimentación inmediata sin reconstruir contenedores.

1. **Levantar en background**:
   ```bash
   make dev-up        # inicia db + app-dev (mvn spring-boot:run con Devtools)
   make dev-logs      # sigue únicamente los logs de la app
   ```
   Alternativa en primer plano: `make dev-up-fg`.
2. **Requisitos**:
   - Docker Compose ≥ 2.20.
   - Volumen `m2_cache` persistente para dependencias Maven.
   - Auto guardado del editor y compilación automática (VS Code → Java: habilita “Build Automatically”) para que Spring Devtools detecte cambios en `target/classes`.
3. **Cómo funciona**:
   - Se monta todo el repositorio en `/workspace`.
   - El comando `mvn -q -DskipTests -Dspring-boot.run.profiles=docker spring-boot:run` recarga al compilar.
   - El puerto público sigue siendo <http://localhost:8090>.
4. **Terminación**: `docker compose down` o `make dev-down` (si agregas un alias similar).

---

Ejecución local sin Docker
--------------------------
> Adecuado si prefieres administrar MariaDB manualmente o trabajar offline.

1. **Descarga y prepara el proyecto**:
   - Clona el repositorio o descarga el `.zip`.
   - Asegúrate de tener **JDK 17 o 21** (el repo incluye Amazon Corretto 21 en `vendor/`).
2. **Base de datos**:
   - Instala MariaDB 10.11+ (MySQL compatible).
   - Crea una base (p. ej. `servitecdb`).
   - Ejecuta los scripts SQL:
     - Opción rápida: importar `ServitecDB.txt`.
     - Opción oficial: dejar que Flyway ejecute `src/main/resources/db/migration/V1__init.sql` → `V7__qa_admin_account.sql`.
3. **Configura credenciales**:
   - Edita `src/main/resources/application.yml` o exporta variables:
     ```properties
     spring.datasource.url=jdbc:mariadb://localhost:3306/servitecdb
     spring.datasource.username=TU_USUARIO
     spring.datasource.password=TU_PASSWORD
     ```
4. **Compilación y arranque**:
   ```bash
   ./mvnw -q -DskipTests compile
   ./mvnw spring-boot:run
   ```
   La aplicación quedará disponible en <http://localhost:8090>.
5. **Uso de Maven wrapper**:
   - Si no tienes Maven global, el wrapper descarga la versión correcta.
   - Puedes usar el JDK portátil: `JAVA_HOME=$PWD/vendor/amazon-corretto-21.0.8.9.1-linux-x64 ./mvnw spring-boot:run`.

---

Scripts, Makefile y tareas de mantenimiento
-------------------------------------------
- `make setup`: verifica JDK (instala si falta) y precarga dependencias (`dependency:go-offline`).
- `make build`: compilación sin tests (`mvn -q -DskipTests compile`). Basado en el stack autodetectado (`Makefile:6-103`).
- `make lint`: ejecuta `mvn verify` con SpotBugs y validaciones.
- `make test`: dispara la suite de pruebas (`mvn test`).
- `make run`: inicia `spring-boot:run` con detección automática de `JAVA_HOME`.
- `make clean`: limpia artefactos (`mvn -q clean`).
- `make watch`: utiliza `docker compose watch` para rebuild automático.
- `scripts/setup_java.sh`: instala Amazon Corretto y ajusta permisos del wrapper (útil en CI o entornos nuevos).

> Consejo Prompt Mentor: documenta los resultados de cada comando significativo en `docs/services/servitec.md` para mantener trazabilidad.

---

Datos semilla, credenciales y roles
-----------------------------------
- **Migraciones Flyway**:
  - `V1__init.sql`: crea tablas principales (usuarios, técnicos, categorías, servicios, tarifas, etc.) y semillas básicas (admin/usuario demo).
  - `V2__seed_tecnicos.sql` y `V3__seed_tecnico_username.sql`: añaden técnicos de ejemplo.
  - `V5__seed_tecnicos_relaciones.sql`: inserta técnicos adicionales con relaciones idempotentes.
  - `V6__solicitud_workflow.sql`: prepara estados y tablas auxiliares para el flujo de solicitudes/agendas.
  - `V7__qa_admin_account.sql`: garantiza un administrador QA (`5555555555` / `Contraseña1#`) para pipelines de seguridad.
- **Usuarios iniciales (form login)**:
  - Usuario final: `user` / `password`.
  - Administrador: `admin` / `password`.
  - Técnico demo: `tecnico@servitec.local` / `password`.
  - Cuenta QA (pipeline): `5555555555` / `Contraseña1#` (recomendado guardar en secretos CI).
- **Dónde se almacenan archivos**:
  - Las e.firmas y certificaciones que suben los técnicos se guardan en `uploads/` (o `/app/uploads` dentro del contenedor). Esta ruta se expone en la app para descargar/ver documentos.

---

API JWT y autenticación híbrida
-------------------------------
- **Endpoints clave**:
  - `POST /auth/login`: recibe `{ "correo": "<email_o_tel>", "contrasena": "<pwd>" }`. Retorna `{"token": "<JWT>"}` y setea cookie `ACCESS_TOKEN` (HttpOnly, SameSite=Lax).
  - `POST /auth/refresh`: lee el token desde header `Authorization: Bearer ...` o cookie y entrega uno nuevo.
  - `GET /auth/logout`: invalida la cookie.
- **Cookies + Form login**:
  - Spring Security combina un formulario tradicional (`/login` → `/login_sesion`) con filtros JWT. El filtro toma el token automáticamente de la cookie.
  - Para fetch/AJAX desde otro origen, usa `credentials: 'include'` y asegúrate de configurar CORS (`SecurityConfiguration.java:42-88`).
- **Claims del token**:
  - `sub` (username), `uid` (ID interno), `roles` (lista de authorities), `username`.
  - Las expiraciones se controlan en `application.yml` (`jwt.expirationDateInMs` y `jwt.refreshExpirationDateInMs`).
- **Seguridad adicional**:
  - CSRF habilitado con repositorio de cookies. Si haces peticiones manuales (curl, CI), recupera `_csrf` desde `/login` antes del POST (ver `.github/workflows/prompt_mentor_ci.yml:83-123` para un ejemplo completo).

---

Pruebas, calidad y pipelines CI
-------------------------------
- **Pruebas locales**:
  - `./mvnw test`: ejecuta la suite unitaria/integración configurada.
  - `make test`: alias que asegura entorno correcto.
- **Análisis estático**:
  - `./mvnw spotbugs:spotbugs` genera `target/spotbugsXml.xml`.
  - Los reportes se suben como artefactos en CI.
- **GitHub Actions**: `.github/workflows/prompt_mentor_ci.yml:1-149`.
  - Jobs: *Security Scans (SpotBugs + ZAP)*.
  - Pasos clave: build, SpotBugs, arranque headless, autenticación QA (con fallback documentado), escaneo OWASP ZAP en modo baseline.
  - Requiere secretos `ZAP_QA_USER` y `ZAP_QA_PASS` (o usa los valores por defecto si no están definidos, aunque se recomienda configurarlos para no exponer credenciales en logs).
- **Métricas ISO/IEC 25010** (alineadas con Prompt Mentor):
  - *Fiabilidad*: éxito de migraciones Flyway y pipelines CI.
  - *Mantenibilidad*: deuda técnica rastreada vía SpotBugs y bitácoras.
  - *Seguridad*: fortaleza de JWT, escaneos ZAP, registros de CSRF.

---

Solución de problemas frecuentes
--------------------------------
| Problema | Causa probable | Solución |
|----------|----------------|----------|
| `WeakKeyException` al iniciar | `JWT_SECRET` corto o débil (`application.yml:34-38`) | Define un secreto hex de ≥64 caracteres en `.env`. |
| `No compiler is provided` al compilar | Solo hay JRE disponible | Exporta `JAVA_HOME` a `vendor/amazon-corretto-21.0.8.9.1-linux-x64` o instala JDK. |
| Flyway reporta `Found non-empty schema but no history table` | DB con datos previos sin baseline | Activa `spring.flyway.baseline-on-migrate=true` (ya preconfigurado) o ejecuta `flyway repair`. |
| Pipeline falla en paso QA login | Falta CSRF/token en POST | Revisa el script en `.github/workflows/prompt_mentor_ci.yml:83-123`; asegura que `/login` sea accesible y que las credenciales existan (`V7__qa_admin_account.sql`). |
| Recursos estáticos no cargan | Ruta incorrecta o caché | Verifica `src/main/resources/static/tema/miestilo.css` y el mapeo en Thymeleaf (`templates/login.html:1-52`). |
| MariaDB rechaza conexión desde la app | Contraseña/env variables inconsistentes | Revisa `.env`, `docker-compose.yml:33-55` y el `SPRING_DATASOURCE_PASSWORD`. |

---

Guía de contribución y bitácoras
--------------------------------
1. **Antes de comenzar**:
   - Lee `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md` y marca el checklist inicial.
   - Revisa `docs/services/servitec.md` para entender el estado actual, desbloqueos y métricas.
2. **Durante el desarrollo**:
   - Documenta cada decisión relevante en `docs/services/servitec.md` (incluye fecha, contexto, comandos y resultados).
   - Si descubres patrones transferibles, añádelos a `Metodologia_Prompt_Mentor/PATRONES_REFERENCIA.md`.
   - Mantén sincronizadas las notas globales (`Metodologia_Prompt_Mentor/CODEx_NOTES.md`) y las especializadas (`CODEx_NOTES_java.md`).
3. **Formato de commits**:
   - Usa prefijos como `servitec:` o `tool:` según el alcance. Ejemplo: `servitec: ajustar flujo de login técnico`.
   - Sigue la plantilla:
     ```
     <servicio|tool>: <resumen imperativo>

     - <detalle 1>
     - <detalle 2>

     Refs: <ticket|bitácora>
     ```
4. **Pull requests**:
   - Adjunta resultados de `make test` / `make lint` cuando apliquen.
   - Enlaza la sección de la bitácora donde se registró el cambio.

---

Licencia y autoría
------------------
- Proyecto desarrollado por **Rodrigo Olvera** como parte del *Proyecto final – Módulo 10*.
- Revisa `LICENSE` para conocer los términos de uso y distribución.
- Si tienes dudas o propuestas de mejora, documenta el contexto en la bitácora y abre un issue o PR siguiendo la metodología Prompt Mentor.

---
Test 




¡Listo! Con esta guía exhaustiva deberías poder clonar, ejecutar, depurar y extender Servitec con confianza, manteniendo la trazabilidad y buenas prácticas exigidas por la colaboración Codex ↔ Equipo. Recuerda siempre actualizar las bitácoras tras cada avance significativo. Felices despliegues. 🚀
