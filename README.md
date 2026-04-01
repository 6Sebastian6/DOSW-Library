# DOSW Library — Sistema de Gestión de Biblioteca

API REST para gestión de libros, usuarios y préstamos, desarrollada con **Spring Boot 4** usando arquitectura en capas y almacenamiento en memoria.

---

## Tecnologías

| Herramienta | Uso |
|---|---|
| Java 21 + Spring Boot 4 | Backend / API REST |
| SpringDoc OpenAPI 2.8 | Documentación Swagger |
| Lombok | Reducción de boilerplate |
| JUnit 5 + Mockito | Pruebas unitarias |
| Spring MockMvc | Pruebas funcionales |
| JaCoCo | Cobertura de pruebas |
| SonarQube | Análisis estático |

La API estará disponible en `http://localhost:8080`, para esto se usa el PostMan

---

## Endpoints disponibles

### Libros — `/api/books`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/books` | Listar todos los libros |
| GET | `/api/books/{id}` | Obtener libro por ID |
| POST | `/api/books` | Crear libro |
| PUT | `/api/books/{id}` | Actualizar libro |
| DELETE | `/api/books/{id}` | Eliminar libro |
| GET | `/api/books/search?title=` | Buscar por título |

### Usuarios — `/api/users`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| GET | `/api/users/search?name=` | Buscar por nombre |

### Préstamos — `/api/loans`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/loans` | Listar todos los préstamos |
| GET | `/api/loans/{id}` | Obtener préstamo por ID |
| POST | `/api/loans?userId=&bookId=` | Crear préstamo |
| PUT | `/api/loans/{id}/return` | Devolver libro |
| GET | `/api/loans/user/{userId}` | Préstamos activos del usuario |

---

## Estructura del proyecto

```
src/
├── main/
│   └── java/edu/eci/dosw/tdd/
│       ├── controller/
│       │   ├── dto/
│       │   │   ├── BookDTO.java
│       │   │   ├── LoanDTO.java
│       │   │   └── UserDTO.java
│       │   ├── mapper/
│       │   │   ├── BookMapper.java
│       │   │   ├── LoanMapper.java
│       │   │   └── UserMapper.java
│       │   ├── BookController.java
│       │   ├── ErrorResponse.java
│       │   ├── GlobalExceptionHandler.java
│       │   ├── LoanController.java
│       │   └── UserController.java
│       ├── core/
│       │   ├── exception/
│       │   │   ├── BookNotAvailableException.java
│       │   │   ├── LoanLimitExceededException.java
│       │   │   └── UserNotFoundException.java
│       │   ├── model/
│       │   │   ├── Book.java
│       │   │   ├── Loan.java
│       │   │   ├── Status.java
│       │   │   └── User.java
│       │   ├── service/
│       │   │   ├── BookService.java
│       │   │   ├── LoanService.java
│       │   │   └── UserService.java
│       │   ├── util/
│       │   │   ├── DateUtil.java
│       │   │   ├── IdGeneratorUtil.java
│       │   │   └── ValidationUtil.java
│       │   └── validator/
│       │       ├── BookValidator.java
│       │       ├── LoanValidator.java
│       │       └── UserValidator.java
│       └── DoswLibraryApplication.java
└── test/
    └── java/edu/eci/dosw/tdd/
        ├── BookControllerTest.java
        ├── BookServiceTest.java
        ├── LoanControllerTest.java
        ├── LoanServiceTest.java
        ├── UserControllerTest.java
        └── UserServiceTest.java
```
---
## Diagramas










