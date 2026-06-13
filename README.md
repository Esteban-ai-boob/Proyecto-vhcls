# API REST de Gestión de Vehículos y Documentos

## 🎯 Características Principales

- ✅ Gestión completa de vehículos (CRUD)
- ✅ Documentos parametrizados asociados a vehículos
- ✅ **Carga de documentos en Base64** ← NUEVO
- ✅ **Notificaciones automáticas por correo** ← NUEVO
- ✅ Validaciones complejas con Bean Validation
- ✅ Estados de documentos (HABILITADO, VENCIDO, EN_VERIFICACION)
- ✅ Búsquedas especializadas y filtros

## Descripción General

Aplicación REST API desarrollada con Spring Boot para la gestión integral de vehículos y sus documentos asociados. Incluye validaciones complejas, búsquedas especializadas, carga de documentos en Base64 y notificaciones por correo automáticas.

## Requisitos Previos

- **Java**: 17 o superior
- **Maven**: 3.6 o superior
- **MySQL**: 5.7 o superior
- **Postman** (opcional, para pruebas de endpoints)

## Instalación y Configuración

### 1. Base de Datos

Primero, crea la base de datos MySQL:

```sql
CREATE DATABASE vehicle_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configuración de Base de Datos

Edita el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

### 3. Compilación del Proyecto

```bash
mvn clean install
```

### 4. Ejecución de la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/vehiclemanagement/
│   │   ├── VehicleManagementApplication.java      # Main class
│   │   ├── config/                                # Configuraciones
│   │   ├── entity/                                # Entidades JPA
│   │   │   ├── Vehicle.java
│   │   │   ├── Document.java
│   │   │   └── VehicleDocument.java
│   │   ├── repository/                            # Repositorios Spring Data JPA
│   │   │   ├── VehicleRepository.java
│   │   │   ├── DocumentRepository.java
│   │   │   └── VehicleDocumentRepository.java
│   │   ├── service/                               # Lógica de negocio
│   │   │   ├── VehicleService.java
│   │   │   └── DocumentService.java
│   │   ├── controller/                            # Controladores REST
│   │   │   ├── VehicleController.java
│   │   │   └── DocumentController.java
│   │   ├── dto/                                   # Data Transfer Objects
│   │   │   ├── VehicleDTO.java
│   │   │   ├── VehicleResponseDTO.java
│   │   │   ├── DocumentDTO.java
│   │   │   ├── VehicleDocumentDTO.java
│   │   │   └── VehicleDocumentResponseDTO.java
│   │   └── exception/                             # Excepciones personalizadas
│   │       ├── ResourceNotFoundException.java
│   │       ├── ValidationException.java
│   │       ├── GlobalExceptionHandler.java
│   │       └── ErrorResponse.java
│   └── resources/
│       └── application.properties                 # Configuración de la app
└── test/                                          # Tests
```

## Entidades de Base de Datos

### Vehículos (vehicles)

```
- id (PK)
- vehicle_type (AUTOMOVIL, MOTOCICLETA)
- license_plate (Unique, 6 caracteres)
- service_type (PUBLICO, PRIVADO)
- fuel_type (GASOLINA, GAS, DIESEL)
- passenger_capacity (Integer)
- color (Código hexadecimal #RRGGBB)
- model_year (Integer)
- brand (String)
- line (String)
```

**Validaciones de Placa:**
- Automóvil: 3 letras + 3 números (ej: ABC123)
- Motocicleta: 3 letras + 2 números + 1 letra (ej: ABC12D)

### Documentos (documents)

```
- id (PK)
- document_code (Unique, String)
- document_name (String)
- vehicle_type_applicability (A, M, AM)
- mandatory_flag (RA, RM, RR)
- description (String)
```

### Relación Vehículo-Documento (vehicle_documents)

```
- id (PK)
- vehicle_id (FK)
- document_id (FK)
- issuance_date (LocalDate)
- expiration_date (LocalDate)
- document_status (HABILITADO, VENCIDO, EN_VERIFICACION)
```

## Endpoints de la API

### Documentos (Gestión Paramétrica)

#### Crear Documento
```
POST /api/documents
Content-Type: application/json

{
  "documentCode": "SOAT",
  "documentName": "Seguro Obligatorio de Accidentes de Tránsito",
  "vehicleTypeApplicability": "AM",
  "mandatoryFlag": "RR",
  "description": "Documento obligatorio para todos los vehículos"
}
```

#### Obtener Todos los Documentos
```
GET /api/documents
```

#### Obtener Documento por ID
```
GET /api/documents/{id}
```

#### Obtener Documento por Código
```
GET /api/documents/code/{code}
```

