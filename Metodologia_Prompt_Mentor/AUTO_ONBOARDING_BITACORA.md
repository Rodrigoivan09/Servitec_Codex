# Bitácora de Auto-Onboarding (Metodología Prompt Mentor)

Esta bitácora registra cada paso, error y éxito durante la construcción del flujo de auto-onboarding para la Metodología Prompt Mentor.

## 2025-09-22 — Preparación inicial
- Se consolidó la carpeta `Metodologia_Prompt_Mentor/` con la estrategia, notas globales y el script `bootstrap_prompt_mentor.sh`.
- Se añadió autodetección básica de tipo de proyecto en el script (backend, frontend, scrapper, data, iac, mobile) y la generación dinámica de `CODEx_NOTES_<tipo>.md` a partir de un template.
- Se creó el catálogo `PATRONES_REFERENCIA.md` con patrones y arquitecturas de referencia, incorporando la sección de actualización continua.
- Se actualizaron `STRATEGY_BITACORA_MENTOR.md`, `CODEx_NOTES.md`, `CODEx_NOTES_backend.md` y `README.md` para documentar el flujo de bootstrap y la obligación de mantener el catálogo al día.

## 2025-09-23 — Inicio del auto-onboarding automatizado
- Se definió el plan en cinco fases: (1) detección de metadata, (2) generación de documentación, (3) estándares operativos, (4) integración con la estrategia y (5) validación.
- Se acordó documentar cada iteración en esta bitácora y en las notas correspondientes.
- Se implementó el script `scripts/auto_onboarding_scan.py` que detecta lenguajes, frameworks, servicios y puertos (incluye lectura de docker-compose cuando PyYAML está disponible).
- Se ejecutó el escáner y se obtuvo el inventario de servicios (FastAPI, Flask, compose) junto a los puertos expuestos y Dockerfiles existentes.
- Se guardó el resultado en `Metodologia_Prompt_Mentor/scan_report.json` para alimentar la generación automática de documentación.
- Se creó el generador `scripts/auto_onboarding_generate.py`, que produce borradores de documentación en `Metodologia_Prompt_Mentor/generated/`.
- Se desarrolló `scripts/auto_onboarding_apply.py` para insertar snapshots auto-generados en las bitácoras existentes (`docs/services/*.md`) sin sobrescribir su contenido principal.
- Las bitácoras reales ahora incluyen la sección "Auto-onboarding snapshot" con enlaces a la fuente generada.
- Se añadieron los estándares operativos base: `AGENTS.md`, `Makefile`, `scripts/codex_bootstrap_env.sh`, `.env.example`, `docs/services/_template.md`, `.github/workflows/prompt_mentor_ci.yml`, `SECURITY.md`, `CODEOWNERS`, `ARTIFACTS.md` y `model_card.md`.
- Se integró el marco científico (DSR/EBSE) y la trazabilidad a ISO/IEC 12207, 15288, 25010, 22989 y 23894 en la estrategia y notas especializadas.
- Se publicaron nuevas imágenes en Artifact Registry para `validator_employee`, `auth_user`, `server_email`, `server_file`, `scrapper_curp2` y `scrapper_scannrtc`, y se actualizaron los contenedores en la VM `liber-salus` (puertos locales 8040, 8060, 8030, 8020, 8008 y 8600). El despliegue de `validator_employee` requirió liberar el puerto 8040 (contenedores `validator` y `validator_employee` previos).
- Se creó el workflow `.github/workflows/deploy-validator.yml`, que automatiza el ciclo build → push → deploy del servicio `validator_employee` usando GitHub Actions, Artifact Registry y despliegue vía SSH en `liber-salus`.
- Pendiente completar la actualización y documentación de los servicios restantes (`server_catalog`, `server_data`, `server_cat_gobi_salud`, `server_cat_nom024`, `server_ia`) y registrar los KPI de disponibilidad tras las pruebas.
- Pendiente inmediato: evaluar qué partes del material generado deben migrarse a los documentos oficiales, automatizar validaciones (`make lint/test`) y registrar métricas KPI en la bitácora EBSE.

> Próximos pasos visibles: integrar los borradores generados en la documentación oficial, ejecutar validaciones (`make lint/test`) y monitorear los KPI definidos (ISO/IEC 25010).

## 2025-09-24 — Endure del pipeline de despliegue
- Se revocó la llave JSON expuesta de `codex-472522` y se generó una nueva clave de servicio para Artifact Registry.
- Se registraron los secretos `GCP_SA_KEY`, `LIBERSALUS_SSH_USER` y `LIBERSALUS_SSH_KEY` en GitHub Actions, asegurando autenticación automática durante los despliegues.
- Se creó una llave SSH ed25519 exclusiva para CI, se añadió al `authorized_keys` de `rodrigo@liber-salus` y se documentaron los comandos de hardening (`chmod 700 ~/.ssh`, `chmod 600 ~/.ssh/authorized_keys`).
- Se validó la conexión con la llave nueva (`ssh -i ~/.ssh/libersalus_deploy rodrigo@34.10.64.56`) y se probó manualmente que el contenedor `validator_employee` responde tras reinicio.
- Quedó formalizado el procedimiento de rotación segura de credenciales en `STRATEGY_BITACORA_MENTOR.md`, `CODEx_NOTES.md` y `docs/services/validator_employee.md`.
- Próximo objetivo: ejecutar un push de prueba para observar el workflow `deploy-validator` y registrar métricas de despliegue (tiempo de build, pull, restart) en la siguiente iteración.
- Se migró el acceso automatizado al usuario `Timoris`, reutilizando la llave `libersalus_ci`, copiándola con `gcloud compute scp` y actualizando los secretos de GitHub.
