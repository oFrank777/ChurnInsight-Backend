# 🛡️ ChurnInsight: Enterprise Backend Orchestrator

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate)

## 📖 Descripción del Proyecto
ChurnInsight es una solución integral para la retención de clientes. Este backend actúa como el **Núcleo de Orquestación** de un sistema distribuido, integrando bases de datos relacionales con microservicios de Inteligencia Artificial externos. Su arquitectura está diseñada para ser **resiliente, desacoplada y escalable**.

---

## 🧠 Arquitectura de Microservicios Híbrida
A diferencia de aplicaciones monolíticas, ChurnInsight utiliza un ecosistema distribuido:

1.  **Core Services (Java/Spring Boot):** Gestiona la lógica de negocio, persistencia de datos y seguridad.
2.  **AI Microservice (Python/Google Colab):** Ejecuta un modelo **RandomForestClassifier** que procesa variables comportamentales para predecir la probabilidad de abandono (*Churn Rate*).
3.  **Cross-Network Bridge:** Implementación de túneles dinámicos (Localtunnel) con inyección de headers (`bypass-tunnel-reminder`) para permitir comunicación servidor-a-servidor entre entornos locales y nubes públicas.

---

## ✨ Características Principales
*   **Integración de IA Externa:** Comunicación vía REST con modelos de Machine Learning alojados de forma remota.
*   **Sistema de Backup (Fallback):** Si la IA externa no está disponible (ej. caída de conexión), el Backend utiliza una lógica de respaldo local basada en un motor de reglas experto.
*   **Normalización Aggressive:** Capa de infraestructura que garantiza la integridad de los datos heredados, transformando estados inconsistentes en tipos de datos Booleanos puros.
*   **Explicabilidad Humana:** El sistema no solo entrega un número; genera reportes descriptivos sobre los factores de riesgo detectados.

---

## 🛠️ Stack Tecnológico & Decisiones Técnicas
*   **Java 17 (LTS):** Aprovechando *Records* y *Stream API* para un código más limpio y eficiente.
*   **Spring Data JPA:** Abstracción de acceso a datos para una gestión de persistencia robusta.
*   **Flyway:** Control de versiones de base de datos, asegurando que el esquema de MySQL sea consistente en todos los entornos.
*   **REST Client (RestTemplate):** Optimizado con interceptores para saltar validaciones de seguridad de túneles en tiempo real.

---

## 📡 Especificación de la API (RESTful)

### Gestión de Predicciones
*   `GET /api/predict/stats`: Retorna métricas globales (Total de clientes, Tasa de Churn agregada).
*   `POST /api/predict/{id}`: Ejecuta el flujo completo de IA:
    1.  Recupera datos del historial de cliente.
    2.  Llama al microservicio de Python por HTTP.
    3.  Aplica reglas de negocio para explicabilidad.
    4.  Persiste el resultado en el historial.

### Seguridad e Infraestructura
*   **Filtro CORS:** Configurado para aceptar peticiones desde dominios de GitHub Pages.
*   **Actuator:** Endpoints `/health` y `/metrics` habilitados para monitoreo proactivo.

---

## � Instalación y Configuración
1.  **Variables de Entorno:** Configurar `src/main/resources/application.properties`.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/churninsight
    api.ds.url=https://tu-modelo-ia.loca.lt/predict_api
    ```
2.  **Compilación y Ejecución:**
    ```bash
    ./mvnw clean spring-boot:run
    ```

---

## 👨‍� Equipo de Backend
- **Repositorio Original:** [pedro8734/churninsight-backend](https://github.com/pedro8734/churninsight-backend)
- **Branch de Integración y IA:** `Rama-Ower`
