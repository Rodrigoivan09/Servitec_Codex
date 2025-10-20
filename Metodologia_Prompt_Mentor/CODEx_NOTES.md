# Estrategia "Bitácora Mentor"

Guía interna para futuras sesiones con Codex en proyectos de `rodrigo`. Resume estructuras clave, acuerdos de trabajo y la bitácora global de errores/soluciones. Está escrita en español porque es el idioma habitual de colaboración y sirve como prompt base cada vez que se abra el repositorio.

- **Metodología oficial**: *Metodología Prompt Mentor* creada por Rodrigo Iván Olvera Martínez Polo.
- **Versión de referencia**: 1.3 (2025-09-24).

## Propósito
1. **Contexto inmediato**: ofrecer un resumen narrado de cómo está armado el backend y qué servicios viven aquí.
2. **Bitácora continua**: centralizar los pasos significativos, errores y correcciones para replicarlos o evitarlos.
3. **Ruta de consulta rápida**: saber en qué directorios buscar al mencionar un microservicio o archivo específico.
4. **Puente con variantes especializadas**: enlazar la versión de notas específica para este tipo de proyecto cuando exista.
5. **Recordatorio operativo**: ante cualquier error, consultar este archivo y el `CODEx_NOTES_<tipo>.md` correspondiente antes de investigar desde cero; tras resolver o conseguir un éxito, registrar inmediatamente la lección aquí y en la bitácora del servicio afectado.

> **Nota recursiva**: cada vez que registremos un logro o mejora en esta guía, validar si debe propagarse a `STRATEGY_BITACORA_MENTOR.md` y al `CODEx_NOTES_<tipo>.md` correspondiente. Mantener sincronizados los tres niveles (estrategia global → nota especializada → esta nota). Codex debe revisar primero `STRATEGY_BITACORA_MENTOR.md` en cada sesión para recordar estas reglas.

## Metodología Prompt Mentor
> Referencia de cambios: consulta la sección 10 de `STRATEGY_BITACORA_MENTOR.md` para el historial de versiones y notas detalladas.
- Idioma principal de documentación: **español** (adaptar si el equipo cambia).
- Estrategia activa: **Bitácora Mentor** con enfoque de diseño orientado a prompts/IA.
- Objetivo: capturar patrones reproducibles de colaboración con IA.
- Tareas automáticas al abrir el repo:
  1. Verificar que esta guía exista y, si no, crearla.
  2. Confirmar que existe una nota especializada `CODEx_NOTES_<tipo>.md` acorde al dominio; si falta, usa el script de bootstrap con el tipo correcto.
  3. Asegurar que cada servicio tenga su documento en `docs/services/`.
  4. Identificar innovaciones fuera de lo convencional y asignarles un nombre propio; registrar si se necesita una nueva categoría (`CODEx_NOTES_<categoria>.md`).
  5. Ejecutar el script opcional `./scripts/bootstrap_prompt_mentor.sh` (desde esta carpeta) o `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh` (desde la raíz) cuando necesites copiar la estrategia a otro repositorio o regenerar una nota especializada.

## Objetivos por horizonte (actualización 2025-10-04)
- **Corto plazo (≤ 2 semanas)**: ✅ 2025-10-04 — CODEOWNERS creado y plan de incidentes documentado en `SECURITY.md`; mantener revisión periódica para nuevas carpetas.
- **Mediano plazo (1-2 meses)**: profundizar el pipeline de seguridad habilitando credenciales para escaneos OWASP ZAP autenticados y refinando los umbrales de Bandit/ZAP; completar la migración de datos reales a los snapshots de `Metodologia_Prompt_Mentor/generated/`.
- **Largo plazo (3+ meses)**: extender la metodología a todos los servicios asociados (crear `docs/services/<servicio>.md`, notas especializadas y `model_card.md` cuando aplique), vinculando métricas de desempeño/fiabilidad en la bitácora EBSE y revisando la estrategia trimestralmente conforme a ISO/IEC 12207/15288.

