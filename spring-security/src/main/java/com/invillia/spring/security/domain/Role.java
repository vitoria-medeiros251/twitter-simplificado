package com.invillia.spring.security.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "role_id")
    private Long roleId;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleEnum name;





}
