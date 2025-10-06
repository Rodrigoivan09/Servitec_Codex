# Guía Especializada: Backend Microservicios (Bitácora Mentor)

Esta nota acompaña al archivo `CODEx_NOTES.md` general y profundiza en proyectos backend basados en microservicios FastAPI + Docker, como el que estamos trabajando. Se actualiza de forma recursiva junto con `STRATEGY_BITACORA_MENTOR.md` (versión 1.2, 2025-09-23) y la bitácora específica de cada servicio. Forma parte de la *Metodología Prompt Mentor* desarrollada por Rodrigo Iván Olvera Martínez Polo.

Consulta `docs/catalog/languages.md` para ver el catálogo multi-stack y ubicar rápidamente las notas de otros lenguajes.

## 1. Contexto rápido del ecosistema
- **Lenguaje y framework**: Python 3.11, FastAPI.
- **Servicios principales**: `validator_employee`, `auth_user`, `server_file`, `server_email`, `server_data`, `server_catalog`, `server_cat_*`, `server_ia`.
- **Infraestructura estándar**:
  - Contenedores Docker individuales por microservicio.
  - Artifact Registry (GCP) para almacenar imágenes.
  - VM con Nginx como reverse proxy (ejemplo: `liber-salus`, zona `us-central1-f`).
  - Docker Compose opcional para entorno local (`docker-compose.yml`).

## 2. Prompt operativo al iniciar sesión
> **Recordatorio**: estás bajo la Estrategia "Bitácora Mentor" y la Metodología Prompt Mentor. Antes de cualquier acción debes leer `STRATEGY_BITACORA_MENTOR.md`, luego este archivo, y finalmente el `.md` del servicio en el que trabajarás. Documenta cada paso, éxito y error en los tres niveles (servicio → esta nota → estrategia) y crea cualquier archivo faltante.

Checklist inicial:
- [ ] Revisaste `CODEx_NOTES.md` para contexto global.
- [ ] Consultaste esta nota para conocer convenciones backend.
- [ ] Identificaste el servicio y abriste su bitácora (`docs/services/<servicio>.md`).
- [ ] Preparaste secciones para registrar nuevos éxitos o mejoras.
- [ ] Detectaste si surgió un patrón no convencional y, de ser así, le asignaste un nombre y categoría adecuada.
- [ ] ¿Necesitas replicar la metodología en otro repositorio? Ejecuta `./scripts/bootstrap_prompt_mentor.sh <ruta_destino>` desde esta carpeta (o `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh <ruta_destino>` desde la raíz del repo base).
- [ ] Revisaste `PATRONES_REFERENCIA.md` para elegir patrones y arquitecturas que encajen con el caso actual.
- [ ] Si añadiste o ajustaste un patrón, registraste la actualización en `PATRONES_REFERENCIA.md` con fecha y referencia a la bitácora del servicio.

## 3. Logros y aprendizajes existentes (septiembre 2025)
1. **Despliegue exitoso de `validator_employee` a GCP**
   - Build local con contexto correcto (`docker build ... validator_employee`).
   - Publicación en Artifact Registry (`docker push`).
   - Resolución de errores: falta de `.` en build, Docker daemon detenido en VM, permisos Artifact Registry para `sudo docker`.
   - Documentado en `docs/services/validator_employee.md` y `CODEx_NOTES.md`.
2. **Autenticación híbrida Docker + gcloud**
   - Combinación de `gcloud auth login`, `gcloud auth configure-docker` y `gcloud auth print-access-token | sudo docker login ...` para registrar credenciales con privilegios.
   - Reutilizable para cualquier VM que use `sudo docker`.
3. **Institucionalización de Bitácora Mentor**
   - Creación de `STRATEGY_BITACORA_MENTOR.md` y establecimiento del mecanismo recursivo de documentación.
   - Este archivo es la primera nota especializada generada a partir de dicha estrategia.

> **Actualización continua**: agrega nuevas entradas aquí apenas se complete un despliegue, migración, refactor, optimización o corrección relevante. Cada éxito debe incluir comandos clave, errores encontrados y pasos de validación.

## 4. Patrones de diseño orientados a prompts
- **Despliegue mentor-IA**: iniciar con resumen del objetivo, ejecutar comandos (`docker build/push`, `docker pull/run`), validar respuesta HTTP; registrar todo en tono pedagógico para que cualquier AI pueda replicarlo.
- **Gestión de credenciales**: patrón para tokens de Artifact Registry (`gcloud auth print-access-token | sudo docker login ...`). Documentar cuándo expira y cómo renovarlo.
- **Recarga de proxy**: siempre indicar que `sudo systemctl reload nginx` debe ejecutarse en la VM; registrar si se pospone.
- **Checklist de cierre**: verificar `docker ps`, revisar logs y anotar pruebas de dominio realizadas.
- **Mentoría reusable**: agrega enlaces cruzados a cualquier nueva categoría o nombre creado para trasladar el patrón a otros dominios.

