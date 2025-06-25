package ru.kata.spring.boot_security.demo.mappers;


import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.stream.Collectors;

@Component
public class DtoMapper {

    private final RoleService roleService;

    public DtoMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setAge(userDTO.getAge());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setPassword(userDTO.getPassword());
        String rolesStr = userDTO.getRoles().stream()
                .map(RoleDto::getName)
                .collect(Collectors.joining(","));
        user.setRolesString(rolesStr);
        user.setRoles(userDTO.getRoles().stream()
                .map(RoleDto::getName).map(roleService::findByName)
                .collect(Collectors.toSet()));
        return user;
    }
}
