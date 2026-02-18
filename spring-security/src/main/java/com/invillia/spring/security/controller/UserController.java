package com.invillia.spring.security.controller;


import com.invillia.spring.security.controller.dtos.CreateUserDto;
import com.invillia.spring.security.domain.Role;
import com.invillia.spring.security.domain.RoleEnum;
import com.invillia.spring.security.domain.User;
import com.invillia.spring.security.repositories.RoleRepository;
import com.invillia.spring.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto){
        var basicRole = roleRepository.findByName(RoleEnum.BASIC);// busca a role Basic novo usuario recebe a role Basic

        var userFrom = userRepository.findByUsername(dto.username());//verifica se o usuario ja existe
        if(userFrom.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));// cria o usuario
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping  //@PreAuthorize: Só permite acesso se o usuário tiver role ADMI
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers(){
        var users = userRepository.findAll(); //busca todos os usuarios
        return ResponseEntity.ok(users);
    }
}
