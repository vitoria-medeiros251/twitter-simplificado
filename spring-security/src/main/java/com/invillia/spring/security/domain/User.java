package com.invillia.spring.security.domain;
import com.invillia.spring.security.controller.dtos.LoginRequests;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.results.graph.Fetch;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name= "tb_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(unique = true)
    private String username;
    private String Password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name= "tb_users_role",
            joinColumns = @JoinColumn(name= "user_id"),
            inverseJoinColumns = @JoinColumn(name= "role_id")
    )
    private Set<Role> roles;

   public boolean isLoginCorrect(LoginRequests loginRequests, PasswordEncoder passwordEncoder){
       return passwordEncoder.matches(loginRequests.password(), this.Password);
   }

}
