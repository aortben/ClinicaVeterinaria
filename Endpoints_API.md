# 📋 Endpoints de la API – Clínica Veterinaria

**Versión:** 1.0  
**Última actualización:** Febrero 2026  
**Base URL:** `http://localhost:8081/api`

---

## 🔐 Reglas de acceso globales (SecurityConfig)

| Patrón | Acceso | Notas |
|---|---|---|
| `/api/auth/**` | **Público** | Sin autenticación requerida |
| `POST /api/**` | **VETERINARIO** | Solo personal médico puede crear |
| `PUT /api/**` | **VETERINARIO** | Solo personal médico puede editar |
| `DELETE /api/**` | **VETERINARIO** | Solo personal médico puede eliminar |
| `GET /api/**` | **Autenticado** | Cualquier usuario autenticado (con filtrado por rol en controladores) |

> **Nota importante:** Además de las reglas globales, muchos endpoints GET aplican filtrado adicional por rol a nivel de controlador:
> - **CLIENTE:** solo ve sus propios datos (clientes, mascotas, citas de sus mascotas, tratamientos)
> - **VETERINARIO:** ve todos los datos del sistema

---

## 1️⃣ Autenticación (`/api/auth`)

| Método | Endpoint | Acceso | Descripción | Body (ej.) |
|---|---|---|---|---|
| `POST` | `/api/auth/registro` | Público | Registra nuevo usuario (CLIENTE o VETERINARIO). Retorna JWT y datos básicos. | `{ "email": "user@clinic.com", "password": "pass123", "rol": "CLIENTE", "nombre": "Juan", "apellidos": "García", "dni": "12345678A", "telefono": "666123456", "direccion": "C/ Ejemplo 1", "numeroColegiado": null, "especialidad": null }` |
| `POST` | `/api/auth/iniciar-sesion` | Público | Login con email/contraseña. Retorna JWT válido. | `{ "email": "user@clinic.com", "password": "pass123" }` |

**Respuesta (ambos):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIs...",
  "email": "user@clinic.com",
  "rol": "CLIENTE"
}
```

---

## 2️⃣ Dashboard (`/api/dashboard`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/dashboard` | Autenticado | Devuelve estadísticas globales (total clientes, mascotas, citas, veterinarios) y últimas 5 citas registradas. |

**Respuesta (ejemplo):**
```json
{
  "stats": {
    "totalClientes": 15,
    "totalMascotas": 23,
    "totalCitas": 47,
    "totalVeterinarios": 5,
    "totalTratamientos": 89,
    "ingresosEstimados": 1450.50
  },
  "ultimasCitas": [ ... ]
}
```

---

## 3️⃣ Clientes (`/api/clientes`)

| Método | Endpoint | Acceso | Descripción | Parámetros |
|---|---|---|---|---|
| `GET` | `/api/clientes` | Autenticado* | Lista clientes paginada con búsqueda. CLIENTE solo ve su perfil. | `?page=0&size=10&sort=id&search=García` |
| `GET` | `/api/clientes/{id}` | Autenticado* | Obtiene un cliente específico. CLIENTE solo su perfil. | `id` (path) |
| `POST` | `/api/clientes` | VETERINARIO | Crea nuevo cliente. | Body: `{ "nombre": "Carlos", "apellidos": "López", "dni": "87654321B", "telefono": "666999888", "direccion": "Av. Principal 2", "email": "carlos@mail.com" }` |
| `PUT` | `/api/clientes/{id}` | VETERINARIO | Actualiza datos cliente. | `id` (path) + Body con nuevos datos |
| `DELETE` | `/api/clientes/{id}` | VETERINARIO | Elimina cliente. | `id` (path) |

---

## 4️⃣ Mascotas (`/api/mascotas`)

| Método | Endpoint | Acceso | Descripción | Parámetros |
|---|---|---|---|---|
| `GET` | `/api/mascotas` | Autenticado* | Lista mascotas paginada. CLIENTE solo ve sus mascotas. | `?page=0&size=10&sort=id&search=Firulais` |
| `GET` | `/api/mascotas/{id}` | Autenticado* | Obtiene mascota específica. | `id` (path) |
| `POST` | `/api/mascotas` | VETERINARIO | Crea mascota vinculada a cliente. | Body: `{ "nombre": "Firulais", "especie": "Perro", "raza": "Pastor Alemán", "fechaNacimiento": "2020-05-15", "peso": 28.5, "clienteId": 3 }` |
| `PUT` | `/api/mascotas/{id}` | VETERINARIO | Actualiza datos mascota. | `id` (path) + Body con nuevos datos |
| `DELETE` | `/api/mascotas/{id}` | VETERINARIO | Elimina mascota y su imagen. | `id` (path) |
| `POST` | `/api/mascotas/{id}/imagen` | VETERINARIO | Sube/reemplaza foto. Máx 5MB. | `id` (path) + `file` (multipart/form-data) |
| `GET` | `/api/mascotas/imagen/{fileName}` | Autenticado | Descarga imagen de mascota. | `fileName` (path) – nombre del archivo |

---

## 5️⃣ Citas Veterinarias (`/api/citas`)

