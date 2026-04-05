package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Pruebas unitarias")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = new UserEntity("USR-001", "Juan Pérez", "juanp", "hashed123", Role.USER);
    }

    // ---- createUser ----

    @Test
    @DisplayName("Crear usuario válido genera ID y lo guarda")
    void createUser_valid_savesAndReturns() {
        when(userRepository.existsByUsername("juanp")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User user = new User(null, "Juan Pérez", "juanp", "pass123", Role.USER);
        User result = userService.createUser(user);

        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith("USR-"));
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Crear usuario con nombre vacío lanza excepción")
    void createUser_emptyName_throwsException() {
        User user = new User(null, "", "juanp", "pass123", Role.USER);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear usuario con username vacío lanza excepción")
    void createUser_emptyUsername_throwsException() {
        User user = new User(null, "Juan", "", "pass123", Role.USER);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    @DisplayName("Crear usuario sin rol lanza excepción")
    void createUser_nullRole_throwsException() {
        User user = new User(null, "Juan", "juanp", "pass123", null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    @DisplayName("Crear usuario con password vacío lanza excepción")
    void createUser_emptyPassword_throwsException() {
        User user = new User(null, "Juan", "juanp", "", Role.USER);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    @DisplayName("Crear usuario con username duplicado lanza excepción")
    void createUser_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("juanp")).thenReturn(true);
        User user = new User(null, "Juan", "juanp", "pass123", Role.USER);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    // ---- getAllUsers ----

    @Test
    @DisplayName("getAllUsers retorna todos los usuarios del repositorio")
    void getAllUsers_returnsAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Juan Pérez", users.get(0).getName());
    }

    // ---- getUserById ----

    @Test
    @DisplayName("getUserById retorna usuario cuando existe")
    void getUserById_existing_returnsUser() {
        when(userRepository.findById("USR-001")).thenReturn(Optional.of(sampleEntity));
        User user = userService.getUserById("USR-001");
        assertEquals("USR-001", user.getId());
    }

    @Test
    @DisplayName("getUserById lanza UserNotFoundException para ID inexistente")
    void getUserById_nonExistent_throwsException() {
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("INVALID"));
    }

    // ---- updateUser ----

    @Test
    @DisplayName("updateUser modifica el nombre correctamente")
    void updateUser_valid_updatesName() {
        when(userRepository.findById("USR-001")).thenReturn(Optional.of(sampleEntity));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User update = new User(null, "Nuevo Nombre", "juanp", null, Role.USER);
        User result = userService.updateUser("USR-001", update);

        assertEquals("Nuevo Nombre", result.getName());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("updateUser con ID inexistente lanza excepción")
    void updateUser_nonExistent_throwsException() {
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());
        User update = new User(null, "Nombre", "usr", null, Role.USER);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser("INVALID", update));
    }

    // ---- deleteUser ----

    @Test
    @DisplayName("deleteUser llama a deleteById cuando el usuario existe")
    void deleteUser_existing_deletesFromRepository() {
        when(userRepository.existsById("USR-001")).thenReturn(true);
        userService.deleteUser("USR-001");
        verify(userRepository).deleteById("USR-001");
    }

    @Test
    @DisplayName("deleteUser con ID inexistente lanza excepción")
    void deleteUser_nonExistent_throwsException() {
        when(userRepository.existsById("INVALID")).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("INVALID"));
        verify(userRepository, never()).deleteById(any());
    }

    // ---- searchUsersByName ----

    @Test
    @DisplayName("searchUsersByName retorna usuarios que coinciden")
    void searchUsersByName_match_returnsResults() {
        when(userRepository.findByNameContainingIgnoreCase("juan")).thenReturn(List.of(sampleEntity));
        List<User> results = userService.searchUsersByName("juan");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("searchUsersByName sin coincidencias retorna lista vacía")
    void searchUsersByName_noMatch_returnsEmpty() {
        when(userRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());
        assertTrue(userService.searchUsersByName("xyz").isEmpty());
    }
}