## Notas especializadas
- `CODEx_NOTES_java.md`: guía activa para este proyecto Servitec (Spring Boot + Thymeleaf + MariaDB). Resume prerequisitos locales (`mvnw`, JDK 17+/21), perfiles y errores frecuentes.
- `CODEx_NOTES_backend.md`: referencia heredada del ecosistema FastAPI y Docker. Manténla como biblioteca de patrones cuando interactuemos con los microservicios Python asociados (validator, auth_user, scrappers, etc.).
- `PATRONES_REFERENCIA.md`: catálogo 360° de patrones, arquitecturas, stacks y criterios de selección.
- Los snapshots generados automáticamente (`Metodologia_Prompt_Mentor/generated/services/*.md`) provienen del repo de microservicios y sirven como ejemplos de bitácoras extensas. Úsalos como inspiración al documentar nuevos componentes.
- `docs/catalog/languages.md`: catálogo de lenguajes y referencias cruzadas para iniciar auto-onboarding por stack.
- Si aparece un nuevo dominio (frontend, mobile, data, IA), crea `CODEx_NOTES_<tipo>.md`, enlázalo desde aquí y desde `STRATEGY_BITACORA_MENTOR.md`, y agrega su entrada en `docs/catalog/languages.md`.

## Índice de documentación operativa
- `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md`: carta magna de la metodología; explica principios, versionado y cómo sincronizar bitácoras entre servicios y notas especializadas.
- `Metodologia_Prompt_Mentor/PROMPT_INICIALIZACION.md`: prompt que debe leerse antes de cada sesión; resume el checklist previo, la narrativa pedagógica y los pasos de cierre.
- `Metodologia_Prompt_Mentor/CODEx_NOTES_java.md`: nota especializada del stack Java/Spring; checklist de entorno, errores frecuentes y métricas activas para Servitec.
- `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md`: guía heredada para microservicios FastAPI; usarla cuando alguna tarea cruce con el ecosistema LiberSalus.
- `Metodologia_Prompt_Mentor/AUTO_ONBOARDING_BITACORA.md`: historial del flujo de auto-onboarding; registra decisiones y pendientes de los scripts `auto_onboarding_*`.
- `Metodologia_Prompt_Mentor/PATRONES_REFERENCIA.md`: catálogo de patrones y arquitecturas nombradas; anota aquí nuevos hallazgos antes de replicarlos en otros repos.
- `Metodologia_Prompt_Mentor/README.md`: manual para clonar la metodología (`bootstrap_prompt_mentor.sh`) y adaptar notas especializadas en otros proyectos.
- `Metodologia_Prompt_Mentor/generated/services/*.md`: snapshots generados automáticamente por servicio; sirven como ejemplos extensos de bitácoras y no deben editarse manualmente.
- `AGENTS.md`: reglas de colaboración Codex ↔ equipo (nomenclaturas, métricas ISO 25010, TODO global de CODEOWNERS e incidentes críticos).
- `ARTIFACTS.md`: inventario de scripts, datasets y validaciones reproducibles; explica cómo orquestar builds con el Makefile y dónde documentar evidencias.
- `SECURITY.md`: políticas de secretos, respuesta a incidentes y riesgo IA; mantenerlo sincronizado con responsables definidos en `CODEOWNERS`.
- `HELP.md`: notas generadas por Spring Initializr; conservarlas como referencia rápida de Maven/Spring Boot.
- `README.md`: guía operativa del proyecto Servitec (modos Docker y local, usuarios de prueba, recomendaciones de desarrollo).
- `docs/services/servitec.md`: bitácora pedagógica del servicio principal; registra cada sesión, validaciones y pendientes.
- `docs/catalog/languages.md`: catálogo de stacks soportados; enlaza notas especializadas y bitácoras por lenguaje.
- `Metodologia_Prompt_Mentor/CODEx_NOTES_template.md`: plantilla base para generar nuevas notas especializadas (`CODEx_NOTES_<tipo>.md`).

