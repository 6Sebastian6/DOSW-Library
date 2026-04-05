package edu.eci.dosw.tdd.core.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String name;
    private String id;
    private String username;
    private String password;
    private Role role;
}