#### Actualizar Documento
```
PUT /api/documents/{id}
Content-Type: application/json

{
  "documentCode": "SOAT_ACTUALIZADO",
  "documentName": "Seguro Obligatorio Actualizado",
  "vehicleTypeApplicability": "A",
  "mandatoryFlag": "RA",
  "description": "Documento actualizado"
}
```

#### Eliminar Documento
```
DELETE /api/documents/{id}
```

### Vehículos (CRUD y Búsquedas)

#### Crear Vehículo con Documentos
```
POST /api/vehicles
Content-Type: application/json

{
  "vehicle": {
    "vehicleType": "AUTOMOVIL",
    "licensePlate": "ABC123",
    "serviceType": "PRIVADO",
    "fuelType": "GASOLINA",
    "passengerCapacity": 5,
    "color": "#FF0000",
    "modelYear": 2023,
    "brand": "Toyota",
    "line": "Corolla"
  },
  "documentIds": [1, 2, 3]
}
```

#### Obtener Todos los Vehículos
```
GET /api/vehicles
```

#### Obtener Vehículo por ID
```
GET /api/vehicles/{id}
```

#### Buscar Vehículo por Placa
```
GET /api/vehicles/plate/{licensePlate}
```
Ejemplo: `GET /api/vehicles/plate/ABC123`

#### Buscar Vehículos por Tipo
```
GET /api/vehicles/type/{type}
```
Tipos: `AUTOMOVIL` o `MOTOCICLETA`

#### Buscar Vehículos por Documento
```
GET /api/vehicles/document/{documentId}
```

#### Buscar Vehículos por Estado del Documento
```
GET /api/vehicles/status/{status}
```
Estados: `HABILITADO`, `VENCIDO`, `EN_VERIFICACION`

#### Actualizar Vehículo
```
PUT /api/vehicles/{id}
Content-Type: application/json

{
  "vehicleType": "AUTOMOVIL",
  "licensePlate": "XYZ789",
  "serviceType": "PUBLICO",
  "fuelType": "DIESEL",
  "passengerCapacity": 4,
  "color": "#0000FF",
  "modelYear": 2024,
  "brand": "Honda",
  "line": "Civic"
}
```

#### Eliminar Vehículo
```
DELETE /api/vehicles/{id}
```

### Documentos de Vehículos

#### Agregar Documento a Vehículo
```
POST /api/vehicles/{vehicleId}/documents
Content-Type: application/json

{
  "documentId": 2,
  "issuanceDate": "2024-01-15",
  "expirationDate": "2025-01-15",
  "documentStatus": "HABILITADO"
}
```

#### Actualizar Documento de Vehículo
```
PUT /api/vehicles/{vehicleId}/documents/{vehicleDocumentId}
Content-Type: application/json

{
  "documentId": 2,
  "issuanceDate": "2024-01-15",
  "expirationDate": "2025-01-15",
  "documentStatus": "VENCIDO"
}
```

#### Eliminar Documento de Vehículo
```
DELETE /api/vehicles/{vehicleId}/documents/{vehicleDocumentId}
```

## Validaciones y Restricciones

### Validaciones de Vehículo
- La placa debe ser única
- La placa debe tener exactamente 6 caracteres
- Formato de placa depende del tipo de vehículo
- La capacidad de pasajeros debe ser mayor a 0
- El color debe ser un código hexadecimal válido
- El modelo debe ser un año válido (>= 1900)
- No se puede crear un vehículo sin documentos asociados

### Validaciones de Documento
- El código del documento debe ser único
- vehicleTypeApplicability debe ser: A, M o AM
- mandatoryFlag debe ser: RA, RM o RR

### Validaciones de Relación Vehículo-Documento
- La fecha de expedición debe ser anterior a la de vencimiento
- No se puede eliminar el último documento de un vehículo
- Un documento solo puede estar asociado una vez a un vehículo
- El documento debe ser aplicable al tipo de vehículo

## Convenciones de Codificación

- **Nombres**: camelCase para variables, PascalCase para clases
- **Entidades**: Anotadas con @Entity y @Table
- **Servicios**: Lógica de negocio sin conocimiento de HTTP
- **Controladores**: Solo manejan solicitudes/respuestas HTTP
- **DTOs**: Para transferencia de datos entre capas
- **Excepciones**: Personalizadas para cada caso de error

## Tecnologías Utilizadas

- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Acceso a datos
- **Hibernate**: ORM
- **MySQL**: Base de datos
- **Lombok**: Reducción de código boilerplate
- **Bean Validation**: Validación de datos
- **Maven**: Gestión de dependencias

## Ejemplo de Flujo Completo

1. **Crear documentos parametrizados:**
   ```
   POST /api/documents
   ```

2. **Crear vehículos con documentos:**
   ```
   POST /api/vehicles (incluir documentIds creados)
   ```

