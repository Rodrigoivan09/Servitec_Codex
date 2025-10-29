# Fase 2 · Acceso seguro (SSH)

**Propósito**: garantizar que humanos y pipelines accedan a la VM con llaves persistentes, evitando errores “Permission denied (publickey)” y sesiones efímeras.

## Checklist rápido
- [ ] Par de llaves generado en la estación local
- [ ] Clave pública registrada en Compute Engine
- [ ] Inicio de sesión verificado
- [ ] Clave privada almacenada como secreto de GitHub
- [ ] Bitácora actualizada (`docs/services/servitec.md`)

## 1. Generar llave local
```bash
ssh-keygen -t ed25519 -f ~/.ssh/servitec_rodev -C "rodrigo-servitec"
```
- `~/.ssh/servitec_rodev` → clave privada.
- `~/.ssh/servitec_rodev.pub` → clave pública para GCP.

> Si ya existe una llave (por ejemplo `~/.ssh/id_rsa`), asegúrate de que la pública esté cargada en GCP y documenta el nombre del archivo.

## 2. Registrar la clave en la VM
### Opción A · Cloud Shell / CLI
```bash
gcloud compute instances add-metadata rodev \
  --zone us-central1-a \
  --project servitec-476518 \
  --metadata "ssh-keys=rodrigo:$(cat ~/.ssh/servitec_rodev.pub)"
```

### Opción B · Consola Web
1. Google Cloud → Compute Engine → VM instances → `rodev`.
2. Botón **Edit** → sección **SSH Keys** → pega el contenido de `servitec_rodev.pub`.

## 3. Probar acceso
```bash
ssh -i ~/.ssh/servitec_rodev rodrigo@35.192.59.158
```
- Debes ver el banner de Debian y el prompt `rodrigo@rodev:~$`.
- Si quieres mantener compatibilidad con `rod@`, repite los pasos con `ssh-keys=rod:...`.

## 4. Preparar secretos para CI/CD
- `SERVITEC_SSH_KEY`: contenido completo de `~/.ssh/servitec_rodev` (privada, formato PEM).
- `SERVITEC_SSH_USER`: `rodrigo`.
- `SERVITEC_VM_HOST`: `35.192.59.158`.
- (Opcional) `SERVITEC_SSH_PORT`: `22` si decides parametrizarlo.

> Sube los secretos en GitHub → Settings → Secrets and variables → Actions.

## 5. Documentación obligatoria
- Añade una entrada en `docs/services/servitec.md` con:
  - Fecha y responsable.
  - Comando `ssh-keygen` utilizado.
  - Ruta de la clave privada (para que otros sepan cuál reutilizar).
  - Validación (`ssh rodrigo@...`).
  - Enlace a este manual (Fase 2).

## 6. Troubleshooting
- **`Permission denied (publickey)`**: revisa que el usuario en `ssh-keys` coincida con el que usas en `ssh`. Vuelve a aplicar el comando `gcloud compute instances add-metadata ...`.
- **`Identity file ... not accessible`**: ejecutaste el comando desde otra máquina sin la clave; sincroniza `~/.ssh/servitec_rodev` o usa `gcloud compute ssh`.
- **`Request had insufficient authentication scopes`**: intentaste modificar metadatos desde la VM; siempre hazlo desde Cloud Shell o tu estación local con `gcloud` autenticado.
