package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("UserController - Pruebas funcionales")
class UserControllerTest {

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private UserDTO validUser(String name, String username) {
        return new UserDTO(null, name, username, "password123", Role.USER);
    }

    private String postUser(UserDTO dto) throws Exception {
        return mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("GET /api/users - lista vacía inicialmente retorna 200")
    void getAllUsers_empty_returns200() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/users - crea usuario y retorna 201 con ID generado")
    void createUser_valid_returns201() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser("Ana Torres", "anatorres"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("USR-")))
                .andExpect(jsonPath("$.name", is("Ana Torres")))
                .andExpect(jsonPath("$.username", is("anatorres")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/users - nombre vacío retorna 400")
    void createUser_emptyName_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserDTO(null, "", "usr", "pass", Role.USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - username duplicado retorna 400")
    void createUser_duplicateUsername_returns400() throws Exception {
        postUser(validUser("Ana Torres", "anatorres"));
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                validUser("Otra Ana", "anatorres"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/{id} - retorna el usuario correcto")
    void getUserById_existing_returns200() throws Exception {
        String body = postUser(validUser("Juan Pérez", "juanp"));
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("Juan Pérez")));
    }

    @Test
    @DisplayName("GET /api/users/{id} - ID inexistente retorna 404")
    void getUserById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - actualiza nombre correctamente")
    void updateUser_valid_returns200() throws Exception {
        String body = postUser(validUser("Nombre Viejo", "usr1"));
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserDTO(null, "Nombre Nuevo", "usr1", null, Role.USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nombre Nuevo")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - elimina usuario y retorna 204")
    void deleteUser_existing_returns204() throws Exception {
        String body = postUser(validUser("Carlos", "carlos1"));
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/api/users/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/users/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/search - encuentra usuarios por nombre")
    void searchByName_match_returnsResults() throws Exception {
        postUser(validUser("Carlos García", "carlosg"));
        postUser(validUser("Carlos López", "carlosl"));
        postUser(validUser("María Rodríguez", "mariar"));
        mockMvc.perform(get("/api/users/search").param("name", "carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}