## Servicios y carpetas principales
- `src/main/java/`: código de la aplicación Servitec (controladores MVC, entidades JPA, seguridad y DTOs).
- `src/main/resources/`: propiedades (`application.yml`, perfiles docker/dev), plantillas Thymeleaf (`templates/`), assets (`static/`) y scripts Flyway (`db/migration/`).
- `docs/services/servitec.md`: bitácora pedagógica del servicio principal; documenta builds, incidentes (JWT, DB) y próximos pasos.
- `Metodologia_Prompt_Mentor/`: estrategia Bitácora Mentor completa, notas especializadas y scripts de auto-onboarding.
- `vendor/amazon-corretto-21.0.8.9.1-linux-x64/`: JDK portátil descargado para compilar sin depender del sistema anfitrión (puede ignorarse en VCS si prefieres un JDK global).
- `docker-compose.yml`: orquesta la base MariaDB (`service db`) usada en desarrollo local. Ejecutar con Docker Desktop o daemon nativo.
- `uploads/`: almacén local de archivos subidos (efirma, certificaciones, fotos de técnicos) durante las pruebas.
- Para colaborar con los microservicios Python relacionados, consulta el repo `libersalus/backend` y las notas en `Metodologia_Prompt_Mentor/generated/`.

## Estrategia "Bitácora Mentor"
- **Qué es**: una bitácora narrativa donde cada acción se documenta como si se estuviera enseñando el proceso paso a paso.
- **Por qué se adoptó**: combina prácticas de postmortem ágil y documentación vivencial; permite que nuevos turnos (incluido Codex) entiendan el razonamiento detrás de cada comando o fix.
- **Cómo se aplica**:
  1. Cada cambio relevante dispara la actualización de la bitácora en el `.md` del servicio implicado.
  2. Siempre se registran problemas encontrados y su resolución detallada.
  3. Se enlaza al contexto o archivos afectados para facilitar futuras búsquedas.

## Bitácora global (septiembre 2025)

### 2025-10-04 — Brújula temporal de la metodología
- Se añadió la sección "Objetivos por horizonte" para priorizar la limpieza de `CODEOWNERS`, la respuesta a incidentes en `SECURITY.md` y las automatizaciones SAST/DAST, alineando los KPI de mantenibilidad/fiabilidad con la Estrategia Bitácora Mentor.
- Se creó `CODEOWNERS` asignando a @rodrigo como responsable de carpetas clave (`src/`, `docs/`, `Metodologia_Prompt_Mentor/`, `scripts/`, artefactos raíz) y se marcó el TODO correspondiente en `AGENTS.md`.
- Se documentó el plan operativo de incidentes críticos en `SECURITY.md`, definiendo responsables, canales, cronograma y checklist posterior para mantener trazabilidad ISO/IEC 25010/15288.
- Se añadió el job `security-scans` al workflow `Prompt Mentor CI` para automatizar SAST+DAST; desde 2025-10-06 se ejecuta SpotBugs/FindSecBugs y el baseline de OWASP ZAP con despliegue temporal de Servitec.

### 2025-10-06 — Servitec bajo escaneo automatizado
- El workflow `Prompt Mentor CI` ahora construye el proyecto Java con Temurin 21, ejecuta SpotBugs/FindSecBugs como SAST y levanta MariaDB + Spring Boot para que OWASP ZAP baseline inspeccione `http://127.0.0.1:8090` (`.github/workflows/prompt_mentor_ci.yml`).
- Los reportes `spotbugs-report` y `zap-baseline-report` se publican como artefactos; revisar hallazgos y registrar mitigaciones en la bitácora del servicio (`docs/services/servitec.md`).
- `SECURITY.md` detalla el monitoreo preventivo actualizado y mantiene como pendiente la autenticación de ZAP para rutas protegidas.
- ZAP se autentica reutilizando el usuario QA definido en los Secrets `ZAP_QA_USER` y `ZAP_QA_PASS` (cookie `JSESSIONID` inyectada mediante `replacer.full_list`); mantener estas credenciales con privilegios mínimos y rotación periódica.
- Siguiente iteración: evaluar creación de un usuario específico de solo lectura para CI y revisar el primer `zap-baseline-report` para confirmar cobertura de vistas restringidas.

