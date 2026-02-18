package com.invillia.spring.security.controller.dtos;

//LoginRequests e a entrada dados que o cliente envia para fazer login
public record LoginRequests(String username, String password) {
}
