# Smart Power Monitor

## Project Overview
This is a Spring Boot based Microservices application to track home power usages and send email alerts to users on their device energy usages.
I have built this application as a part of Spring Boot learning project by following an online tutorial and have implemented & enhanced it into my own repository. I plan to add additional components and features to the application to improve the design.

The system accepts energy readings from devices, processes them asynchronously, stores time-series metrics, raises alerts when usage spikes, and exposes a unified API through an API Gateway with resilience, security, and observability built in.

## Tech stack

- **Language:** Java **26**  
- **Framework:** **Spring Boot 4.1** (domain services and gateway);  **Spring AI** (`insight-service`)  
- **Spring Cloud:** **2025.1.0** — Gateway (Server WebMVC), **Circuit Breaker** (Resilience4j)  
- **Messaging:** **Apache Kafka** (KRaft)  
- **Databases:** **MySQL 8** (relational data), **InfluxDB 2** (time-series usage)  
- **Identity (local dev):** **Keycloak**
- **Email (local dev):** **Mailpit**  
- **Observability:** **Micrometer**, **Prometheus**, **Grafana** 
- **API documentation:** **springdoc-openapi** (gateway aggregates service OpenAPI URLs) 
- **Containerization:** **Docker** & **Docker Compose**  
- **Build:** **Maven** (each service includes `mvnw`)  



## Design
<img width="2668" height="1822" alt="Smart-Power-Monitor" src="https://github.com/user-attachments/assets/d68f085a-e20f-4cdf-b3b7-a2966a289718" />


## Architecture overview

The system is a **microservices architecture** built primarily with **Spring Boot 4.1** and **Java 26**. Services are independently deployable modules; integration uses **synchronous HTTP** (client → gateway → service) and **asynchronous messaging** (Kafka) where loose coupling and scale matter.

**Patterns and capabilities:**

| Area | Approach |
|------|----------|
| **API Gateway** | Spring Cloud Gateway (Server MVC); single public HTTP façade, route aggregation, OpenAPI aggregation |
| **Service communication** | REST between gateway and backends; Kafka for ingestion → usage → alerts |
| **Resilience** | **Circuit breakers** (Resilience4j) on gateway routes with fallbacks |
| **Security** | **OAuth2 Resource Server** on the gateway; **Keycloak** for identity (dev profile in Docker Compose) |
| **Observability** | Spring Boot **Actuator**, **Micrometer**, **Prometheus** scrape targets, **Grafana** dashboards |
| **Configuration** | Per-service `application.properties` (no separate Spring Cloud Config Server in this repo) |

**High-level interaction:** Clients call the **API Gateway**. Domain services (**user**, **device**, **telemetry**, **insight**) sit behind it. **Telemetry** publishes to Kafka; **usage** consumes, writes to **InfluxDB**, and may publish **alerts**; **alert** consumes alerts and sends email (e.g. via **Mailpit** in local dev). **Insight** can provide AI-style summaries (Spring AI), routed through the gateway when enabled.

---

## Services breakdown

| Service | Port | Responsibility | Key technologies | Interactions |
|---------|------|------------------|------------------|--------------|
| **api-gateway** | `9000` | Public entry: routing, circuit breaking, JWT validation, aggregated API docs | Spring Boot 4.1, Spring Cloud Gateway (WebMVC), Resilience4j, OAuth2 Resource Server, springdoc | Proxies to user, device, ingestion, insight services; calls Keycloak JWKS |
| **user-service** | `8080` | User accounts and related persistence | Spring Boot 4.1, JPA, MySQL, Flyway, Actuator/Prometheus | MySQL; invoked via gateway |
| **device-service** | `8081` | Device registry / metadata | Spring Boot 4.1, JPA, MySQL, Actuator/Prometheus | MySQL; invoked via gateway |
| **telemetry-service** | `8082` | Accept energy readings over HTTP and publish to streaming pipeline | Spring Boot 4.1, Kafka producer, Actuator/Prometheus | Produces to Kafka (`energy-usage`); invoked via gateway or directly for tests |
| **usage-service** | `8083` | Consume usage events, time-series storage, aggregation / threshold logic | Spring Boot 4.1, Kafka consumer/producer, InfluxDB Java client, Actuator/Prometheus | Kafka ↔ InfluxDB; produces alert events for downstream consumers |
| **alert-service** | `8084` | Consume alert events, notify users (e.g. email) | Spring Boot 4.1, Kafka, JPA, Mail, MySQL, Actuator/Prometheus | Kafka consumer; SMTP (Mailpit locally); MySQL where applicable |
| **insight-service** | `8085` | Usage insights (e.g. LLM-backed explanations via Ollama) | Spring Boot 4.1, Spring AI, Ollama starter, Actuator/Prometheus | Invoked via gateway; optional external Ollama runtime |

---
## Gateway in the public network
The API Gateway sits in a public or DMZ-style network segment while core services run in a more private zone. Clients never talk to every microservice directly; they use one controlled entry point.

<img width="595" height="451" alt="Api-Gateway" src="https://github.com/user-attachments/assets/085cd46d-13ed-4165-a9d9-0ccfb42bb811" />

Figure: Public vs private network separation with the gateway as the controlled entry point.

## Observability with Prometheus and Grafana
Services expose Prometheus-compatible metrics via Actuator. Prometheus scrapes and stores series; Grafana visualizes SLO-friendly dashboards (latency, errors, JVM, circuit breaker health).

<img width="834" height="372" alt="Observability" src="https://github.com/user-attachments/assets/d881e357-79c7-45d2-a649-cd7e6f78ff1c" />



**Access points (local defaults)**

| What |  URL |
|------|--------|
| API Gateway	| http://localhost:9000 |
Grafana	| http://localhost:3000 (admin / admin) |
Prometheus	| http://localhost:9090 |
Kafka UI	| http://localhost:8070 |
Mailpit	 | http://localhost:8025 |
Keycloak |	http://localhost:8091 |
InfluxDB UI |	http://localhost:8072 |

Service-specific OpenAPI is linked from the gateway’s Swagger UI configuration (/swagger-ui.html).

## Observability

- Each Spring Boot app exposes `**/actuator/prometheus**` (enabled via dependencies and management config).
- **Prometheus** (`docker/prometheus/prometheus.yml`) defines scrape jobs for the gateway and all services on the host.
- **Grafana** loads provisioning from `docker/grafana/provisioning` and uses Prometheus as a data source.
- **Circuit breaker** state can be surfaced through Actuator health where enabled (see gateway `application.properties`).

Use Grafana for dashboards and Prometheus for ad-hoc queries and alerting rules as you extend the deployment

---

**Planned enhancements:**
 - Front-End
 - Cloud Deployment of containers
 - User Dashboards
 - Redis

**More Details and step by step instructions to follow soon**
