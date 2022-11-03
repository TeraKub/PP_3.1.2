package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

// UserDetailsService предоставляет юзера по имени пользователя
public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    List<User> allUsers();
    boolean saveUser(User user);
    User getUserById(int id);
    void delete(int id);
}
