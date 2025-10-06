# Catálogo de Patrones y Arquitecturas (Metodología Prompt Mentor)

Este catálogo sirve como referencia rápida para seleccionar patrones de diseño, arquitecturas y stacks tecnológicos según el dominio del sistema. Está orientado a equipos que siguen la Metodología Prompt Mentor y debe mantenerse actualizado cuando aparezcan nuevos patrones aplicables.

## 1. Patrones de diseño clásicos (GoF)

| Categoría | Patrones | Aplicaciones típicas |
|-----------|----------|----------------------|
| Creacionales | Singleton, Factory Method, Abstract Factory, Builder, Prototype | Control de instancias, construcción flexible de objetos complejos, desacoplar creación del uso |
| Estructurales | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy | Conversión de interfaces, desacoplar abstracción/implementación, composición jerárquica, extensiones dinámicas, simplificar subsistemas |
| Comportamiento | Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor | Flujo de responsabilidades, encapsular comandos, navegación de colecciones, coordinación de objetos, snapshots, notificaciones, cambio de comportamiento dinámico |

> **Tip Prompt Mentor**: describe en la bitácora qué problema buscabas resolver y por qué el patrón elegido encaja mejor que alternativas.

## 2. Patrones arquitectónicos

- **Monolito / Monolito modular**: entrega rápida, simplicidad de despliegue; modular cuando se prevén límites futuros.
- **Microservicios**: servicios independientes alrededor de capacidades de negocio; usar cuando hay equipos especializados y necesidad de escalado separado.
- **SOA**: precursor de microservicios; útil en entornos empresariales con bus de servicios.
- **Serverless / FaaS**: funciones aisladas manejadas por el proveedor (AWS Lambda, Cloud Functions). Ideal para cargas event-driven y picos impredecibles.
- **Event-Driven / Pub/Sub**: sistemas reaccionan a eventos (Kafka, Pub/Sub). Complementar con Event Sourcing o CQRS según necesidad de auditoría.
- **CQRS + Event Sourcing**: separar escritura/lectura y derivar estado desde eventos para auditoría, escalabilidad de lectura y reconstrucción de historia.
- **Clean Architecture / Hexagonal / Ports & Adapters**: abstracciones de dominio en el centro, infraestructura periférica; favorece pruebas y reemplazo de adaptadores.
- **DDD (Domain-Driven Design)**: enfocarse en bounded contexts, lenguaje ubicuo y agregados para dominios complejos.

## 3. Patrones de integración y APIs

| Patrón | Descripción | Cuándo usar |
|--------|-------------|------------|
| REST | Recursos con HTTP estándar | Compatibilidad amplia, CRUD, exposición pública |
| GraphQL | Consultas tipadas, selección de campos | Evitar over/under fetching, clientes web/mobile personalizados |
| gRPC | RPC binario sobre HTTP/2 | Baja latencia, comunicación servicio a servicio |
| WebSockets / SSE | Comunicación bidireccional o streaming | Chat, notificaciones en tiempo real, dashboards |
| Webhooks | Notificaciones push hacia clientes externos | Integraciones con terceros, eventos asíncronos |

## 4. Patrones de UI / Frontend

- **MVC / MVP**: separación clásica en web y desktop.
- **MVVM**: Android/Kotlin, frameworks modernos (Vue, SwiftUI).
- **Flux / Redux**: manejo de estado unidireccional en React, React Native.
- **MVI**: variantes reactivas (Jetpack Compose, Angular NgRx).
- **VIPER / Clean Swift**: iOS modular, testable.

## 5. Patrones de datos y analítica

| Dominio | Patrones | Notas |
|---------|----------|-------|
| OLTP | Normalización, ACID, índices | Sistemas transaccionales (bancos, e-commerce) |
| OLAP | Data Warehouse (Kimball, Inmon), cubos, star schema | BI, reportes históricos |
| Streaming | Lambda/Kappa Architecture, stream processing (Flink, Beam) | Ingesta near real-time |
| Data Lake / Lakehouse | Almacenamiento esquemas flexibles, Delta/Apache Iceberg | ML, analytics avanzado |
| Series temporales | Downsampling, ventanas deslizantes, compresión | IoT, monitoreo |

## 6. Patrones de despliegue e infraestructura

- **CI/CD (blue/green, canary, rolling)**: despliegue seguro incremental.
- **Infraestructura como Código (IaC)**: Terraform, Pulumi, CloudFormation.
- **Contenedores + orquestación**: Docker, Kubernetes, ECS.
- **Edge + CDN**: mejorar latencia global.
- **Serverless pipelines**: Cloud Build, GitHub Actions, Cloud Run jobs.

## 7. Seguridad y cumplimiento

