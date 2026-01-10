# ChurnInsight - Predicción de Deserción de Clientes

Este proyecto es una herramienta para predecir la probabilidad de que un cliente cancele un servicio (churn). Está diseñado especialmente para empresas de telecomunicaciones, fintechs y servicios de suscripción.

## 🚀 Funcionalidades Principales (MVP)

- **Predicción Individual (JSON)**: Endpoint que recibe datos de un cliente y devuelve el riesgo de churn.
- **Predicción en Lote (CSV)**: Carga masiva de clientes para análisis rápido.
- **Dashboard Interactivo**: Interfaz web para visualizar resultados y estadísticas.
- **Explicabilidad**: Identifica los 3 factores de riesgo más importantes para cada cliente.
- **Persistencia**: Registro de todas las evaluaciones en base de datos MySQL.
- **Contenerización**: Listo para ejecutar con Docker.

## 🛠️ Requisitos Técnicos

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose (Opcional)

## 💻 Cómo Ejecutar el Proyecto

### Opción 1: Con Docker Compose (Recomendado)

```bash
docker-compose up --build
```

### Opción 2: Desarrollo Local (Maven)

1. Configura tu base de datos en `src/main/resources/application.properties`.
2. Ejecuta el comando:
```bash
./mvnw spring-boot:run
```
3. Accede al Dashboard en: `http://localhost:8080/index.html`

### 🛠️ Herramientas de Inspección (Jurados)
- **Documentación API (Swagger)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Estado del Sistema (Health)**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## 📡 Ejemplo de Uso de la API (REST)

### Endpoint POST `/predict`

**Petición (JSON):**
```json
{
  "cliente_id": 1001,
  "tiempo_contrato_meses": 12,
  "retrasos_pago": 2,
  "uso_mensual": 14.5,
  "plan": "PREMIUM",
  "soporte_tickets": 4
}
```

**Respuesta:**
```json
{
  "prevision": "Va a cancelar",
  "probabilidad": 0.85,
  "factoresRiesgo": [
    "Más de 2 meses de retraso en pagos",
    "Alto número de tickets de soporte",
    "Suscrito al plan Básico"
  ]
}
```

### Endpoint GET `/predict/stats`

**Respuesta:**
```json
{
  "total_evaluados": 150,
  "tasa_churn": 0.18
}
```

## ☁️ Despliegue en OCI (Oracle Cloud Infrastructure)

Para asegurar un funcionamiento óptimo en el **Free Tier de OCI** (recursos limitados), se recomiendan los siguientes parámetros en la ejecución de la JVM:

```bash
java -Xmx512M -Xms256M -jar target/churninsight-0.0.1-SNAPSHOT.jar
```
*Esto limita el uso de memoria para no exceder los límites de la instancia gratuita.*

## 📂 Estructura del Proyecto

- `src/main/java`: Código fuente de la API (Spring Boot).
- `src/main/resources/static`: Frontend (HTML/JS).
- `src/main/resources/db/migration`: Migraciones de base de datos (Flyway).

## 👥 Equipo
- **Data Science**: Limpieza, EDA y creación del modelo de clasificación.
- **Back-end**: Construcción de API REST, integración de modelo y Dashboard.