3. **Consultar vehículos:**
   ```
   GET /api/vehicles
   GET /api/vehicles/plate/{placa}
   GET /api/vehicles/type/AUTOMOVIL
   GET /api/vehicles/document/{documentId}
   GET /api/vehicles/status/HABILITADO
   ```

4. **Agregar documentos adicionales:**
   ```
   POST /api/vehicles/{vehicleId}/documents
   ```

5. **Actualizar documentos:**
   ```
   PUT /api/vehicles/{vehicleId}/documents/{vehicleDocumentId}
   ```

## Tratamiento de Errores

Todos los errores se devuelven en este formato:

```json
{
  "timestamp": "2024-04-08T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos",
  "path": "/api/vehicles",
  "details": {
    "licensePlate": "La placa es requerida"
  }
}
```

## Códigos de Estado HTTP

- **201 Created**: Recurso creado exitosamente
- **200 OK**: Solicitud exitosa
- **204 No Content**: Recurso eliminado exitosamente
- **400 Bad Request**: Error de validación
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

## Notas Importantes

- La base de datos se crea automáticamente en la primera ejecución (ddl-auto: update)
- Los documentos de nuevos vehículos se crean con estado "EN_VERIFICACION"
- El estado del documento se actualiza automáticamente si ha vencido
- Se pueden buscar vehículos por múltiples criterios
- No se puede eliminar un vehículo que tiene documentos asociados (se eliminan juntos por CascadeType.ALL)

---

## 🆕 Carga de Documentos en Base64 y Notificaciones por Correo

### Descripción

A partir de esta versión, la API permite:
- **Almacenar archivos en Base64**: Los documentos se guardan en formato Base64 en el campo `documentContent`
- **Notificaciones automáticas**: Cuando se carga un documento, se envía automáticamente un correo de notificación

### Configuración SMTP

Edita `src/main/resources/application.properties` para configurar el servidor de correo:

#### Opción 1: Mailtrap (Recomendado para testing)
```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=TU_USERNAME
spring.mail.password=TU_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Opción 2: Gmail
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_correo@gmail.com
spring.mail.password=contraseña_de_aplicacion
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Carga de Documento con Base64

```bash
POST /api/vehicles/{vehicleId}/documents
Content-Type: application/json

{
  "documentId": 1,
  "issuanceDate": "2024-01-15",
  "expirationDate": "2026-01-15",
  "documentStatus": "EN_VERIFICACION",
  "documentContent": "JVBERi0xLjQKJeLjz9MNCjEgMCBvYmoK...",
  "notificationEmail": "usuario@gmail.com"
}
```

**Parámetros:**
- `documentContent` (String, Opcional): Contenido del archivo en Base64
- `notificationEmail` (String, Opcional): Correo para enviar notificación

**Resultado:**
- El documento se guarda en la BD con el contenido en Base64
- Se envía un correo HTML con los detalles del documento
- Si hay error en el correo, la operación sigue siendo exitosa

### Ejemplo de Correo Recibido

```
De: Sistema de Gestión de Vehículos y Documentos
Asunto: Notificación: Documento Cargado - ABC-1234

✓ Documento Cargado Exitosamente

Detalles del Documento:
• Placa del Vehículo: ABC-1234
• Nombre del Documento: Licencia de Conducción
• Código del Documento: LIC-001
• Fecha y Hora: 2026-05-27 14:35:22
```

### Generar Base64

Para convertir un archivo a Base64:

**Linux/Mac:**
```bash
base64 documento.pdf
```

**Windows PowerShell:**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\documento.pdf"))
```

**Online:**
https://www.base64encode.org/

### Verificar Documentos en BD

```sql
SELECT vd.id, v.license_plate, d.document_name, 
       LENGTH(vd.document_content) as tamaño_bytes,
       LEFT(vd.document_content, 50) as primeros_caracteres
FROM vehicle_documents vd
JOIN vehicles v ON vd.vehicle_id = v.id
JOIN documents d ON vd.document_id = d.id
WHERE vd.document_content IS NOT NULL;
```

### Documentación Completa

Para documentación detallada, guías de prueba y ejemplos, consulta:
- **DEMO_READY.md**: Guía para presentación en vivo
- **GUIA_DOCUMENTOS_CORREO.md**: Guía técnica completa
- **REQUESTS_POSTMAN.json**: Requests listos para Postman

---

## Próximas Mejoras

- Paginación en listados
- Búsquedas avanzadas con filtros combinados
- Historiales de cambios
- Autenticación y autorización
- API Documentation con Swagger/OpenAPI
- Pruebas unitarias e integración
- Descarga de documentos (decodificar Base64)
- Validación de tipos MIME
- Límites de tamaño de archivo
