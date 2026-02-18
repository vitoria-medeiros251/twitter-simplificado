package com.invillia.spring.security.repositories;

import com.invillia.spring.security.domain.Role;
import com.invillia.spring.security.domain.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(RoleEnum name);
}
