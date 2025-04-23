package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserServiceInterface {
    void createUser(User user);
    List<User> getAllUsers();
    User getUserById(Integer id);
    void deleteUserById(Integer id);
    void updateUser(Integer id, User user);
    UserDetails loadUserByUsername(String username);
}
