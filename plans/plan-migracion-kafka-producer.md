# Plan de Migración: `infinia-kafka-producer`

Este documento describe el plan para migrar la funcionalidad de producción de mensajes de Kafka y la interfaz de alta de productos a un nuevo proyecto.

## Fase 1: Análisis y Preparación (Completada)

-   [x] **Localizar Productor Kafka:** Se identificó `ProductProducer.java` y `AdminKafkaController.java`.
-   [x] **Identificar Componentes Frontend:** Se localizó `AdminKafkaPanel.js`.
-   [x] **Analizar Dependencias:** Se revisó el `pom.xml` para identificar las dependencias necesarias.

## Fase 2: Creación del Nuevo Proyecto (`infinia-kafka-producer`)

### Backend (Completado)

-   [x] **Estructura del Proyecto:** Creado `pom.xml`, clase principal y `application.properties`.
-   [x] **Migración de Código:**
    -   [x] `ProductKafkaMessage.java` (DTO) migrado y refactorizado.
    -   [x] `ProductProducer.java` migrado y refactorizado.
    -   [x] `JwtService.java` (versión simplificada) creado.
    -   [x] `JwtAuthenticationFilter.java` (versión simplificada) creado.
    -   [x] `SecurityConfig.java` creada para proteger el endpoint.
    -   [x] `AdminKafkaController.java` migrado y refactorizado.

### Frontend (Pendiente)

-   [ ] **Estructura del Proyecto:** Crear la estructura para un proyecto de React.
-   [ ] **Migración de Componentes:** Mover `AdminKafkaPanel.js` y sus dependencias.
-   [ ] **Configuración:** Ajustar la URL del endpoint para apuntar al nuevo servicio (`http://localhost:8081`).

## Fase 3: Refactorización del Proyecto Original (`infinia-sports`) (Pendiente)

-   [ ] **Eliminar Código Backend:** Eliminar `AdminKafkaController.java`, `ProductProducer.java` y la configuración del productor.
-   [ ] **Eliminar Código Frontend:** Eliminar `AdminKafkaPanel.js` y sus rutas.
-   [ ] **Verificar Consumidor:** Asegurar que `ProductConsumer.java` sigue funcionando.

## Fase 4: Pruebas y Documentación (Pendiente)

-   [ ] **Pruebas de Integración:** Probar el flujo completo de extremo a extremo.
-   [ ] **Documentación:** Actualizar el `README.md` de ambos proyectos.
