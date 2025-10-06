# Paquete de Artefactos Reproducibles (Metodología Prompt Mentor)

## 1. Indicaciones generales
- Ejecuta `scripts/codex_bootstrap_env.sh` para preparar el entorno Python (`.venv`).
- Usa el Makefile como orquestador:
  - `make bootstrap`
  - `make lint`
  - `make test`
- Documenta en la bitácora EBSE la fecha, commit y resultados de cada ejecución.

## 2. Scripts principales (one-click)
| Objetivo | Comando | Output esperado |
|----------|---------|-----------------|
| Escaneo de servicios | `python Metodologia_Prompt_Mentor/scripts/auto_onboarding_scan.py` | JSON con lenguajes, puertos y Dockerfiles |
| Generación de borradores | `python Metodologia_Prompt_Mentor/scripts/auto_onboarding_generate.py` | Markdown en `Metodologia_Prompt_Mentor/generated/` |
| Aplicar snapshots | `python Metodologia_Prompt_Mentor/scripts/auto_onboarding_apply.py` | Sección `<!-- AUTO-ONBOARDING:START -->` actualizada en bitácoras |
| Arranque servicio (ejemplo) | `make start SERVICE=validator_employee PORT=8040` | API corriendo en `http://localhost:8040/docs` |

## 3. Datos y recursos (principios FAIR)
- **Ubicación**: `scrapper_data/`, `validator_employee/empleados.db` (documentar licencia y procedencia).
- **Accesibilidad**: indicar credenciales dummy o enlaces públicos.
- **Interoperabilidad**: describir formatos (CSV, JSON, SQLite) y esquemas.
- **Reusabilidad**: añadir metadata (fecha, propietario, restricciones legales).

## 4. Seeds y configuración
- Variables de entorno en `.env.example` (copiar a `.env`).
- Seeds o parámetros aleatorios: registrar en bitácoras cuando se usen.
- Versiones de dependencias: `requirements.txt`, `Makefile` y logs de CI.

## 5. Validaciones automáticas
- CI: `.github/workflows/prompt_mentor_ci.yml` (lint + test con pytest).
- Comandos locales: `make lint`, `make test`, `make typecheck` (añadir mypy cuando aplique).
- Guardar reportes en `Metodologia_Prompt_Mentor/generated/` o adjuntarlos en la bitácora EBSE.

## 6. Troubleshooting
- Problemas frecuentes se documentan en `docs/services/<servicio>.md` bajo “Erros comunes”.
- Para fallos de entorno: borrar `.venv` y re-ejecutar `scripts/codex_bootstrap_env.sh`.
- Para conflictos de dependencias: anotar versiones conflictivas y solución en `PATRONES_REFERENCIA.md`.

> Actualiza este documento cuando aparezcan nuevos scripts, datasets o procesos relevantes.
