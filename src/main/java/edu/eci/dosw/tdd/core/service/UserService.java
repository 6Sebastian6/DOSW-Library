package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.UserEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserEntityMapper::toDomain)
                .toList();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .map(UserEntityMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntityMapper::toDomainWithPassword)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }

    public User createUser(User user) {
        UserValidator.validateForCreate(user);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El username '" + user.getUsername() + "' ya está en uso");
        }

        user.setId(IdGeneratorUtil.generateUserId());
        // Hashea el password con BCrypt antes de persistir
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity saved = userRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(saved);
    }

    public User updateUser(String id, User userDetails) {
        UserValidator.validate(userDetails);

        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        existing.setName(userDetails.getName());
        return UserEntityMapper.toDomain(userRepository.save(existing));
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(UserEntityMapper::toDomain)
                .toList();
    }
}