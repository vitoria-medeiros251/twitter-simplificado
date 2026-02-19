package com.invillia.spring.security.config;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey; // ${} busca o valor no application.properties e o spring converte o arquivo app.pub em um objeto RSAPublicKey

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey; // Converte o arquivo app.key em um objeto RSAPrivateKey


    @Bean //configura a segurança da sua aplicação. Linha por linha
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.POST,"/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tweets").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tweets").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/tweets/**").authenticated()
                        .anyRequest().authenticated()) // todas as requisiçoes precisam de autenticacao
                .csrf(csrf -> csrf.disable()) //Desabilita CSRF (Cross-Site Request Forgery) - comum em APIs REST stateless
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))// configura como resource server Oauth2 - valida tokens jwt nas requisiçoes /Usa as chaves RSA (publicKey/privateKey) para verificar se o token é válido
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//Sem sessão no servidor - cada requisição deve ter seu próprio token JWT/ não cria cookies de sessao


        return http.build();

    }



    @Bean  //jwt decodifica e valida o token jwt
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(publicKey).build(); // usa a chave publica para verificar se o token e legitimo
      //verifica se o token foi assinado com a chave privada

    }

    @Bean //cria e assina tokens jwt no login e codifica e assina o tokens
    public JwtEncoder jwtEncoder(){  //usa a chave privada para assinar e cria um jwl com ambas as chaves
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new  NimbusJwtEncoder(jwks);

    }

    @Bean //criptografa as senhas
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //criptografa as senhas com o algoritmo BCrypt


}
