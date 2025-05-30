package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")

public class AdminRestController {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/save_user")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        userDTO.setRoles(
                userDTO.getRoles().stream()
                        .map(role -> roleService.findByName(role.getName()))
                        .map(RoleDto::new)
                        .collect(Collectors.toSet())
        );

        userService.saveUser(userDTO);
        return ResponseEntity.ok("User created successfully");
    }


    @PutMapping("/update_user/{id}")
    public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO) {
        userDTO.setRoles(
                userDTO.getRoles().stream()
                        .map(role -> roleService.findByName(role.getName()))
                        .map(RoleDto::new)
                        .collect(Collectors.toSet())
        );

        userService.updateUser(id, userDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
