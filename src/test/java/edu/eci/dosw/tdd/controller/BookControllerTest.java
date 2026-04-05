package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.BookDTO;
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
@DisplayName("BookController - Pruebas funcionales")
class BookControllerTest {

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private BookDTO validBook() {
        return new BookDTO(null, "Clean Code", "Robert C. Martin", 3, 0);
    }

    private String postBook(BookDTO dto) throws Exception {
        return mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("GET /api/books - lista vacía inicialmente retorna 200")
    void getAllBooks_empty_returns200() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/books - crea libro y retorna 201 con ID generado")
    void createBook_valid_returns201() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBook())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("BOK-")))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.availableCopies", is(3)));
    }

    @Test
    @DisplayName("POST /api/books - título vacío retorna 400")
    void createBook_emptyTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "", "Autor", 1, 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/books/{id} - retorna el libro correcto")
    void getBookById_existing_returns200() throws Exception {
        String body = postBook(validBook());
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)));
    }

    @Test
    @DisplayName("GET /api/books/{id} - ID inexistente retorna 404")
    void getBookById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/books/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("INVALID")));
    }

    @Test
    @DisplayName("PUT /api/books/{id} - actualiza libro existente")
    void updateBook_valid_returns200() throws Exception {
        String body = postBook(validBook());
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookDTO(null, "Título Actualizado", "Nuevo Autor", 5, 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Título Actualizado")));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - elimina libro y retorna 204")
    void deleteBook_existing_returns204() throws Exception {
        String body = postBook(validBook());
        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/api/books/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - ID inexistente retorna 404")
    void deleteBook_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/books/INVALID")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/books/search - encuentra libros por título")
    void searchByTitle_match_returnsResults() throws Exception {
        postBook(validBook());
        postBook(new BookDTO(null, "The Clean Coder", "Martin", 1, 0));
        postBook(new BookDTO(null, "Refactoring", "Fowler", 2, 0));
        mockMvc.perform(get("/api/books/search").param("title", "clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}