package edu.eci.dosw.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("LoanController - Pruebas funcionales")
class LoanControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String userId;
    private String bookId;

    @BeforeEach
    void setUp() throws Exception {
        String userBody = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "Juan Pérez"))))
                .andReturn().getResponse().getContentAsString();
        userId = objectMapper.readTree(userBody).get("id").asText();

        String bookBody = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "Clean Code", "Martin", 3, 0))))
                .andReturn().getResponse().getContentAsString();
        bookId = objectMapper.readTree(bookBody).get("id").asText();
    }

    private String createLoan(String uId, String bId) throws Exception {
        return mockMvc.perform(post("/api/loans")
                        .param("userId", uId)
                        .param("bookId", bId))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("GET /api/loans - lista vacía inicialmente retorna 200")
    void getAllLoans_empty_returns200() throws Exception {
        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/loans - crea préstamo y retorna 201 con estado ACTIVE")
    void createLoan_valid_returns201() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .param("userId", userId)
                        .param("bookId", bookId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("LOAN-")))
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.bookId", is(bookId)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.loanDate", notNullValue()))
                .andExpect(jsonPath("$.dueDate", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/loans - usuario inexistente retorna 404")
    void createLoan_invalidUser_returns404() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .param("userId", "USR-INVALID")
                        .param("bookId", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/loans - libro inexistente retorna 404")
    void createLoan_invalidBook_returns404() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .param("userId", userId)
                        .param("bookId", "BOK-INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/loans - límite de 3 préstamos activos retorna 409")
    void createLoan_limitExceeded_returns409() throws Exception {
        String id1 = objectMapper.readTree(mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "Libro 1", "Autor", 1, 0))))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String id2 = objectMapper.readTree(mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "Libro 2", "Autor", 1, 0))))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String id3 = objectMapper.readTree(mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "Libro 3", "Autor", 1, 0))))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        createLoan(userId, id1);
        createLoan(userId, id2);
        createLoan(userId, id3);

        mockMvc.perform(post("/api/loans")
                        .param("userId", userId)
                        .param("bookId", bookId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("3 préstamos")));
    }

    @Test
    @DisplayName("POST /api/loans - libro sin copias disponibles retorna 409")
    void createLoan_bookUnavailable_returns409() throws Exception {
        String singleBookId = objectMapper.readTree(mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookDTO(null, "Un Ejemplar", "Autor", 1, 0))))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        createLoan(userId, singleBookId);

        String otroUserId = objectMapper.readTree(mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "Otro Usuario"))))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/loans")
                        .param("userId", otroUserId)
                        .param("bookId", singleBookId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/loans/{id} - retorna préstamo existente")
    void getLoanById_existing_returns200() throws Exception {
        String body = createLoan(userId, bookId);
        String loanId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(get("/api/loans/" + loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(loanId)));
    }

    @Test
    @DisplayName("GET /api/loans/{id} - ID inexistente retorna 404")
    void getLoanById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/loans/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - devuelve libro y cambia estado a RETURN")
    void returnBook_active_returns200() throws Exception {
        String body = createLoan(userId, bookId);
        String loanId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(put("/api/loans/" + loanId + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RETURN")))
                .andExpect(jsonPath("$.returnDate", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - devolver libro ya devuelto retorna 400")
    void returnBook_alreadyReturned_returns400() throws Exception {
        String body = createLoan(userId, bookId);
        String loanId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(put("/api/loans/" + loanId + "/return"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/loans/" + loanId + "/return"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - ID inexistente retorna 404")
    void returnBook_notFound_returns404() throws Exception {
        mockMvc.perform(put("/api/loans/INVALID/return"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/loans/user/{userId} - retorna préstamos activos del usuario")
    void getActiveLoansByUser_returns200() throws Exception {
        createLoan(userId, bookId);

        mockMvc.perform(get("/api/loans/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")));
    }

    @Test
    @DisplayName("GET /api/loans/user/{userId} - no retorna préstamos devueltos")
    void getActiveLoansByUser_excludesReturned() throws Exception {
        String body = createLoan(userId, bookId);
        String loanId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(put("/api/loans/" + loanId + "/return"));

        mockMvc.perform(get("/api/loans/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