### Empaquetado y despliegue de `validator_employee`
1. **Construcción local**: `docker build -t us-central1-docker.pkg.dev/codex-472522/backend/validator-employee:latest validator_employee`
   - *Objetivo*: crear imagen desde el Dockerfile localizado en el directorio del servicio.
   - *Aprendizaje*: el primer intento falló por omitir el punto final (contexto). Se resolvió repitiendo con el parámetro de ruta.
2. **Publicación**: `docker push us-central1-docker.pkg.dev/codex-472522/backend/validator-employee:latest`
   - *Objetivo*: enviar la imagen a Artifact Registry para que la VM la consuma.
3. **Cloud Shell**: `docker pull ...` seguido de `gcloud compute instances list`
   - *Objetivo*: validar acceso al registro y confirmar VM objetivo (`liber-salus`).
4. **Acceso a la VM**: `ssh rod@34.10.64.56`
   - *Nota*: la conexión se hace directo porque `gcloud compute ssh` solicitó generar claves nuevas; se mantuvo la llave existente.
5. **Verificar Nginx**: `sudo systemctl status nginx`
   - *Resultado*: servicio activo (proxy escuchando peticiones públicas).
6. **Autenticación GCP dentro de la VM**:
   - `gcloud auth login` → autorizó la cuenta personal.
   - `gcloud auth configure-docker us-central1-docker.pkg.dev` → registró helper para Docker.
   - Error: al usar `sudo docker`, Artifact Registry seguía negando acceso.
   - Solución: `gcloud auth print-access-token | sudo docker login -u oauth2accesstoken --password-stdin https://us-central1-docker.pkg.dev` para insertar credenciales en el perfil `root` que usa `sudo`.
7. **Descarga y ejecución**:
   - `sudo docker pull ...`
   - `sudo docker run -d --name validator --restart unless-stopped -p 127.0.0.1:8040:8040 us-central1-docker.pkg.dev/codex-472522/backend/validator-employee:latest`
   - *Error previo*: el daemon no corría (`systemctl status docker` decía “Unit ... could not be found”). Se instaló `docker.io` y se habilitó el servicio (`sudo systemctl enable --now docker`).
8. **Recarga pendiente**: `sudo systemctl reload nginx` se deja como paso final tras verificar aplicación.

### Errores frecuentes y soluciones
- **Docker host inválido (`tcp://localhost:2375`)**: se resolvió limpiando la variable `DOCKER_HOST` o volviendo a trabajar con el socket unix por defecto.
- **Falta de Dockerfile en raíz**: se recordó usar el contexto `validator_employee/` o la bandera `-f` para apuntar al archivo correcto.
- **Permisos Artifact Registry**: usar autenticación basada en token para `sudo docker` o agregar el usuario al grupo `docker` y evitar `sudo`.
- **Servicio Docker ausente**: instalar `docker.io` y habilitar el servicio en la VM.

### Automatización CI/CD de `validator_employee` (24-sep-2025)
1. **Revocación de llave comprometida**: desde IAM → Cuentas de servicio → `Administrar claves`, se eliminó el `private_key_id` expuesto y se generó un JSON nuevo.
   - *Riesgo reducido*: evitamos que terceros reutilicen credenciales filtradas.
2. **Alta de secretos en GitHub Actions**: se cargó `GCP_SA_KEY` con el JSON nuevo y se añadieron `LIBERSALUS_SSH_USER` (`rodrigo`) y `LIBERSALUS_SSH_KEY` (clave privada ed25519 sin passphrase).
   - *Errores previos*: el pipeline fallaba por `Unauthenticated request` al no existir el helper; quedó mitigado almacenando el token en Actions.
3. **Provisionamiento de clave SSH dedicada**: `ssh-keygen -t ed25519 -f ~/.ssh/libersalus_deploy -C "deploy@libersalus"` sin passphrase, se publicó la clave (`cat ~/.ssh/libersalus_deploy.pub >> ~/.ssh/authorized_keys`) y se aseguraron permisos (`chmod 700 ~/.ssh`, `chmod 600 ~/.ssh/authorized_keys`).
4. **Sincronización local y tests**: se trajo la clave privada con `scp` cuando fue necesario y se verificó acceso (`ssh -i ~/.ssh/libersalus_deploy rodrigo@34.10.64.56`).
5. **Ejecución del pipeline**: tras documentar la sesión, se hace `git status`, `git add`, `git commit` y `git push main` para disparar `.github/workflows/deploy-validator.yml`.
   - *Verificación*: en la pestaña Actions revisar que los pasos `Build & Push` y `Deploy` terminen en verde.
