package edu.eci.dosw.tdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoswLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoswLibraryApplication.class, args);
        System.out.println("📚 Biblioteca DOSW - Aplicación iniciada");
    }
}
