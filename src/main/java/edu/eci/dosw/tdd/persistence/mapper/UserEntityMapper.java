package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;

public class UserEntityMapper {

    private UserEntityMapper() {}

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .password(null)
                .role(entity.getRole())
                .build();
    }

    public static User toDomainWithPassword(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}