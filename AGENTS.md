# Guía de Colaboración Codex ↔ Equipo (Metodología Prompt Mentor)

## Principios generales
- Siempre leer `Metodologia_Prompt_Mentor/STRATEGY_BITACORA_MENTOR.md` antes de contribuir.
- Responder con narrativa pedagógica: explica el porqué de cada cambio y referencia archivos con ruta + línea.
- Toda decisión relevante debe registrarse en la bitácora del servicio (`docs/services/<servicio>.md`) y, si aplica, en `CODEx_NOTES.md`.

## Convenciones de nombres y estructura
- Ramas git: `feature/<descripcion-corta>` o `fix/<incidencia>`. Usar minúsculas y guiones.
- Commits: prefijo con el servicio o carpeta principal (`validator_employee:`, `server_file:`) seguido de resumen imperativo.
- Directorios nuevos deben documentarse en `CODEx_NOTES.md`.

## Flujo de trabajo sugerido
1. Ejecutar los scripts de auto-onboarding si hay cambios estructurales:
   ```bash
   python Metodologia_Prompt_Mentor/scripts/auto_onboarding_scan.py
   python Metodologia_Prompt_Mentor/scripts/auto_onboarding_generate.py
   ```
2. Actualizar la bitácora del servicio con los pasos realizados y errores encontrados.
3. Completar el checklist de validaciones (`make lint`, `make test`) y anotar resultados.
4. Si se descubren patrones nuevos, agregarlos a `PATRONES_REFERENCIA.md` con fecha y contexto.

## Trazabilidad a procesos ISO/IEC/IEEE 12207 / 15288
| Actividad Prompt Mentor | ISO/IEC/IEEE 12207 | ISO/IEC/IEEE 15288 |
|------------------------|--------------------|--------------------|
| Descubrimiento y análisis (plan + bitácora inicial) | Technical Processes – Stakeholder Requirements | Technical Processes – Stakeholder Requirements |
| Diseño/Implementación (cambios de código, scripts) | Technical Processes – Implementation | Technical Processes – Design Definition |
| Evaluación (lint, pruebas, revisión) | Technical Processes – Verification & Validation | Technical Processes – Verification & Validation |
| Documentación y bitácoras | Management Processes – Information Management | Management Processes – Information Management |
| Despliegue / Operación | Technical Processes – Transition & Operation | Technical Processes – Transition & Operation |

## Política de métricas (ISO/IEC 25010)
- **Eficiencia de desempeño** → KPI: tiempo promedio/p95 de respuesta, consumo de memoria/CPU.
- **Fiabilidad** → KPI: defectos/KSLOC, MTBF vs. MTTR por servicio.
- **Usabilidad** → KPI: SUS/NPS, tasa de tareas completadas en walkthroughs.
- **Mantenibilidad** → KPI: deuda técnica/mes, complejidad ciclomática promedio, cobertura de pruebas.
- **Portabilidad** → KPI: tiempo de despliegue cross-env, incidencias de compatibilidad.
- Cada iteración debe registrar qué KPI impacta, incluir evidencia en la bitácora EBSE y actualizar `CODEx_NOTES.md` cuando cambien objetivos de calidad.

## IA + Humanos
- Codex no debe sobrescribir archivos críticos sin respaldo; si es necesario, generar `.bak` y notificar en la bitácora.
- Las decisiones de diseño se discuten en la bitácora global; si hay incertidumbre, plantear preguntas antes de ejecutar.
- Validar que cada auto-generación se revise manualmente antes de fusionarla a la documentación oficial.

## Plantillas de commits
```
<servicio|tool>: <resumen imperativo>

- <detalle 1>
- <detalle 2>

Refs: <ticket|bitácora>
```

## TODO
- [x] Documentar propietarios por carpeta en `CODEOWNERS`.
- [x] Definir respuesta ante incidentes críticos (añadir a `SECURITY.md`).
