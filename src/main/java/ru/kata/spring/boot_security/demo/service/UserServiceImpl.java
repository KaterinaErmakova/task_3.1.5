package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.mappers.DtoMapper;
import ru.kata.spring.boot_security.demo.models.User;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final RoleService roleService;
    private final UserDao userDao;
    private final DtoMapper dtoMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleService roleService, DtoMapper dtoMapper) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.dtoMapper = dtoMapper;
    }


    @Override
    @Transactional
    public List<UserDTO> getAllUsers() {
        return userDao.getAllUsers().stream().map(UserDTO::new).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public UserDTO getUserById(int id) {
        return new UserDTO(userDao.getUserById(id));

    }

    @Override
    @Transactional
    public void saveUser(UserDTO userDTO) {
        User user = dtoMapper.toEntity(userDTO);
        userDao.saveUser(user);

    }

    @Override
    @Transactional
    public void updateUser(int id, UserDTO userDTO) {

        User updatedUser = userDao.getUserById(id);
        if (updatedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        updatedUser.setName(userDTO.getName());
        updatedUser.setSurname(userDTO.getSurname());
        updatedUser.setAge(userDTO.getAge());
        updatedUser.setPhoneNumber(userDTO.getPhoneNumber());
        updatedUser.setPassword(userDTO.getPassword());
        updatedUser.setRoles(userDTO.toRolesSet(roleService));
        updatedUser.setRolesString(userDTO.getRolesString());

        userDao.updateUser(updatedUser);

    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        User user = userDao.findById(id);
        if (user != null) {
            userDao.deleteUser(user);

        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