## 5. Calidad y métricas (ISO/IEC 25010)
- **Eficiencia de desempeño**: p95 de respuesta, consumo de memoria/CPU y tiempo de arranque por servicio.
- **Fiabilidad**: defectos/KSLOC, MTBF/MTTR, tasa de errores 5xx tras despliegue.
- **Usabilidad (APIs)**: conformidad con OpenAPI, incidencias de clientes, resultados de walkthroughs.
- **Mantenibilidad**: deuda técnica mensual, complejidad ciclomática, cobertura de pruebas.
- **Portabilidad**: tiempo para desplegar en staging/prod y compatibilidad de contenedores.
- Documentar KPI afectados en la bitácora EBSE y consolidarlos en `AUTO_ONBOARDING_BITACORA.md`.

## 6. Gestión de riesgo IA (NIST AI RMF + ISO/IEC 22989)
- **Govern**: asignar responsables y controles en `SECURITY.md` (roles, auditorías).
- **Map**: detallar propósito, datos y supuestos del modelo en `model_card.md`.
- **Measure**: fijar métricas (precisión, sesgo, latencia de inferencia) y almacenar evidencias reproducibles.
- **Manage**: definir mitigaciones (fallback, límites), monitoreo continuo y revisiones periódicas.

## 7. Paquete de reproducibilidad (ACM Badging)
- Scripts: Makefile, `scripts/codex_bootstrap_env.sh`, auto-onboarding (`Metodologia_Prompt_Mentor/scripts/*`).
- Datos/fixtures: documentar bases SQLite, mocks y licencias (principios FAIR).
- Seeds/configuración: registrar variables en `.env.example`, parámetros de despliegue y seeds de pruebas.
- Validaciones: ejecutar `make lint`, `make test`, adjuntar logs de CI y resultados en bitácoras.

## 8. Procedimiento estándar de despliegue (backend)

### 8.1 Preparación local
1. Asegurarse de estar en la raíz del repo (`backend/`).
2. Ejecutar `docker build -t us-central1-docker.pkg.dev/<proyecto>/<repo>/<servicio>:<tag> <ruta_servicio>`.
   - Validar `Dockerfile` correcto (en `validator_employee/Dockerfile`, etc.).
3. Publicar la imagen con `docker push ...`.
4. Registrar la acción en la bitácora del servicio.

### 8.2 Verificación en Cloud Shell
1. `gcloud auth login` (si no está configurado) y `gcloud config set project <proyecto>`.
2. `docker pull ...` para comprobar acceso a Artifact Registry.
3. `gcloud compute instances list` para localizar VMs y zonas.

### 8.3 Actualización en la VM con Nginx
1. Conectar: `gcloud compute ssh <instancia> --zone=<zona>` o `ssh <usuario>@<ip>` si la llave está provisionada.
2. Verificar servicios: `sudo systemctl status docker`, `sudo systemctl status nginx`.
3. Si falta Docker: instalar `sudo apt-get install -y docker.io` y habilitarlo.
4. Autenticar Docker con privilegios: `gcloud auth print-access-token | sudo docker login -u oauth2accesstoken --password-stdin https://<region>-docker.pkg.dev`.
5. Descargar imagen: `sudo docker pull ...`.
6. Reiniciar contenedor: detener/eliminar el previo (si existe) y `sudo docker run -d --name <servicio> --restart unless-stopped -p 127.0.0.1:<puerto>:<puerto> ...`.
7. Recargar Nginx: `sudo systemctl reload nginx`.
8. Validar aplicación (curl local o dominio) y registrar resultados.

## 9. Errores comunes y soluciones documentadas
- **Docker daemon no instalado**: instalar `docker.io`, habilitar servicio y reintentar build/pull.
- **Variable `DOCKER_HOST` apuntando a TCP 2375**: limpiar con `unset DOCKER_HOST` antes de usar Docker local.
- **Permisos insuficientes en Artifact Registry**: usar el flujo híbrido de `gcloud auth` + `sudo docker login` descrito arriba.
- **Archivo `Dockerfile` no encontrado**: especificar el contexto correcto o usar `-f <ruta/Dockerfile>`.
- **Nginx fuera de la VM**: ejecutar recargas únicamente dentro de la instancia (`sudo systemctl reload nginx`).

## 10. Próximos éxitos a documentar
- Automatización del pipeline (Cloud Build / GitHub Actions) para publicar imágenes automáticamente.
- Gestión de variables de entorno sensibles con Secret Manager o `.env` cifrados.
- Estrategias de health checks y monitoreo (Stackdriver, Prometheus, etc.).
- Integración entre microservicios adicionales (por ejemplo, conexión con `server_file` para manejo de documentos).

## 11. Sincronización recursiva
- Cada vez que esta nota se actualice, registra el éxito correspondiente en `STRATEGY_BITACORA_MENTOR.md` (sección de éxitos) y enlaza desde `CODEx_NOTES.md` en "Éxitos y mejoras propagadas".
- Si surge un nuevo tipo de proyecto (frontend, móvil, scrapper, IA), replica este proceso generando `CODEx_NOTES_<tipo>.md` y ajusta el árbol de referencias.

---
Con esta guía, cualquier iteración backend puede arrancar rápidamente, manteniendo la narrativa pedagógica y asegurando que los logros se propaguen a toda la documentación de manera recursiva.
