# Metodología Prompt Mentor

Este paquete contiene la documentación y herramientas necesarias para habilitar la Metodología Prompt Mentor en cualquier repositorio.

## Contenido

- `STRATEGY_BITACORA_MENTOR.md`: estrategia principal (versión 1.1) con la guía completa, historial de cambios y lineamientos recursivos.
- `CODEx_NOTES.md`: guía global para el proyecto, incluyendo tareas automáticas, patrones de diseño orientados a prompts y enlaces a notas especializadas.
- `CODEx_NOTES_backend.md`: nota especializada para microservicios backend FastAPI + Docker.
- `CODEx_NOTES_java.md`: nota especializada para proyectos Java (Spring Boot, Gradle/Maven, etc.).
- `PATRONES_REFERENCIA.md`: catálogo 360° de patrones de diseño, arquitecturas, stacks y criterios para elegirlos.
- `PROMPT_INICIALIZACION.md`: prompt de arranque que toda sesión debe leer; indica cómo crear/actualizar el archivo cuando se replica la metodología.
- `scripts/bootstrap_prompt_mentor.sh`: script que copia estos archivos a otra ruta y prepara la estructura mínima (`docs/services/`).

## Cómo usarlo en otro proyecto

1. **Copiar la carpeta**: corta o copia `Metodologia_Prompt_Mentor/` al repositorio destino y súbela a control de versiones.
2. **Ejecutar el bootstrap**:
   ```bash
   cd Metodologia_Prompt_Mentor
   ./scripts/bootstrap_prompt_mentor.sh .. [tipo]
   ```
   - Sustituye `..` por la ruta donde quieras que se creen los archivos (por ejemplo `../otro-proyecto`).
   - El parámetro `tipo` es opcional. Si no lo proporcionas, el script intentará autodetectar el stack (backend, frontend, scrapper, data, iac, mobile).
   - Si quieres forzar un tipo, pásalo explícitamente (ejemplos: `ecommerce`, `iot`, `analytics`).
   - El script genera la nota especializada `CODEx_NOTES_<tipo>.md` a partir del template y no sobrescribe archivos existentes.
3. **Actualizar la documentación**:
   - Edita `CODEx_NOTES.md` y las notas especializadas para reflejar los servicios reales del nuevo proyecto.
   - Crea o ajusta `docs/services/<servicio>.md` conforme trabajes en cada componente.
   - Revisa `PATRONES_REFERENCIA.md` para seleccionar patrones y arquitecturas adecuados al dominio, y agrega cualquier patrón nuevo que se adopte.
   - Si el proyecto no es backend, revisa la nota generada (`CODEx_NOTES_<tipo>.md`) y elimina la de ejemplo (`CODEx_NOTES_backend.md`) si no aplica.
4. **Seguir la estrategia**:
   - Antes de responder o modificar código en el nuevo repo, lee `STRATEGY_BITACORA_MENTOR.md`.
   - Documenta cada cambio siguiendo la bitácora por servicio, registra innovaciones y actualiza el historial de versiones cuando avance la metodología.
5. **Generar documentación automática (opcional)**:
   ```bash
   python scripts/auto_onboarding_scan.py        # vista rápida en JSON
   python scripts/auto_onboarding_generate.py    # borradores en generated/
   ```
   - Revisa los archivos en `Metodologia_Prompt_Mentor/generated/` y copia lo necesario al repositorio principal.

## Nota sobre rutas

La estrategia asume que esta carpeta vive en la raíz del repositorio. Si decides moverla, actualiza las referencias al script en `STRATEGY_BITACORA_MENTOR.md` y `CODEx_NOTES*.md` para mantener la recursividad.

## Órdenes rápidas

- Ejecutar bootstrap en el mismo repo (detección automática):
  ```bash
  cd Metodologia_Prompt_Mentor
  ./scripts/bootstrap_prompt_mentor.sh ..
  ```
- Ejecutar bootstrap hacia otra ruta con tipo custom:
  ```bash
  cd Metodologia_Prompt_Mentor
  ./scripts/bootstrap_prompt_mentor.sh /ruta/al/otro/repo data
  ```

Sigue las indicaciones de la estrategia para mantener la documentación sincronizada y versionada.
