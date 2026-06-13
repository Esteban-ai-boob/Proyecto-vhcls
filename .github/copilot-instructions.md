<!-- Use this file to provide workspace-specific custom instructions to Copilot. -->

# Proyecto: API REST de Gestión de Vehículos y Documentos

## Descripción
Aplicación REST API con Spring Boot para la gestión integral de vehículos y documentos asociados, con validaciones complejas y búsquedas especializadas.

## Stack Tecnológico
- **Framework**: Spring Boot 3.2.0
- **Lenguaje**: Java 17
- **Base de Datos**: MySQL
- **ORM**: JPA/Hibernate
- **Gestor de Dependencias**: Maven
- **Validaciones**: Bean Validation + Validaciones personalizadas

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/vehiclemanagement/
│   │   ├── config/          # Configuraciones de la aplicación
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data JPA
│   │   ├── service/         # Lógica de negocio
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   └── exception/       # Excepciones personalizadas
│   └── resources/
│       └── application.properties  # Configuración de la aplicación
└── test/                    # Pruebas unitarias e integración
```

## Checklist de Configuración

- [x] **Crear archivo pom.xml** con dependencias Maven
- [x] **Configurar application.properties** para MySQL
- [x] **Criar entidades JPA** (Vehículo, Documento, VehículoDocumento)
- [x] **Crear repositorios** con consultas personalizadas
- [x] **Implementar servicios** con lógica de negocio
- [x] **Crear controladores REST** con endpoints CRUD
- [x] **Agregar validaciones** en entidades
- [x] **Compilar el proyecto** y resolver errores
- [x] **Crear archivo README.md** con documentación
- [ ] **Probar endpoints** con Postman

## Reglas de Desarrollo
- Usar anotaciones de Lombok para reducir código boilerplate
- Implementar validaciones con @Valid y Bean Validation
- Usar DTOs para transferencia de datos
- Crear excepciones personalizadas para errores específicos
- Documentar endpoints con comentarios Javadoc
- Seguir convenciones de nombres: camelCase para variables, PascalCase para clases
