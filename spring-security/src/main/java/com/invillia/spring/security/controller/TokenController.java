package com.invillia.spring.security.controller;


import com.invillia.spring.security.controller.dtos.LoginRequests;
import com.invillia.spring.security.controller.dtos.LoginResponse;
import com.invillia.spring.security.domain.Role;
import com.invillia.spring.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;


@RestController
@RequestMapping("login")
public class TokenController {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//fluxo de login: O cliente envia uma requisição POST para /login com o username e password no corpo da requisição. O controller verifica se o usuário existe e se a senha está correta usando o BCryptPasswordEncoder. Se as credenciais forem válidas, o controller cria um token JWT contendo as informações do usuário e suas roles, e retorna esse token na resposta. O token tem um tempo de expiração definido (300 segundos neste caso). O cliente pode então usar esse token para autenticar futuras requisições ao backend, incluindo-o no header Authorization como Bearer <token>.
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequests loginRequests) {
        var user = userRepository.findByUsername(loginRequests.username()); //busca o usuario no banco

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequests, bCryptPasswordEncoder)) { //verifica se a senha esta correta(Bcrypt)
            throw new BadCredentialsException("user or password is invalid"); // se invalido lança erro 401


        }


        var now = Instant.now(); //Pega o momento exato atual
        var expiresIn = 300L; //token expira em 300 segundos 5 minutos o L e de Long


        var scopes = user.get().getRoles() // pega as roles do usuario e converte para string (ADMIN BASIC) para colocar no token JWT
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder() // monta as informaçoes do token
                .issuer("mybackend")      //Quem emitiu o token
                .subject(user.get().getUserId().toString()) //ID do usuario
                .expiresAt(now.plusSeconds(expiresIn)) // hora atual + 300 segundos quando o token fica invalido
                .claim("scope", scopes)
                .build();

                                                                                           //jwtEncoder cria o token JWT
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); //Usa a chave privada para assinar o token /getTokenValue() extrai o token como string
        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn)); // retorna resposta

    }


}





