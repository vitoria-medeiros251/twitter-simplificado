package com.invillia.spring.security.domain;

import lombok.Getter;

@Getter
public enum RoleEnum {

    BASIC(1L),
    ADMIN(2L);

    private final long roleId;

    RoleEnum(long roleId) {
        this.roleId = roleId;
    }
}
