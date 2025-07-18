### **Plan de Implementación: Generación de Imágenes con IA (Stability.ai)**

Este plan detalla los pasos para integrar un sistema de generación de imágenes basado en la descripción de un producto, utilizando la API de Stability.ai.

**Fase 1: Backend - Creación del Servicio de Generación de Imágenes**

1.  **Añadir Dependencias**:
    *   **Acción**: Proponer la modificación del archivo `pom.xml` para añadir las dependencias necesarias. Se usará un cliente HTTP como `OkHttp` para comunicarse con la API de Stability.ai.
    *   **Verificación**: El usuario revisará y aprobará los cambios en el `pom.xml`.

2.  **Configurar API Key**:
    *   **Acción**: Proponer añadir la propiedad `stability.api.key` al archivo `application.properties`. Se recordará al usuario que use un placeholder y gestione la clave real como un secreto.
    *   **Verificación**: El usuario revisará y aprobará los cambios en `application.properties`.

3.  **Crear Endpoint y Lógica de Servicio**:
    *   **Acción**: Proponer la creación de las siguientes clases Java:
        *   `AiController`: Con un endpoint `POST /api/ai/generate-image`.
        *   `ImageGenerationService`: Con la lógica para llamar a la API externa.
        *   DTOs (`ImageRequest`, `ImageResponse`): Para manejar los datos de entrada y salida.
    *   **Verificación**: El usuario revisará y aprobará cada una de las nuevas clases.

4.  **Proteger el Endpoint**:
    *   **Acción**: Proponer la modificación de `SecurityConfig.java` para asegurar que el nuevo endpoint solo sea accesible para usuarios con el rol `ADMIN`.
    *   **Verificación**: El usuario revisará y aprobará los cambios en la configuración de seguridad.

**Fase 2: Frontend - Integración en la Interfaz de Administrador**

5.  **Crear Servicio de API en Frontend**:
    *   **Acción**: Proponer la creación de una nueva función en un archivo de servicios de API (ej. `api.js`) para llamar al nuevo endpoint del backend.
    *   **Verificación**: El usuario revisará y aprobará el nuevo código del servicio.

6.  **Modificar Componente del Formulario**:
    *   **Acción**: Proponer la modificación del componente de React que contiene el formulario de alta de producto (`AdminPanel.js` o similar). Se añadirá:
        *   Un nuevo campo de texto para la descripción (o se usará el existente en el JSON).
        *   Un botón "Generar Imagen con IA".
        *   Un estado para almacenar la URL de la imagen generada y un visor para mostrarla.
    *   **Verificación**: El usuario revisará y aprobará los cambios en el componente de React.

7.  **Actualizar Lógica del Componente**:
    *   **Acción**: Proponer la lógica para que, al hacer clic en el nuevo botón, se llame al servicio de API, se reciba la URL de la imagen y se actualice automáticamente el campo `imageUrl` en el `textarea` del JSON.
    *   **Verificación**: El usuario revisará y aprobará la lógica añadida al componente.

**Fase 3: Migración Futura a Google Cloud (Opcional)**

8.  **Cambiar Proveedor de IA**:
    *   **Contexto**: Este paso se ejecutará en el futuro, una vez que se disponga de acceso para crear y gestionar proyectos en Google Cloud Platform.
    *   **Acción**:
        *   Añadir las dependencias del SDK de Google Cloud para Java en `pom.xml`.
        *   Crear una nueva implementación del servicio de generación de imágenes (`GoogleImageGenerationService`) que utilice la API de Imagen de Google.
        *   Actualizar la configuración para inyectar la nueva implementación del servicio en el controlador.
    *   **Verificación**: El usuario revisará y aprobará todos los cambios de código relacionados con la integración de Google Cloud.
