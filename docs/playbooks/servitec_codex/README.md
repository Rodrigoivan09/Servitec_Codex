# Manual Operativo Servitec Codex

## Clasificación adoptada
- Para los equipos de desarrollo es más intuitivo seguir el ciclo de vida DevOps — **Onboarding → Integración Continua → Infraestructura → Despliegue** — porque replica el orden cronológico en el que se ejecutan las tareas al levantar un proyecto nuevo.
- Cada categoría agrupa solo los artefactos y scripts necesarios para superar esa fase, evitando mezclar instrucciones de diferentes momentos.
- Las guías se escriben con narrativa Prompt Mentor e incluyen referencias directas a los archivos del repositorio (ruta + línea) para mantener trazabilidad ISO/IEC 12207/15288.

## Estructura de la carpeta
- `01-onboarding.md` — Cómo iniciar una colaboración, ejecutar el auto-onboarding y preparar bitácoras.
- `02-ci-cd.md` — Configuración de `Prompt Mentor CI`, disparo por API (n8n) y secretos de análisis de seguridad.
- `03-infrastructure.md` — Alta de la VM `rodev`, gestión de llaves SSH y prerequisitos del host (Docker/Compose).
- `04-deployment.md` — Plantilla de workflow GitHub Actions para publicar contenedores en Artifact Registry y desplegarlos en `rodev`.

## Cómo usar el manual
1. Lee `01-onboarding.md` antes de tocar código; valida que las bitácoras del servicio y `CODEx_NOTES.md` estén sincronizados.
2. Sigue `02-ci-cd.md` para asegurar que el pipeline CI se ejecute desde GitHub y n8n con los inputs correctos.
3. Configura el acceso a la infraestructura con `03-infrastructure.md`.
4. Finalmente, personaliza el despliegue usando `04-deployment.md`.

Cada documento termina con una checklist de verificación “primera ejecución” para garantizar que funcione a la primera cuando otro desarrollador replique el setup.
