# Servicio `test` (auto-onboarding 2025-09-25)

## Tipo detectado
- fastapi

## Archivos relevantes
- `scrapper_data/test/testFoto.py`
- `scrapper_data/test/testINE.py`

## Puertos observados
- `8002`
- `8003`

## Despliegue manual sugerido
- Revisar Dockerfile asociado (si existe) y el compose: 
- (no se detectaron definiciones docker-compose)
- Para ejecución directa, validar el comando del framework: 
- `scrapper_data/test/testFoto.py` utiliza `fastapi`
- `scrapper_data/test/testINE.py` utiliza `fastapi`

## Bitácora automática
1. [ ] Completar pasos reales de despliegue (llenar con comandos exactos).
2. [ ] Documentar dependencias externas (bases de datos, colas, secretos).
3. [ ] Registrar validaciones manuales o salud (`/docs`, ping, etc.).

## Errores comunes / TODOs
- [ ] Añadir escenarios conocidos una vez validados.

## Validaciones pendientes
- [ ] ¿Existen pruebas automatizadas? Documentar resultado de `make test` o equivalente.
- [ ] Añadir sección de endpoints (OpenAPI) cuando se revise la app.

> Plantilla generada automáticamente. Ajusta el contenido según lo aprendido en la bitácora real.
