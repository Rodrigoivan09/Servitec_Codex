# Guía Especializada: {{TYPE_TITLE}} (Bitácora Mentor)

Esta nota se genera como base para proyectos de tipo `{{TYPE_KEY}}`. Ajusta cada sección según el contexto real del repositorio y mantén la narrativa pedagógica de la Metodología Prompt Mentor.

## 1. Contexto rápido del ecosistema
- **Dominio principal**: describe el foco (ej. ecommerce, IoT, data analytics).
- **Tecnologías clave**: anota lenguajes, frameworks y servicios predominantes.
- **Dependencias externas**: APIs, proveedores, integraciones obligatorias.

## 2. Prompt operativo al iniciar sesión
> **Recordatorio**: la Metodología Prompt Mentor exige leer `STRATEGY_BITACORA_MENTOR.md`, esta nota especializada y la bitácora del servicio antes de ejecutar cualquier cambio.

Checklist inicial:
- [ ] Revisaste `CODEx_NOTES.md` para contexto global.
- [ ] Consultaste esta nota para conocer convenciones del dominio `{{TYPE_KEY}}`.
- [ ] Ubicaste los servicios relevantes y abriste su bitácora (`docs/services/<servicio>.md`).
- [ ] Preparaste secciones para registrar nuevos éxitos o patrones.
- [ ] Detectaste patrones no convencionales y, de ser necesario, definiste nombres/categorías.
- [ ] ¿Necesitas replicar la metodología en otro repositorio? Ejecuta el script de bootstrap indicando el tipo apropiado.
- [ ] Documentaste cualquier actualización en `PATRONES_REFERENCIA.md`.

## 3. Logros y aprendizajes iniciales
- Enlista configuraciones, despliegues o flujos ya probados en este dominio.
- Añade enlaces a bitácoras (`docs/services/...`) que respalden cada logro.

## 4. Patrones destacados para {{TYPE_TITLE}}
- Enumera patrones de arquitectura, integración o UI específicos del dominio.
- Indica el motivo de uso, riesgos mitigados y cómo validarlos.

## 5. Procedimientos estándar
- Documenta pasos repetibles (build, despliegue, pipelines, monitoreo) adaptados al dominio.
- Incluye herramientas necesarias y comandos clave.

## 6. Calidad y métricas (ISO/IEC 25010)
- Define los KPI relevantes (p95, defectos/KSLOC, deuda técnica, tiempo de despliegue cross-env, etc.).
- Vincula cada KPI con la bitácora EBSE y el template DSR.
- Indica cómo se recolectan datos (scripts, dashboards) y dónde se almacenan.

## 7. Gestión de riesgo IA (NIST AI RMF + ISO/IEC 22989)
- Aplica cuando el dominio usa modelos IA.
- Describe actividades Govern/Map/Measure/Manage y referencia `model_card.md`.
- Define términos clave (artefacto IA, riesgo residual, uso previsto).

## 8. Paquete de reproducibilidad (ACM Badging)
- Enumera scripts, datasets, seeds, outputs esperados y vínculos a CI.
- Señala requisitos FAIR para datos y configuraciones.

## 9. Errores comunes y soluciones
- Registra incidentes frecuentes detectados en este tipo de proyectos y cómo resolverlos.

## 10. Próximos éxitos a documentar
- Lista mejoras o automatizaciones pendientes.

## 11. Sincronización recursiva
- Cada actualización en esta nota debe reflejarse en `STRATEGY_BITACORA_MENTOR.md` (historial de versiones) y en la bitácora global.
- Mantén enlaces bidireccionales con `PATRONES_REFERENCIA.md` cuando se agreguen patrones nuevos.

---
Sustituye los marcadores `{{TYPE_TITLE}}` y `{{TYPE_KEY}}` tras generar la nota. Si el script la creó automáticamente, revisa que los nombres sean correctos y completa la información pendiente.
