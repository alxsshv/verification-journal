package com.alxsshv.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNoPassDto {
    private long id;
    private String username;
    private String surname;
    private String name;
    private String patronymic;
    private String phoneNumber;
    private boolean checked;
    private boolean enabled;
    private Set<RoleDto> roles;
}