6. **Validación en la VM**: `sudo docker ps --filter name=validator_employee` confirma el contenedor actualizado; si hubo cambios funcionales, correr pruebas puntuales (`curl http://127.0.0.1:8040/health`).
   - *Seguimiento de errores*: si falla la publicación, repetir el flow de claves y revisar que los secretos no expiren.
7. **Cambio de usuario a `Timoris`**: se reutilizó la llave existente en `~/.ssh/libersalus_ci`, se copió a la máquina local con `gcloud compute scp Timoris@liber-salus:~/.ssh/libersalus_ci ~/.ssh/libersalus_ci`, se aseguró con `chmod 600` y se actualizaron los secretos `LIBERSALUS_SSH_USER` (valor `Timoris`) y `LIBERSALUS_SSH_KEY`.

### Éxitos y mejoras propagadas
- **Estrategia Bitácora Mentor institucionalizada (septiembre 2025)**: se formalizó la guía `STRATEGY_BITACORA_MENTOR.md`, habilitando un circuito recursivo de documentación. Recordar actualizar ese archivo con cualquier nuevo patrón exitoso y crear la nota especializada del dominio si aún no existe.
- **Nota especializada backend creada (septiembre 2025)**: `CODEx_NOTES_backend.md` captura procedimientos de despliegue y errores resueltos para microservicios FastAPI. Mantenerla sincronizada con las bitácoras de cada servicio y con la estrategia global.
- **Escáner de auto-onboarding (septiembre 2025)**: se añadió `Metodologia_Prompt_Mentor/scripts/auto_onboarding_scan.py`, que inventaría servicios (FastAPI/Flask/compose), puertos y artefactos Docker para alimentar la documentación automática de la Fase 2.
- **Generador de documentación (septiembre 2025)**: `Metodologia_Prompt_Mentor/scripts/auto_onboarding_generate.py` crea borradores en `Metodologia_Prompt_Mentor/generated/` (resumen global y plantillas por servicio) para revisión manual antes de integrarlos.

## Patrones de diseño orientados a prompts
- **Despliegue manual con Docker + Artifact Registry**: prompt base documentado en `docs/services/validator_employee.md`; replicable en otros servicios backend.
- **Autenticación híbrida gcloud + Docker**: usar token de acceso para `sudo docker`; registrar siempre comandos y justificación.
- **Bitácora pedagógica**: cada actualización debe describir qué problema resolvimos, cómo lo detectamos y qué pasos concretos se siguieron para que la IA pueda reproducirlos.
- **Checklist IA futuro**: planificar automatizaciones (Cloud Build, pruebas) y documentar qué piezas manuales quedan pendientes para transformarlas en prompts o scripts.
- **Mentoría reusable**: mantener catálogos de innovaciones nombradas y enlazarlas con la categoría correspondiente para aplicarlas en otros sistemas; cuando añadas un patrón nuevo, documenta la entrada también en `PATRONES_REFERENCIA.md`.
- **Bootstrap portátil**: para clonar la estrategia en otro repositorio, usa `./scripts/bootstrap_prompt_mentor.sh <ruta_destino>` desde esta carpeta (o `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh <ruta_destino>` desde la raíz) y luego ajusta los archivos al nuevo contexto.

## Calidad y métricas (ISO/IEC 25010)
- **Eficiencia de desempeño**: registrar p95 de respuesta, consumo de memoria/CPU y objetivos por servicio.
- **Fiabilidad**: dar seguimiento a defectos/KSLOC, MTBF, MTTR y errores de despliegue.
- **Usabilidad**: cuando aplique, medir SUS/NPS y tasa de tareas completadas; documentar hallazgos en la bitácora EBSE.
- **Mantenibilidad**: medir deuda técnica/mes, complejidad y cobertura de pruebas; actualizar en la nota especializada.
- **Portabilidad**: medir tiempo de despliegue cross-env e incidencias de compatibilidad.
- Cada KPI debe vincularse al template DSR (métricas/amenazas) y registrarse en `AUTO_ONBOARDING_BITACORA.md`.

