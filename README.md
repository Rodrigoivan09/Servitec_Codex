🛠️ Proyecto Servitec
Plataforma web de gestión de servicios técnicos para el hogar, desarrollada con Spring Boot, Thymeleaf y MariaDB.

🚀 Instrucciones para ejecutar el proyecto

Opción A) Docker (recomendado)

1) Prerrequisitos
- Docker 24+ y Docker Compose 2+
- Puerto `8090` libre; `3306` si usas la DB expuesta.

2) Variables importantes (preconfiguradas en docker-compose.yml)
- `SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/ServitecDB`
- `SPRING_DATASOURCE_USERNAME=root`
- `SPRING_DATASOURCE_PASSWORD=MSqlDB`
- `JWT_SECRET=<valor>` (ya definido con un secreto de ejemplo)

3) Levantar stack (App + MariaDB)

docker compose up --build

4) Acceso
- App: http://localhost:8090
- MariaDB: localhost:3306 (root / MSqlDB)

5) Migraciones y datos semilla (Flyway)
- La app corre con el perfil `docker` y ejecuta Flyway al iniciar.
- Crea tablas + datos iniciales (admin/user/categorías/servicios/tarifas) desde `src/main/resources/db/migration/V1__init.sql`.
- Los archivos de e.firma/certificación se guardan en `./uploads` (mapeado al contenedor).

Notas Docker
- El servicio de DB se ejecuta con `--lower_case_table_names=1` para evitar problemas de mayúsculas/minúsculas entre tablas SQL y entidades JPA.
- La app usa el perfil `docker` (`SPRING_PROFILES_ACTIVE=docker`). Puedes ajustar variables en `docker-compose.yml`.
- Si cambias el puerto, ajusta el mapeo en `docker-compose.yml` y/o `SERVER_PORT`.

Reinicializar base de datos (opcional)
- Para recrear la DB desde cero: `docker compose down -v && docker compose up --build`.

Opción B) Local (sin Docker)
📦 Descargar y preparar el proyecto
Descarga el archivo .zip del proyecto Servitec.
Extrae el contenido en la carpeta de tu preferencia.
Dentro del proyecto encontrarás un archivo llamado servotecDB.txt. Este contiene el script SQL para crear y poblar la base de datos.

🛠️ Configurar la base de datos
El proyecto fue desarrollado usando MariaDB, así que se recomienda tenerlo instalado.
Crea una base de datos nueva (por ejemplo servitec), y luego ejecuta el contenido del archivo servotecDB.txt para crear las tablas y registros necesarios.

⚙️ Modificar archivo application.properties
Ubicado en: src/main/resources/application.properties
Edita las siguientes líneas para usar tu usuario y contraseña de la base de datos:

spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

También puedes adaptar esta configuración si prefieres usar otro motor de base de datos como MySQL.

▶️ Ejecutar el proyecto
Puedes correr el proyecto desde tu IDE (IntelliJ, Eclipse, etc.) o con Maven:

./mvnw spring-boot:run

Una vez iniciado, abre tu navegador en:
http://localhost:8090

👥 Usuarios disponibles

🔑 Usuario regular
Usuario: user
Contraseña: password

Al iniciar sesión con este usuario, podrás:

Navegar por categorías de servicios.

Ver técnicos disponibles con sus precios.

Agendar una cita seleccionando fecha y hora.

Simular un pago con tarjeta.

🛡️ Administrador
Usuario: admin
Contraseña: password

Este usuario tiene acceso completo al panel de administración, donde podrá:

Crear, editar o eliminar usuarios, técnicos, categorías, servicios

Corregir datos o rehacer elementos ante cualquier error.

🧪 Recomendaciones
Verifica que MariaDB esté corriendo antes de iniciar el proyecto.

Asegúrate de que el puerto 8090 esté libre en tu máquina.

Si cambias el puerto o nombre de la base de datos, actualízalo también en application.properties.

📄 Tecnologías utilizadas
Java 17

Spring Boot 3

Spring Security (con autenticación por rol y JWT)

Thymeleaf

MariaDB

HTML5 / Bootstrap

🔐 JWT (API)
- Login: `POST /auth/login` con body `{ "correo": "<email_o_tel>", "contrasena": "<pwd>" }` → setea cookie `ACCESS_TOKEN` (HttpOnly, SameSite=Lax) y retorna `{ token }`.
- Refresh: `POST /auth/refresh` lee token de `Authorization: Bearer ...` o cookie `ACCESS_TOKEN`, renueva cookie y retorna `{ token }`.
- Uso desde frontend: el filtro toma el token de la cookie automáticamente. Para peticiones fetch/AJAX, usa `credentials: 'include'` si no es misma-origen.
- Claims del token: `sub` (username), `uid` (id del usuario), `roles` (lista de roles), `username`.
- Sesión: stateless (sin sesión de servidor); CORS habilitado con credenciales.

👨‍💻 Autor
Rodrigo Olvera
Proyecto final – Módulo 10
