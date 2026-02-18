package com.invillia.spring.security.config;
import com.invillia.spring.security.domain.RoleEnum;
import com.invillia.spring.security.domain.User;
import com.invillia.spring.security.repositories.RoleRepository;
import com.invillia.spring.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner { // executa automaticamente quando a aplicação inicia

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleRepository.findByName(RoleEnum.ADMIN); //busca a role admin no banco

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(     // verifica se ja existe um usuario ADMIN se existe apenas imprime a mensagem
                                       // se Não existe cria o usuario admin


                user -> {
                    System.out.println("admin ja existe");
                },

                () -> {
                    var user = new User(); //cria usuario admin com Username,senha (criptografada com BCrpt) e salva no banco
                    user.setUsername("admin");
                    user.setPassword(bCryptPasswordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                }
               //Garante que sempre exista um usuário admin no sistema ao iniciar a aplicação.
                // sem o CommandLineRunner, você teria que criar o admin manualmente toda vez que rodasse a aplicação pela primeira vez.
        );

    }

}
