# Bitácora de Servicio — validator_employee (Metodología Prompt Mentor)

## 1. Contexto rápido
- Microservicio FastAPI que valida expedientes de empleados (`/validate`, `/status`).
- Despliegue estándar en contenedores (Docker/Artifact Registry) y ejecución local vía `make start SERVICE=validator_employee`.
- Dependencias externas: SQLite de catálogo (`empleados.db`), APIs de validación CURP y correo SMTP (mockeados en desarrollo).

## 2. Operación resumida
- **Local**: `make start SERVICE=validator_employee PORT=8040` → expone documentación en `http://localhost:8040/docs`.
- **Docker/CI**: `docker build -t validator_employee:dev validator_employee && docker run -p 8040:8040 validator_employee:dev`.
- **Variables clave**: `JWT_SECRET`, `SMTP_USER`, `SMTP_PASS`, `TRUSTED_ORIGINS`.

## 3. Bitácora pedagógica

### 2025-10-04 — Pipeline de seguridad automatizado
- **Contexto**: alineamos la metodología con KPI de mantenibilidad/fiabilidad; faltaba ejecutar SAST/DAST recurrente.
- **Acción**: el workflow `Prompt Mentor CI` ahora incorpora el job `security-scans` (Bandit + OWASP ZAP baseline) que sube los artefactos `bandit-report` y `zap-baseline-report` en cada ejecución.
- **Aprendizaje**: mantener el target `http://localhost:8040` operativo (via `make start` o despliegue temporal) para obtener resultados reales del baseline; siguiente paso habilitar autenticación en ZAP usando secretos de GitHub Actions.
- **Referencias**: `.github/workflows/prompt_mentor_ci.yml`, `SECURITY.md` (sección "Monitoreo preventivo y escaneos automatizados"), `Metodologia_Prompt_Mentor/CODEx_NOTES.md`.

## 4. Validaciones
- Revisar artefactos `bandit-report` y `zap-baseline-report` en cada corrida del workflow.
- Ejecutar `bandit -r validator_employee` localmente si se añaden dependencias sensibles.

## 5. Pendientes
- Configurar usuario de prueba y token para escaneo autenticado con ZAP.
- Documentar resultados de ZAP autenticado en la bitácora (añadir entradas nuevas por fecha).

## 6. Referencias cruzadas
- `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md`
- `Metodologia_Prompt_Mentor/AUTO_ONBOARDING_BITACORA.md`
- `Metodologia_Prompt_Mentor/generated/services/validator_employee.md`
- `SECURITY.md`
- `.github/workflows/prompt_mentor_ci.yml`
