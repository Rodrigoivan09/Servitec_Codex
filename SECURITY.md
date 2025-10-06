# Política de Seguridad (Metodología Prompt Mentor)

## Manejo de secretos
- Nunca incluir credenciales reales en commits ni en `.env.example`.
- Utilizar gestores seguros (Secret Manager, Vault o GitHub Secrets) para despliegues.
- Rotar tokens y contraseñas sensibles al menos cada 90 días o tras un incidente.

## Reporte de vulnerabilidades
- Enviar hallazgos a `seguridad@ejemplo.com` (actualizar con el contacto real).
- Incluir pasos de reproducción, impacto esperado y servicio afectado.
- Priorizar vulnerabilidades de autenticación, exposición de datos personales y escalamiento de privilegios.

## Respuesta ante incidentes
- Registrar el incidente en la bitácora del servicio y en `CODEx_NOTES.md`.
- Realizar postmortem siguiendo la Metodología Prompt Mentor (qué pasó, causas raíz, acciones correctivas).
- Coordinar la revocación de secretos comprometidos y desplegar parches de seguridad.

### Plan operativo ante incidentes críticos
- **Responsables**: `@rodrigo` (owner principal según `CODEOWNERS`) lidera la respuesta; Codex apoya la recolección de evidencias y documentación. En caso de ausencia, escalar al suplente registrado en `CODEOWNERS`.
- **Canales**: usar el canal interno de incidentes (Slack/Teams) y correo `seguridad@ejemplo.com` para alertar a stakeholders; registrar hora de notificación.
- **Línea de tiempo recomendada**:
  1. **0–15 min (detección)**: confirmar el incidente, activar alerta, capturar logs iniciales y preservar evidencias (no reiniciar servicios sin snapshot).
  2. **15–60 min (contención)**: aislar componentes afectados (revocar credenciales vía Secret Manager, pausar endpoints vulnerables, activar reglas WAF/Firewall cuando existan).
  3. **≤ 4 h (erradicación)**: parchear la vulnerabilidad, restaurar configuraciones seguras, validar integridad con pruebas dirigidas (`make test` o suites específicas) y preparar comunicado interno.
  4. **≤ 24 h (recuperación)**: devolver el servicio a producción controlando despliegues (`make docker-redeploy` o pipeline CI) y monitorear métricas de error/latencia.
  5. **≤ 48 h (postmortem)**: documentar causa raíz, acciones correctivas y métricas afectadas en la bitácora del servicio, `CODEx_NOTES.md` y `STRATEGY_BITACORA_MENTOR.md` si implica cambios de proceso.
- **Checklist posterior al incidente**:
  - Actualizar `AGENTS.md` con lecciones y decisiones globales.
  - Registrar KPIs impactados (fiabilidad, mantenibilidad) y evidencia en la bitácora EBSE.
  - Programar revisión de accesos y rotación de llaves asociadas al incidente.

## Monitoreo preventivo y escaneos automatizados
- `Prompt Mentor CI` ejecuta el job `security-scans` en GitHub Actions con SpotBugs (SAST) sobre el código Java (`./mvnw spotbugs:spotbugs`) y el baseline de OWASP ZAP (`zap-baseline.py`) apuntando a `http://127.0.0.1:8090`.
- El workflow levanta una instancia temporal de MariaDB 10.11 y arranca la aplicación Spring Boot con el perfil `docker` para que ZAP analice el sitio en caliente sin relajar la configuración de producción.
- Los artefactos `spotbugs-report` y `zap-baseline-report` quedan disponibles en la ejecución del workflow para su revisión manual.
- Ajustar los parámetros de ZAP (reemplazos, autenticación) cuando se habiliten flujos protegidos; mantener credenciales fuera del repositorio y alimentarlas mediante secretos de Actions.
- Las credenciales del usuario QA para el escaneo autenticado se leen desde los Secrets `ZAP_QA_USER` y `ZAP_QA_PASS`; rotarlas periódicamente y limitar su alcance a privilegios mínimos necesarios para el rastreo.

## Riesgo IA (ISO/IEC 23894)
- **Roles**: designar responsable de seguridad IA, propietario del modelo y contacto legal; documentarlos en `CODEOWNERS`.
- **Matriz de riesgo**: evaluar impacto/probabilidad para casos de uso indebido, sesgos, fallas de robustez y privacidad.
- **Controles obligatorios**: revisión ética, monitoreo de desempeño, límites de uso, protección de datos y registro de decisiones.
- **Evidencias**: almacenar resultados de pruebas, logs y aprobaciones en la bitácora EBSE y `model_card.md`.
- **Ciclo de revisión**: mínimo trimestral; actualizar matriz y controles tras incidentes o cambios mayoritarios del modelo.

## Pendientes
- [x] Definir responsable de seguridad (actualizar CODEOWNERS).
- [x] Automatizar análisis estático/dinámico en CI (SAST/DAST).
- [x] Configurar escaneo autenticado en OWASP ZAP (manejo de credenciales mediante secretos).
