package com.invillia.spring.security.controller.dtos;
//O CreateUserDto define quais informações o cliente pode enviar ao criar um usuário no UserController, protegendo campos sensíveis como roles e userId
public record CreateUserDto(String username , String password) {
}
