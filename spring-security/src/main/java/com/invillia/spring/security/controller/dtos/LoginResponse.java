package com.invillia.spring.security.controller.dtos;

//LoginResponse(saida)Resposta que o servidor envia após login bem-sucedido
public record LoginResponse(String accessToken, Long expiresIn) {
}