| Método | Endpoint | Acceso | Descripción | Parámetros |
|---|---|---|---|---|
| `GET` | `/api/citas` | Autenticado* | Lista citas paginada. CLIENTE solo ve citas de sus mascotas. | `?page=0&size=10&sort=id&search=revisión` |
| `GET` | `/api/citas/{id}` | Autenticado* | Obtiene cita con tratamientos asociados. | `id` (path) |
| `POST` | `/api/citas` | VETERINARIO | Agendar cita. | Body: `{ "fechaHora": "2026-03-15T10:30:00", "motivo": "Revisión general", "diagnostico": "Sin incidencias", "estado": "PROGRAMADA", "mascotaId": 5, "veterinarioId": 2 }` |
| `PUT` | `/api/citas/{id}` | VETERINARIO | Actualiza cita (preserva tratamientos). | `id` (path) + Body con nuevos datos |
| `DELETE` | `/api/citas/{id}` | VETERINARIO | Elimina cita. | `id` (path) |

**Estados cita:** `PROGRAMADA`, `EN_CURSO`, `COMPLETADA`, `CANCELADA`

---

## 6️⃣ Tratamientos (`/api/tratamientos`)

| Método | Endpoint | Acceso | Descripción | Parámetros |
|---|---|---|---|---|
| `GET` | `/api/tratamientos` | Autenticado* | Lista tratamientos. CLIENTE solo ve los de sus mascotas. | `?page=0&size=10&sort=id&search=antibiótico` |
| `GET` | `/api/tratamientos/{id}` | Autenticado* | Obtiene tratamiento específico. | `id` (path) |
| `GET` | `/api/tratamientos/cita/{citaId}` | Autenticado* | Obtiene tratamientos de una cita. | `citaId` (path) |
| `POST` | `/api/tratamientos` | VETERINARIO | Crea tratamiento para cita. | Body: `{ "descripcion": "Inyección de antibiótico", "medicamento": "Amoxicilina 500mg", "precio": 25.50, "observaciones": "Aplicar cada 12h", "citaId": 8 }` |
| `PUT` | `/api/tratamientos/{id}` | VETERINARIO | Actualiza tratamiento. | `id` (path) + Body con nuevos datos |
| `DELETE` | `/api/tratamientos/{id}` | VETERINARIO | Elimina tratamiento. | `id` (path) |

---

## 7️⃣ Veterinarios (`/api/veterinarios`)

| Método | Endpoint | Acceso | Descripción | Parámetros |
|---|---|---|---|---|
| `GET` | `/api/veterinarios` | Autenticado | Lista veterinarios paginada. | `?page=0&size=10&sort=id&search=García` |
| `GET` | `/api/veterinarios/{id}` | Autenticado | Obtiene veterinario específico. | `id` (path) |
| `GET` | `/api/veterinarios/especialidades` | Autenticado | Obtiene catálogo de especialidades. | — |
| `POST` | `/api/veterinarios` | VETERINARIO | Crea nuevo veterinario. | Body: `{ "nombre": "Laura", "apellidos": "Martínez", "numeroColegiado": "CV-2023-001", "especialidad": "Cirugía", "email": "laura@clinic.com" }` |
| `PUT` | `/api/veterinarios/{id}` | VETERINARIO | Actualiza datos veterinario. | `id` (path) + Body con nuevos datos |
| `DELETE` | `/api/veterinarios/{id}` | VETERINARIO | Elimina veterinario. | `id` (path) |

**Especialidades disponibles:**
```
Medicina General
Cirugía
Dermatología
Traumatología
Exóticos
Cardiología
Oncología
Odontología
```

---

## 📊 Parámetros de Paginación y Búsqueda

Todos los endpoints GET que retornan listas soportan:

| Parámetro | Tipo | Default | Descripción |
|---|---|---|---|
| `page` | int | 0 | Número de página (0-indexado) |
| `size` | int | 10 | Cantidad de registros por página |
| `sort` | string | `id` | Campo para ordenar (ej: `nombre`, `fechaNacimiento`, `precio`) |
| `search` | string | null | Búsqueda de texto libre (busca en múltiples campos) |

**Ejemplo:** `GET /api/clientes?page=2&size=20&sort=apellidos&search=García`

---

## 🚨 Códigos de Respuesta HTTP

| Código | Significado |
|---|---|
| `200` | OK – Operación exitosa |
| `201` | CREATED – Recurso creado exitosamente |
| `204` | NO CONTENT – Eliminación exitosa (sin body) |
| `400` | BAD REQUEST – Datos inválidos o malformados |
| `401` | UNAUTHORIZED – No autenticado o token inválido |
| `403` | FORBIDDEN – Autenticado pero sin permisos |
| `404` | NOT FOUND – Recurso no existe |
| `500` | INTERNAL SERVER ERROR – Error del servidor |

---

## 🔑 Autenticación con JWT

**Header requerido para endpoints privados:**
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...
```

El JWT se obtiene en `/api/auth/registro` o `/api/auth/iniciar-sesion` y debe incluirse en todas las peticiones protegidas.

---

## 📝 Notas de Implementación

- **Multipart:** El endpoint `POST /api/mascotas/{id}/imagen` acepta solo archivos en `multipart/form-data` con clave `file`
- **UUID en imágenes:** Las imágenes se renombran con UUID para evitar colisiones (ej: `550e8400-e29b-41d4-a716-446655440000.jpg`)
- **Directorio de almacenamiento:** `uploads/mascotas/` (configurable en `application.properties`)
- **Persistencia:** Solo se guarda el nombre/ruta en BD, no el contenido binario
- **Filtrado por rol:** La lógica de acceso a nivel de controlador asegura que CLIENTE solo vea sus datos
