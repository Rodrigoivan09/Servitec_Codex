# Catálogo de Lenguajes (Auto-Onboarding)

## Java / Spring Boot
- Proyecto activo: **Servitec**.
- Documentación clave:
  - `docs/services/servitec.md` (bitácora pedagógica por sesión).
  - `Metodologia_Prompt_Mentor/CODEx_NOTES_java.md` (guía de stack, checklist y errores frecuentes).
  - `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md` (estrategia global).
- Tooling disponible:
  - Maven Wrapper (`./mvnw`).
  - JDK portátil en `vendor/amazon-corretto-21.0.8.9.1-linux-x64/`.
  - Docker Compose para MariaDB (`docker compose up db`).

## Python / FastAPI (ecosistema LiberSalus)
- Microservicios principales: `validator_employee`, `auth_user`, `server_file`, `server_email`, `server_data`, `server_catalog`, scrappers (`scrapper_curp2`, `scrapper_scannrtc`), `server_ia`.
- Documentación clave:
  - `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md` (guía especializada backend FastAPI + Docker).
  - Bitácoras generadas automáticamente en `Metodologia_Prompt_Mentor/generated/services/` (una por servicio).
  - `Metodologia_Prompt_Mentor/AUTO_ONBOARDING_BITACORA.md` (historial del flujo de auto-onboarding).
- Tooling disponible:
  - Scripts de bootstrap en `Metodologia_Prompt_Mentor/scripts/`.
  - Makefile y workflows documentados en el repo original `libersalus/backend`.

## Cómo usar este catálogo
1. Antes de iniciar una sesión, identifica el lenguaje/stack con esta tabla y lee la nota especializada correspondiente.
2. Replica las bitácoras específicas (`docs/services/<servicio>.md`) siguiendo el formato pedagógico.
3. Si aparece un nuevo stack, crea `CODEx_NOTES_<tipo>.md`, añade una entrada en este archivo y enlázala desde `Metodologia_Prompt_Mentor/CODEx_NOTES.md`.
4. Mantén sincronizados los tres niveles: estrategia global → nota especializada → bitácora del servicio.

