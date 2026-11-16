# IronCrypt 

**IronCrypt** es una plataforma web  para la gestión segura y colaborativa de credenciales, desarrollada bajo el **Jakarta EE 10** para ser desplegada en **Eclipse GlassFish 7**.

La aplicación proporciona almacenamiento seguro de credenciales organizado en **bóvedas personales** y **bóvedas compartidas para equipos**, aplicando cifrado simétrico autenticado de alta seguridad y control de acceso basado en roles (RBAC).

---

## Stack Tecnológico

| Capa / Componente | Tecnología | Detalle de Implementación |
|---|---|---|
| **Lenguaje & Runtime** | Java 17 | JDK 17 LTS |
| **Plataforma Empresarial** | Jakarta EE 10 | APIs estándar para Servlets, EJB, JPA y CDI |
| **Servidor de Aplicaciones** | Eclipse GlassFish 7.x | Contenedor de aplicaciones compatible con Jakarta EE 10 |
| **Capa Web (MVC)** | Jakarta Servlet 6.0 + JSP + JSTL | Controladores HTTP, filtrado de sesiones y vistas responsivas |
| **Capa de Negocio** | EJB (Stateless Session Beans) | Fachadas transaccionales (`AbstractFacade`, `UsuarioFacade`, etc.) |
| **Persistencia (ORM)** | Jakarta Persistence 3.0 (JPA / EclipseLink) | Mapeo objeto-relacional con DataSource JNDI (`jdbc/ironcrypt`) |
| **Base de Datos** | PostgreSQL 14+ | Almacenamiento relacional con índices y claves foráneas en cascada |
| **Seguridad de Acceso** | BCrypt (`jBCrypt 0.4`) | Hashing seguro con salting automático para contraseñas de login |
| **Criptografía de Bóvedas** | AES-256-GCM + PBKDF2 | Cifrado simétrico autenticado (AEAD) con tag de 128 bits, derivación por passphrase (HMAC-SHA256, 65.536 iteraciones) |
| **Construcción y Dependencias** | Apache Maven | Empaquetado en formato WAR (`IronCrypt.war`) |

---

##  Modelos del Sistema (Entidades de Dominio)

El modelo de datos se compone de 5 entidades principales mapeadas mediante JPA y PostgreSQL:

```
[Usuario] 1 ──── * [MiembroEquipo] * ──── 1 [Equipo]
   1                                           1
   │                                           │
   ▼                                           ▼
[Boveda (Personal)]                        [Boveda (Equipo)]
   1                                           1
   └────────────────────┬──────────────────────┘
                        │
                        ▼ 1
                  [ItemBoveda] *
```

### 1. `Usuario` (`usuarios`)
Representa a los usuarios registrados en el sistema.
- `id` (PK): Identificador numérico único.
- `email`: Correo electrónico único (usado para iniciar sesión).
- `passwordHash`: Hash BCrypt de la contraseña del usuario.
- `nombreCompleto`: Nombre y apellido del usuario.
- `fechaCreacion`: Timestamp de registro.

### 2. `Equipo` (`equipos`)
Grupos de trabajo para compartir credenciales entre múltiples usuarios.
- `id` (PK): Identificador numérico único.
- `nombre`: Nombre descriptivo del equipo.
- `creadoPor` (FK): Referencia al usuario fundador del equipo.
- `fechaCreacion`: Timestamp de creación.

### 3. `MiembroEquipo` (`miembros_equipo`)
Entidad asociativa que vincula usuarios con equipos, definiendo su nivel de acceso.
- `id` (PK): Identificador de la membresía.
- `equipo` (FK): Equipo al que pertenece.
- `usuario` (FK): Usuario miembro.
- `rol`: Nivel de privilegios dentro del equipo (`ADMIN` o `MEMBER`).
- `fechaUnion`: Fecha en que el usuario se incorporó.

### 4. `Boveda` (`bovedas`)
Contenedor lógico de credenciales. Puede ser de dos tipos según su contexto:
- **Personal:** Asociada a un usuario (`equipo == null`). Solo accesible por su propietario.
- **De Equipo:** Vinculada a un equipo (`equipo != null`). Accesible por los miembros según su rol.
- Campos: `id`, `nombre`, `descripcion`, `usuario` (FK propietario), `equipo` (FK opcional), `fechaCreacion`.

### 5. `ItemBoveda` (`items_boveda`)
Representa cada credencial individual almacenada y cifrada dentro de una bóveda.
- `id` (PK): Identificador del registro.
- `boveda` (FK): Bóveda contenedora.
- `titulo`: Nombre identificador del ítem (ej. *"Cuenta AWS"*).
- `websiteUrl`: URL del servicio o plataforma.
- `username`: Nombre de usuario o correo de la cuenta almacenada.
- `passwordCifrada`: Contraseña cifrada en Base64 resultante de AES-256-GCM.
- `ivNonce`: Vector de inicialización de 12 bytes en Base64 (único por cada ítem).
- `salt`: Salt de 16 bytes en Base64 utilizado para la derivación de clave con PBKDF2.
- `notas`: Comentarios o notas adicionales en texto claro.
- `fechaCreacion` / `fechaActualizacion`: Marcas temporales de auditoría.

---

##  Arquitectura Criptográfica

1. **Autenticación al Sistema:** Las contraseñas de las cuentas de usuario se almacenan hasheadas con **BCrypt**, impidiendo su recuperación en caso de filtración de la base de datos.
2. **Cifrado de Credenciales:**
   - La contraseña en texto plano nunca toca la base de datos.
   - Se genera un **Salt de 16 bytes** y un **IV de 12 bytes** aleatorios por cada operación (`SecureRandom`).
   - Se deriva una clave AES de 256 bits mediante **PBKDF2WithHmacSHA256** (65.536 iteraciones) usando la clave maestra del usuario o una clave personalizada.
   - El algoritmo **AES/GCM/NoPadding** produce el texto cifrado y calcula un tag de autenticación de 128 bits para garantizar que ningún dato haya sido manipulado.