## Gestión de riesgo IA (NIST AI RMF + ISO/IEC 22989)
- **Govern**: establecer roles/responsables y políticas en `SECURITY.md` y bitácoras.
- **Map**: describir propósito del sistema IA, datos y supuestos en `model_card.md`.
- **Measure**: definir métricas de desempeño/robustez (precisión, sesgo, p95 de inferencia) y evidencias de validación.
- **Manage**: programar monitoreo, mitigaciones y revisiones periódicas; registrar resultados en la bitácora EBSE.
- Terminología base ISO/IEC 22989 (artefacto IA, riesgo residual, uso previsto) debe aparecer en la documentación relevante.

## Paquete de reproducibilidad (ACM Badging)
- **Scripts**: `Metodologia_Prompt_Mentor/scripts/auto_onboarding_*.py`, `scripts/codex_bootstrap_env.sh`, Makefile.
- 2025-10-02 — **Makefile multistack**: se reescribió `Makefile:1-192` para detectar automáticamente el stack (Maven, Gradle, npm, Poetry, Go, Cargo, etc.), exponer objetivos genéricos (`make setup`, `make build`, `make lint`, `make test`, `make run`, `make format`, `make clean`) y reiniciar Docker Compose con `down -v --remove-orphans` antes de cada `compose-up`. Usa `FORCE_STACK=<stack> make <target>` o variables `<TARGET>_CMD` para personalizar comandos y replica esta plantilla en otros repositorios cuando se descubran mejoras. Nota: si se añade lógica genérica, evita preasignar `*_CMD` a vacío y recuerda que `VERBOSE=1` deshabilita los flags de `-q/--quiet` para depurar builds.
- 2025-10-09 — **Bootstrap JDK + base de datos**: `scripts/setup_java.sh` instala Amazon Corretto 21 en `vendor/` y asegura `JAVA_HOME` en `.env`. `make run` invoca `scripts/ensure_db.sh` para levantar `docker compose db` y esperar estado `healthy` antes de iniciar Spring Boot; reutiliza el target `ensure-db` si solo se necesita la base.
- 2025-10-09 — **Flujo maestro de solicitudes**: `V6__solicitud_workflow.sql` crea campos para adjuntos, motivaciones y tiempos de respuesta. `SolicitudServiceImpl` adjunta archivos sanitizados en `uploads/solicitudes/<id>` y el técnico gestiona aceptación/declinación desde `TecnicoController` + vistas Thymeleaf. Reusar estos patrones cuando otro servicio requiera matching con multimedia.
- **Datos**: listar datasets o fixtures (ubicación, licencia, formato) siguiendo principios FAIR.
- **Seeds/configuración**: especificar semillas, variables en `.env.example` y parámetros de despliegue.
- **Validaciones automáticas**: enlazar CI (`.github/workflows/prompt_mentor_ci.yml`) y comandos `make lint/test`.
- Mantener evidencia de ejecuciones (logs, reportes) para reproducibilidad externa.

## Uso sugerido de esta guía
1. Leer esta sección al iniciar sesión para refrescar qué servicios hay y qué se hizo en la última iteración.
2. Ante cambios futuros, actualizar tanto esta bitácora global como la sección específica del servicio en `docs/services/<servicio>.md`.
3. Ampliar con nuevas lecciones aprendidas (por ejemplo, automatización de despliegues, scripts de pruebas, etc.).

## Próximos pasos recomendados
- Automatizar builds/push con Cloud Build o GitHub Actions.
- Añadir variables de entorno/volúmenes al comando `docker run` según requiera la aplicación (credenciales de correo, Twilio, etc.).
- Documentar flujos de los demás servicios en el mismo formato narrativo para mantener consistencia.
