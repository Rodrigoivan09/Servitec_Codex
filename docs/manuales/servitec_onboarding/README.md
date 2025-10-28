# Manual de Onboarding Servitec — Clasificación por Fases Operativas

> **Metodología Prompt Mentor** · El objetivo es que cualquier desarrollador replique la infraestructura y despliegue de Servitec sin improvisar. Clasificamos la información en tres fases porque ese orden refleja cómo se construye un pipeline funcional: primero la infraestructura, luego el acceso seguro y, finalmente, la automatización CI/CD. Cada sección enlaza pasos verificables y su propósito específico.

## Tabla de contenido
- [Fase 1 · Infraestructura base](infraestructura.md) — Google Cloud, Artifact Registry y VM.
- [Fase 2 · Acceso seguro](acceso_ssh.md) — generación de llaves, pruebas interactivas y documentación.
- [Fase 3 · CI/CD y workflow](ci_cd.md) — secretos, jobs de GitHub Actions y validaciones finales.

## Cómo usar este manual
1. Abre cada fase en orden; no saltes fases porque los pasos posteriores dependen de los anteriores.
2. Marca los checklists incluidos en cada archivo a medida que completes comandos o configuraciones.
3. Registra en `docs/services/servitec.md` cualquier desviación o hallazgo nuevo, manteniendo la trazabilidad.
4. Si detectas un nuevo patrón replicable, propágalo también a `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md` y agrega referencias en `PATRONES_REFERENCIA.md`.

> **Nota**: Si necesitas adaptar este manual a otro servicio, duplica la carpeta `servitec_onboarding`, renómbrala y actualiza las rutas; la clasificación por fases es transversal a cualquier backend Dockerizado desplegado en VM + Artifact Registry.
