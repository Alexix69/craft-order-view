# craft-order-view — Frontend Web

Proyecto frontend del sistema web para Mueblería Classic. Aplicación web desarrollada con Spring Boot 4.1.0 y Thymeleaf que consume la API REST del backend. Incluye el módulo público de cotización y seguimiento para clientes, el panel de administración y el tablero Kanban para artesanos.

## Tecnologías

- Java 21
- Spring Boot 4.1.0
- Thymeleaf + Thymeleaf Layout Dialect
- Bootstrap 5.3 (vía CDN — requiere conexión a internet)
- WebClient (comunicación con el backend)
- JavaScript vanilla (Kanban drag-and-drop, validación inline)

## Requisitos previos

- Java 21 instalado
- Maven (o usar el wrapper `./mvnw` incluido)
- El backend `craft-order` corriendo en `http://localhost:8080`
- Conexión a internet (Bootstrap se carga desde CDN)
- IDE recomendado: Spring Tool Suite 4 o IntelliJ IDEA

## Estructura del proyecto

```
craft-order-view/
├── src/main/java/com/classic/craftorderview/
│   ├── controller/       # Controladores MVC por módulo (publico, admin, artesano)
│   ├── service/          # Servicios ApiService que consumen el backend vía WebClient
│   ├── dto/              # DTOs de request y response
│   └── config/           # Configuración de WebClient y NavegacionAdvice
├── src/main/resources/
│   ├── templates/        # Vistas Thymeleaf organizadas por módulo
│   │   ├── publico/      # Catálogo, cotización y seguimiento del cliente
│   │   ├── admin/        # Panel de administración y Kanban admin
│   │   ├── artesano/     # Tablero Kanban del artesano
│   │   └── plantilla/    # Layout base y fragmentos reutilizables
│   ├── static/assets/    # CSS y JS personalizados
│   └── application.properties
└── README.md
```

## Configuración

El proyecto no requiere base de datos ni variables de entorno adicionales. Toda la lógica de negocio reside en el backend.

> ⚠️ **Nota importante:** El frontend requiere que el backend `craft-order` esté corriendo en `http://localhost:8080` antes de iniciar. Si el backend no está disponible, las pantallas mostrarán errores de conexión.

> ℹ️ **Puerto:** Por defecto el frontend corre en el puerto `8091`. Si deseas cambiarlo a `8081`, modifica `server.port` en `application.properties`.

## Pasos para ejecutar

> Asegúrate de que el backend `craft-order` esté corriendo antes de continuar.

### 1. Abrir el proyecto en el IDE

Importa la carpeta `craft-order-view` como proyecto Maven existente.

### 2. Ejecutar la aplicación

Desde el IDE ejecuta la clase principal `CraftorderviewApplication` o desde terminal:

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en: `http://localhost:8091`

## Módulos disponibles

| Módulo | URL | Acceso |
|---|---|---|
| Catálogo de muebles | `http://localhost:8091/catalogo` | Público |
| Solicitar cotización | `http://localhost:8091/cotizar` | Público |
| Seguimiento de pedido | `http://localhost:8091/seguimiento/{token}` | Público (con token) |
| Panel administrador | `http://localhost:8091/admin` | Requiere login |
| Tablero artesano | `http://localhost:8091/artesano/kanban` | Requiere login |
| Login | `http://localhost:8091/login` | Público |

## Puertos

| Servicio | Puerto |
|---|---|
| Frontend | 8081 (por defecto) |
| Backend API requerido | 8080 |
