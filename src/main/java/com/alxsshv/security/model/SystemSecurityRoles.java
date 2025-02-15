package com.alxsshv.security.model;

import lombok.Getter;

@Getter
public enum SystemSecurityRoles {
    USER ("USER", "Пользователь"),
    SYSTEM_ADMIN ("SYSTEM_ADMIN", "Администратор"),
    VERIFICATION_EMPLOYEE ("VERIFICATION_EMPLOYEE", "Поверитель"),
    VERIFICATION_MANAGER("VERIFICATION_MANAGER","Контроль выполнения поверки");


    SystemSecurityRoles(String name, String pseudonym){
        this.name = name;
        this.pseudonym = pseudonym;
    }

    private final String name;
    private final String pseudonym;

    @Override
    public String toString() {
        return name;
    }
}
