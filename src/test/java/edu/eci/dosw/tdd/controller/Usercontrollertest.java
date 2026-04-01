package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("UserController - Pruebas funcionales")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String postUser(String name) throws Exception {
        UserDTO dto = new UserDTO(null, name);
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
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "Ana Torres"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("USR-")))
                .andExpect(jsonPath("$.name", is("Ana Torres")));
    }

    @Test
    @DisplayName("POST /api/users - nombre vacío retorna 400")
    void createUser_emptyName_returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/{id} - retorna el usuario correcto")
    void getUserById_existing_returns200() throws Exception {
        String body = postUser("Juan Pérez");
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("INVALID")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - actualiza nombre correctamente")
    void updateUser_valid_returns200() throws Exception {
        String body = postUser("Nombre Viejo");
        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "Nombre Nuevo"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nombre Nuevo")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - ID inexistente retorna 404")
    void updateUser_notFound_returns404() throws Exception {
        mockMvc.perform(put("/api/users/INVALID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "Nombre"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - elimina usuario y retorna 204")
    void deleteUser_existing_returns204() throws Exception {
        String body = postUser("Carlos López");
        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - ID inexistente retorna 404")
    void deleteUser_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/users/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/search - encuentra usuarios por nombre")
    void searchByName_match_returnsResults() throws Exception {
        postUser("Carlos García");
        postUser("Carlos López");
        postUser("María Rodríguez");

        mockMvc.perform(get("/api/users/search").param("name", "carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/users/search - sin coincidencias retorna lista vacía")
    void searchByName_noMatch_returnsEmpty() throws Exception {
        postUser("Ana Torres");
        mockMvc.perform(get("/api/users/search").param("name", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

