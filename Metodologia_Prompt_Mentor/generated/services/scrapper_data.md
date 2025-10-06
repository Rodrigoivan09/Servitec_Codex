# Servicio `scrapper_data` (auto-onboarding 2025-09-25)

## Tipo detectado
- fastapi, flask

## Archivos relevantes
- `scrapper_data/CFE.py`
- `scrapper_data/CFEtest.py`
- `scrapper_data/completo.py`
- `scrapper_data/consultarCURP.py`
- `scrapper_data/cropImage.py`
- `scrapper_data/curp.py`
- `scrapper_data/curp2.py`
- `scrapper_data/curpINE.py`
- `scrapper_data/curpMejorado.py`
- `scrapper_data/direccion.py`
- `scrapper_data/dom.py`
- `scrapper_data/filtro.py`
- `scrapper_data/formularios.py`
- `scrapper_data/infoPersonal.py`
- `scrapper_data/luz.py`
- `scrapper_data/luz2.py`
- `scrapper_data/mejora.py`
- `scrapper_data/noticias.py`
- `scrapper_data/recibeCURP.py`
- `scrapper_data/respaldo_scannerRTC.py`
- `scrapper_data/scanID.py`
- `scrapper_data/scann.py`
- `scrapper_data/scannDocument.py`
- `scrapper_data/scannRTC.py`
- `scrapper_data/scannRTC2.py`
- `scrapper_data/scannRTCdocument.py`
- `scrapper_data/scanner.py`
- `scrapper_data/scanner2.py`
- `scrapper_data/scannerNuevo.py`
- `scrapper_data/test.py`
- `scrapper_data/texto.py`
- `scrapper_data/texto2.py`
- `scrapper_data/texto3.py`
- `scrapper_data/texto4.py`
- `scrapper_data/texto5.py`

## Puertos observados
- `5000`
- `5500`
- `8006`
- `8007`
- `8008`
- `8009`
- `8010`
- `8013`
- `8014`
- `8016`
- `8020`
- `8021`
- `8070`
- `8090`
- `8600`
- `8601`
- `8603`

## Despliegue manual sugerido
- Revisar Dockerfile asociado (si existe) y el compose: 
- (no se detectaron definiciones docker-compose)
- Para ejecución directa, validar el comando del framework: 
- `scrapper_data/texto.py` utiliza `fastapi`
- `scrapper_data/scanID.py` utiliza `flask`
- `scrapper_data/dom.py` utiliza `fastapi`
- `scrapper_data/formularios.py` utiliza `fastapi`
- `scrapper_data/respaldo_scannerRTC.py` utiliza `flask`
- `scrapper_data/cropImage.py` utiliza `fastapi`
- `scrapper_data/recibeCURP.py` utiliza `fastapi`
- `scrapper_data/scannRTC.py` utiliza `flask`
- `scrapper_data/CFE.py` utiliza `fastapi`
- `scrapper_data/test.py` utiliza `fastapi`
- `scrapper_data/noticias.py` utiliza `fastapi`
- `scrapper_data/CFEtest.py` utiliza `fastapi`
- `scrapper_data/curpMejorado.py` utiliza `fastapi`
- `scrapper_data/filtro.py` utiliza `flask`
- `scrapper_data/infoPersonal.py` utiliza `fastapi`
- `scrapper_data/texto5.py` utiliza `fastapi`
- `scrapper_data/direccion.py` utiliza `fastapi`
- `scrapper_data/texto2.py` utiliza `fastapi`
- `scrapper_data/curp.py` utiliza `fastapi`
- `scrapper_data/scannDocument.py` utiliza `flask`
- `scrapper_data/curp2.py` utiliza `fastapi`
- `scrapper_data/luz.py` utiliza `fastapi`
- `scrapper_data/texto3.py` utiliza `fastapi`
- `scrapper_data/scannRTC2.py` utiliza `flask`
- `scrapper_data/consultarCURP.py` utiliza `fastapi`
- `scrapper_data/scanner.py` utiliza `flask`
- `scrapper_data/luz2.py` utiliza `fastapi`
- `scrapper_data/completo.py` utiliza `fastapi`
- `scrapper_data/scanner2.py` utiliza `flask`
- `scrapper_data/texto4.py` utiliza `fastapi`
- `scrapper_data/scannRTCdocument.py` utiliza `flask`
- `scrapper_data/curpINE.py` utiliza `fastapi`
- `scrapper_data/scannerNuevo.py` utiliza `flask`
- `scrapper_data/mejora.py` utiliza `fastapi`
- `scrapper_data/scann.py` utiliza `flask`

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
