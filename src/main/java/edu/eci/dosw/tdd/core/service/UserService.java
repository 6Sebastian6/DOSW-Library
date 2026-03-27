package edu.eci.dosw.tdd.core.service;


import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public User createUser(User user) {
        UserValidator.validate(user);
        user.setId(IdGeneratorUtil.generateUserId());
        users.add(user);
        return user;
    }

    public User updateUser(String id, User userDetails) {
        UserValidator.validate(userDetails);
        User user = getUserById(id);
        user.setName(userDetails.getName());
        return user;
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        users.remove(user);
    }

    public List<User> searchUsersByName(String name) {
        return users.stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}
