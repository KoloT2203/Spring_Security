package ru.kata.spring.boot_security.demo.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;
import  ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDao implements UserDaoInterface {

    @PersistenceContext
    private EntityManager em;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDao(EntityManager em, PasswordEncoder passwordEncoder) {
        this.em = em;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Role> getRoles() {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    @Override
    public List<User> index() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User getUserById(Integer id) {
        return em.find(User.class, id);
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public void update(Integer id, User updateUser) {
        User user = em.find(User.class, id);
        user.setUsername(updateUser.getUsername());

        String pass = updateUser.getPassword();
        user.setPassword(passwordEncoder.encode(pass));

        Set<Role> managedRoles = new HashSet<>();
        for (Role role : updateUser.getRoles()) {
            Role persistentRole = findRoleByName(role.getName());
            if (persistentRole == null) {
                em.persist(role);
            }
            managedRoles.add(persistentRole);
        }

        user.getRoles().clear();
        user.getRoles().addAll(managedRoles);
        user.setAge(updateUser.getAge());
        user.setName(updateUser.getName());
        user.setLastName(updateUser.getLastName());
        em.merge(user);
    }

    @Override
    public void deleteById(Integer id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }
    @Override
    public User findByUsername(String username) {
        return em.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public Role findRoleByName(String name) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
