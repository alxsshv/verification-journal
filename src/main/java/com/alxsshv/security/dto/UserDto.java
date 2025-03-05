package com.alxsshv.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @Size(min = 3, message = "Имя пользователя должно содержать не менее 3 символов")
    private String username;
    @Size(min = 5, message = "Длина пароля должна быть не менее 5 символов")
    private String password;
    @NotEmpty(message = "Пожалуйста укажите фамилию пользователя")
    private String surname;
    @NotEmpty(message = "Пожалуйста укажите имя пользователя")
    private String name;
    @NotEmpty(message = "Пожалуйста укажите отчество пользователя")
    private String patronymic;
    private String phoneNumber;
    private boolean checked;
    private boolean enabled;
    private Set<RoleDto> roles;
}