- **OAuth2 / OpenID Connect** para autenticación delegada.
- **mTLS / Mutual TLS** para comunicación interna segura.
- **OWASP Top 10** como checklist de riesgos web.
- **PCI-DSS, GDPR, HIPAA** según industria.
- **Zero Trust**: autenticar y autorizar cada petición.

## 8. Lenguajes y stacks frecuentes

| Lenguaje | Usos principales | Frameworks / Notas |
|----------|-----------------|--------------------|
| JavaScript / TypeScript | Front web, Node.js, herramientas | React, Next.js, Express, NestJS |
| Python | APIs, scripting, data/ML | FastAPI, Django, Flask, Airflow |
| Java / Kotlin | Backend enterprise, Android | Spring Boot, Quarkus, Micronaut |
| C# / .NET | Backend enterprise, desktop | ASP.NET Core, Azure Functions |
| Go | Servicios cloud, redes | gRPC nativo, Kubernetes tooling |
| Rust | Sistemas de alto rendimiento/seguridad | Axum, Tokio |
| PHP | CMS, comercio | Laravel, Symfony |
| Ruby | Web productivo | Rails |
| Swift | iOS | SwiftUI, Vapor |
| C/C++ | Sistemas embebidos | Qt, frameworks nativos |
| SQL | Consultas relacionales | PostgreSQL, MySQL |

## 9. Tipos de sistemas y combinaciones sugeridas

### 9.1 Comercio electrónico
- **Arquitectura**: monolito modular o microservicios + headless commerce.
- **Patrones**: DDD para dominios (catálogo, pedidos), event-driven para inventario.
- **APIs**: REST + webhooks; GraphQL para tienda front.
- **Infra**: PCI-DSS, pasarelas PSP, antifraude, lakehouse para analítica.

### 9.2 Delivery / on-demand
- Microservicios + event-driven (órdenes, tracking, pagos).
- gRPC interno, GraphQL hacia aplicaciones móviles.
- Optimización de rutas (VRP) y notificaciones push.

### 9.3 SaaS B2B
- Multi-tenant, RBAC/ABAC, versionado SemVer estricto.
- APIs backward compatible, migraciones controladas.

### 9.4 HealthTech
- Cumplimiento HIPAA/GDPR, historial clínico, telemedicina.
- Encriptación en tránsito/reposo, auditoría completa, consentimiento explícito.

### 9.5 IoT / Tiempo real
- Event-driven, pub/sub, almacenes de series temporales.
- Edge computing para baja latencia, sincronización con nube.

### 9.6 Data & Analytics
- Core OLTP + pipelines ETL/ELT a data lakehouse.
- Catálogo de datos, lineage, orquestación (Airflow, Dagster).

## 10. Cómo elegir el stack adecuado

1. **Tipo de producto**: identifica si es transaccional, contenido, IoT, analítica, etc.
2. **Escala y latencia**: picos de tráfico, disponibilidad global, tolerancia a fallos.
3. **Equipo y skills**: prioriza lenguajes y frameworks dominados por el equipo.
4. **Regulación**: PCI, GDPR, HIPAA según la industria.
5. **Integraciones**: con qué sistemas debe hablar (ERPs, PSPs, CRMs).
6. **Presupuesto / operación**: CapEx vs OpEx, quién administra la infraestructura.
7. **Observabilidad**: planifica logs, métricas, trazas desde el inicio.

## 11. Uso dentro de la Metodología Prompt Mentor

- Documenta en la bitácora del servicio el patrón elegido, sus motivaciones y los riesgos mitigados.
- Si se descubre un patrón emergente, añade una nueva sección aquí (y en `STRATEGY_BITACORA_MENTOR.md`) con fecha y contexto.
- Antes de implementar, valida si el patrón encaja con el dominio y si el equipo tiene la experiencia necesaria.
- Genera prompts que expliquen al asistente qué patrón se adoptará y qué resultados se esperan.

## 12. Recursos para profundizar

- *Pattern-Oriented Software Architecture* (Buschmann et al.).
- Martin Fowler (martinfowler.com) para arquitectura y refactorización.
- *Site Reliability Engineering* (O'Reilly) para operación y fiabilidad.
- OWASP, PCI Council, NIST para seguridad.
- DB-Engines, Stack Overflow Developer Survey, TIOBE Index para tendencias en lenguajes y bases de datos.

## 13. Actualización continua

- Cada vez que adoptes un patrón nuevo o combines arquitecturas con éxito comprobado, añade una entrada aquí indicando fecha, proyecto y enlace a la bitácora del servicio.
- Registra en el historial de `STRATEGY_BITACORA_MENTOR.md` cualquier actualización mayor de este catálogo.
- Si un patrón deja de ser recomendable, documenta la razón y sugiere alternativas.

Mantén este catálogo sincronizado con los repositorios donde se use la Metodología Prompt Mentor. Cada nueva versión debe registrarse en el historial correspondiente.
