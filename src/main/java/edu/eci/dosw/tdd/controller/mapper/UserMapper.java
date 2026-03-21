package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getName()
        );
    }

    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        return new User(
                userDTO.getId(),
                userDTO.getName()
        );
    }
}
