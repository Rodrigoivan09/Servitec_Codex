🛠️ Proyecto Servitec
Plataforma web de gestión de servicios técnicos para el hogar, desarrollada con Spring Boot, Thymeleaf y MariaDB.

🚀 Instrucciones para ejecutar el proyecto
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

👨‍💻 Autor
Rodrigo Olvera
Proyecto final – Módulo 10