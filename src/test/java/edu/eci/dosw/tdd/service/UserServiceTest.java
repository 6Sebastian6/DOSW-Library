package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserService - Pruebas unitarias")
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ---- createUser ----

    @Test
    @DisplayName("Crear usuario válido genera ID con prefijo USR-")
    void createUser_validUser_generatesId() {
        User user = new User("Juan Pérez", null);
        User result = userService.createUser(user);

        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith("USR-"));
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("Crear usuario con nombre vacío lanza excepción")
    void createUser_emptyName_throwsException() {
        User user = new User("", null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    @DisplayName("Crear usuario con nombre nulo lanza excepción")
    void createUser_nullName_throwsException() {
        User user = new User(null, null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    // ---- getAllUsers ----

    @Test
    @DisplayName("getAllUsers retorna lista vacía al inicio")
    void getAllUsers_emptyInitially() {
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    @DisplayName("getAllUsers retorna todos los usuarios creados")
    void getAllUsers_returnsAllCreated() {
        userService.createUser(new User("Ana", null));
        userService.createUser(new User("Luis", null));
        assertEquals(2, userService.getAllUsers().size());
    }

    // ---- getUserById ----

    @Test
    @DisplayName("getUserById retorna usuario existente")
    void getUserById_existingId_returnsUser() {
        User created = userService.createUser(new User("Ana", null));
        User found = userService.getUserById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getUserById lanza UserNotFoundException para ID inexistente")
    void getUserById_nonExistentId_throwsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("INVALID"));
    }

    // ---- updateUser ----

    @Test
    @DisplayName("updateUser modifica el nombre correctamente")
    void updateUser_validData_updatesName() {
        User created = userService.createUser(new User("Nombre Viejo", null));
        User update = new User("Nombre Nuevo", null);
        User updated = userService.updateUser(created.getId(), update);

        assertEquals("Nombre Nuevo", updated.getName());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    @DisplayName("updateUser con nombre vacío lanza excepción")
    void updateUser_emptyName_throwsException() {
        User created = userService.createUser(new User("Ana", null));
        User update = new User("", null);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(created.getId(), update));
    }

    @Test
    @DisplayName("updateUser con ID inexistente lanza excepción")
    void updateUser_nonExistentId_throwsException() {
        User update = new User("Nombre", null);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser("INVALID", update));
    }

    // ---- deleteUser ----

    @Test
    @DisplayName("deleteUser elimina al usuario correctamente")
    void deleteUser_existingId_removesUser() {
        User created = userService.createUser(new User("Ana", null));
        userService.deleteUser(created.getId());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(created.getId()));
    }

    @Test
    @DisplayName("deleteUser con ID inexistente lanza excepción")
    void deleteUser_nonExistentId_throwsException() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("INVALID"));
    }

    // ---- searchUsersByName ----

    @Test
    @DisplayName("searchUsersByName encuentra usuarios que contienen el término")
    void searchUsersByName_matchingName_returnsResults() {
        userService.createUser(new User("Carlos García", null));
        userService.createUser(new User("Carlos López", null));
        userService.createUser(new User("María Rodríguez", null));

        List<User> results = userService.searchUsersByName("carlos");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("searchUsersByName es insensible a mayúsculas")
    void searchUsersByName_caseInsensitive() {
        userService.createUser(new User("Ana Torres", null));
        List<User> results = userService.searchUsersByName("ANA");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("searchUsersByName sin coincidencias retorna lista vacía")
    void searchUsersByName_noMatch_returnsEmpty() {
        userService.createUser(new User("Ana Torres", null));
        List<User> results = userService.searchUsersByName("xyz");
        assertTrue(results.isEmpty());
    }
}