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

## Reglas de negocio

- Un usuario puede tener **máximo 3 préstamos activos** simultáneamente.
- Solo se puede prestar un libro si tiene **copias disponibles** (`availableCopies > 0`).
- Al crear un préstamo la fecha de vencimiento se fija a **7 días**.
- Al devolver un libro el estado pasa a `RETURN` y las copias disponibles se restauran.

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

### Diagrama de componentes generales

![](https://github.com/6Sebastian6/DOSW-Library/blob/featureNorelacional/Imagenes/Componentes%20generales.png)

Este muestra una visión muy general del programa y se divide en dos componentes grandes como el Library Front que será toda la capa visual del programa y Library Core que es la que tendrá toda la lógica y tambien la base de datos.

### Diagrama de componentes especificos

![](https://github.com/6Sebastian6/DOSW-Library/blob/featureNorelacional/Imagenes/Diagrama%20de%20componentes.png)

Este diagrama muestra de una forma mas detallada las relaciones entre los componentes de cada parte del sistema como el User, Book y Loan. Cada uno tiene su Mapper, Controller, Service y Validator.

### Diagrama de clases

![](https://github.com/6Sebastian6/DOSW-Library/blob/featureNorelacional/Imagenes/Diagrama%20de%20Clases%201.png)
![](https://github.com/6Sebastian6/DOSW-Library/blob/featureNorelacional/Imagenes/Diagrama%20de%20Clases%202.png)

En este caso tenemos dos paquetes, uno que es el core que se encarga de toda la lógica, dentro de el tiene otros paquetes como model, service, validator, útil. El otro paquete que es el controller que es el que adapta la comunicación y da la API REST.

---

# Cobertura

## JaCoCo

El analisis con JaCoCo nos muestra

![](https://github.com/6Sebastian6/DOSW-Library/blob/featureNorelacional/Imagenes/Cobertura.png)
