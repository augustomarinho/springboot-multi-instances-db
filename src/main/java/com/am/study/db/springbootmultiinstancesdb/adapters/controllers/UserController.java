package com.am.study.db.springbootmultiinstancesdb.adapters.controllers;

import com.am.study.db.springbootmultiinstancesdb.application.models.User;
import com.am.study.db.springbootmultiinstancesdb.application.ports.UserPort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserPort userPort;

    public UserController(UserPort userPort) {
        this.userPort = userPort;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userPort.save(user);
    }

    @GetMapping("/users/{id}:{dbType}")
    public User getUser(@PathVariable Long id, @PathVariable String dbType) {
        return userPort.findByIdForceDBType(id, dbType).orElse(null);
    }

    @GetMapping("/users/{id}/transaction:{dbType}")
    public User getUserInTransaction(@PathVariable Long id, @PathVariable String dbType) {
        if ("rw".equalsIgnoreCase(dbType)) {
            return userPort.findByIdWithTransactionRW(id).orElse(null);
        }

        return userPort.findByIdWithTransactionRO(id).orElse(null);
    }

    @GetMapping("/users/external/{externalId}:{dbType}")
    public User getUserByExternalId(@PathVariable UUID externalId, @PathVariable String dbType) {
        return userPort.findByExternalIdForceDBType(externalId, dbType).orElse(null);
    }

    @GetMapping("/users:{dbType}")
    public List<User> getAllUsers(@PathVariable String dbType) {
        return userPort.findAllForceDBType(dbType);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userPort.deleteById(id);
    }
}