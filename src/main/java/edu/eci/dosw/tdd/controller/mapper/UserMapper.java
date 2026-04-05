package edu.eci.dosw.tdd.controller.mapper;


import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPassword(null);//la contraseña no se puede devolver
        dto.setRole(user.getRole());
        return dto;
    }

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
    }
}
