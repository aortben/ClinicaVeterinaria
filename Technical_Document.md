# Documento Técnico: API Clínica Veterinaria

## 1. Introducción
Esta API REST proporciona los servicios de back-end para la gestión de una Clínica Veterinaria. Permite administrar clientes, mascotas, veterinarios, citas y tratamientos. Además, incluye un sistema de seguridad (autenticación y autorización) basado en **JWT** y control de acceso por roles.

## 2. Tecnologías Utilizadas
- **Java 21**
- **Spring Boot 3.4.1** (Web, Data JPA, Security, Validation)
- **MariaDB** (Base de datos relacional en Docker)
- **Hibernate / JPA** (Persistencia de datos)
- **JWT (JSON Web Tokens)** (Autenticación sin estado)
- **Lombok** (Reducción de código repetitivo)
- **Swagger / OpenAPI 3** (Documentación interactiva de la API)

## 3. Seguridad y Control de Acceso (Roles)
El sistema implementa seguridad basada en roles (`CLIENTE`, `VETERINARIO`) mediante `Spring Security`. Todas las peticiones, excepto las de autenticación, requieren un token JWT válido enviado en el `Authorization` header (`Bearer <token>`).

- **CLIENTE**: Puede acceder en modo lectura (GET) a los datos y agendar nuevas citas (POST `api/citas`).
- **VETERINARIO**: Tiene permisos totales (CRUD) sobre todas las entidades.

## 4. Estructura de Endpoints de la API

La API responde y recibe objetos JSON representados internamente como DTOs para evitar exponer el modelo de datos real o causar ciclos infinitos.

### Autenticación (`/api/auth`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `POST` | `/api/auth/register` | Registro de un nuevo usuario | `RegistroDTO` | Público |
| `POST` | `/api/auth/login` | Inicio de sesión (devuelve JWT) | `LoginDTO` | Público |

### Pacientes - Mascotas (`/api/mascotas`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `GET` | `/api/mascotas` | Lista mascotas (paginado y búsqueda) | `?page, size, sort, search` | Autenticados |
| `GET` | `/api/mascotas/{id}` | Detalles de una mascota | Path Variable `id` | Autenticados |
| `POST` | `/api/mascotas` | Crear nueva mascota | `MascotaDTO` | `VETERINARIO` |
| `PUT` | `/api/mascotas/{id}`| Actualizar mascota | `MascotaDTO` | `VETERINARIO` |
| `DELETE`| `/api/mascotas/{id}`| Eliminar mascota | Path Variable `id` | `VETERINARIO` |
| `POST` | `/api/mascotas/{id}/imagen` | Subir foto de la mascota | `multipart/form-data (file)` | `VETERINARIO` |
| `GET` | `/api/mascotas/imagen/{name}`| Descarga foto de la mascota | Path Variable `name` | Autenticados |

### Propietarios - Clientes (`/api/clientes`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `GET` | `/api/clientes` | Lista clientes (paginado y búsqueda) | `?page, size, sort, search` | Autenticados |
| `GET` | `/api/clientes/{id}` | Detalles de un cliente | Path Variable `id` | Autenticados |
| `POST` | `/api/clientes` | Crear nuevo cliente | `ClienteDTO` | `VETERINARIO` |
| `PUT` | `/api/clientes/{id}`| Actualizar cliente | `ClienteDTO` | `VETERINARIO` |
| `DELETE`| `/api/clientes/{id}`| Eliminar cliente | Path Variable `id` | `VETERINARIO` |

### Empleados - Veterinarios (`/api/veterinarios`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `GET` | `/api/veterinarios` | Lista veterinarios (paginado, bq) | `?page, size, sort, search` | Autenticados |
| `GET` | `/api/veterinarios/{id}`| Detalles de un vet. | Path Variable `id` | Autenticados |
| `POST` | `/api/veterinarios` | Crear nuevo veterinario | `VeterinarioDTO` | `VETERINARIO` |
| `PUT` | `/api/veterinarios/{id}`| Actualizar veterinario | `VeterinarioDTO` | `VETERINARIO` |
| `DELETE`| `/api/veterinarios/{id}`| Eliminar veterinario | Path Variable `id` | `VETERINARIO` |

### Citas Médicas (`/api/citas`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `GET` | `/api/citas` | Lista de citas | `?page, size, sort, search` | Autenticados |
| `GET` | `/api/citas/{id}` | Detalles de una cita | Path Variable `id` | Autenticados |
| `POST` | `/api/citas` | Solicitar/crear cita | `CitaVeterinariaDTO` | `CLIENTE`, `VETERINARIO` |
| `PUT` | `/api/citas/{id}` | Modificar cita | `CitaVeterinariaDTO` | `VETERINARIO` |
| `DELETE`| `/api/citas/{id}` | Eliminar cita | Path Variable `id` | `VETERINARIO` |

### Tratamientos Médicos (`/api/tratamientos`)
| Método | Endpoint | Descripción | Body / Parámetros | Rol |
|--------|----------|-------------|-------------------|-----|
| `GET` | `/api/tratamientos` | Listado global | `?page, size, sort, search` | Autenticados |
| `GET` | `/api/tratamientos/{id}` | Detalles de tratamiento | Path Variable `id` | Autenticados |
| `GET` | `/api/citas/{id}/tratamientos`| Listar trat. de una cita | Path Variable `id` | Autenticados |
| `POST` | `/api/citas/{id}/tratamientos`| Añadir trat. a cita | `TratamientoDTO` | `VETERINARIO` |
| `PUT` | `/api/tratamientos/{id}` | Actualizar trat. | `TratamientoDTO` | `VETERINARIO` |
| `DELETE`| `/api/tratamientos/{id}` | Eliminar trat. | Path Variable `id` | `VETERINARIO` |

## 5. Decisiones de Diseño
- **Migración a REST**: La aplicación evolucionó de MVC (Thymeleaf) a REST puro. Se eliminaron las vistas de servidor para separar las responsabilidades y permitir un cliente frontend (Angular, React, Vue, móvil).
- **Mapeo DTO**: Se evita enviar las Entidades JPA directamente sobre la API para evitar ciclos de referencia infinita y fuga de información sensible.
- **Paginación y Búsqueda**: Todos los endpoints `GET` de listas soportan `Pageable`, optimizando en el backend la recuperación de grandes volúmenes de datos mediante MariaDB + JPA (p.e: `LIKE %termino%`).
- **Swagger**: Para la documentación dinámica y las pruebas interactivas de la API, se incluyó `springdoc-openapi` expuesto en `/swagger-ui.html`.
