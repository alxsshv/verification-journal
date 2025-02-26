package com.alxsshv.security.initializer;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@AllArgsConstructor
@Slf4j
public class UserInitializer {
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultRoleService defaultRoleService;
    @Autowired
    private ModelMapper mapper;

    @PostConstruct
    public void initialize() {
        generateRootUser();
    }

    private void generateRootUser() {
        final UserDto userDto = new UserDto();
        try {
            userDto.setName("Root");
            userDto.setSurname("Root");
            userDto.setPatronymic("Root");
            userDto.setUsername("Root");
            userDto.setRoles(Set.of(mapper.map(defaultRoleService.getRootRole(), RoleDto.class)));
            userDto.setPassword("Rooot");
            userDto.setChecked(true);
            userDto.setEnabled(true);
            userService.create(userDto);
            log.info("Пользователь с полным доступом добавлен в систему");
        } catch (ConstraintViolationException ex) {
            log.warn(ex.getMessage());
        }
    }

}
