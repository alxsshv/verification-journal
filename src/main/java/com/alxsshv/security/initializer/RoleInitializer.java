package com.alxsshv.security.initializer;

import com.alxsshv.security.model.Role;
import com.alxsshv.security.model.SystemSecurityRoles;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.RoleService;
import com.alxsshv.security.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer {
    @Autowired
    private DefaultRoleService defaultRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @PostConstruct
    public void initialize() {
        generateRootRole(SystemSecurityRoles.SYSTEM_ADMIN);
        generateDefaultRole(SystemSecurityRoles.USER);
        generateRole(SystemSecurityRoles.VERIFICATION_EMPLOYEE);
    }

    private void generateRole(SystemSecurityRoles securityRole) {
        final Role role = new Role();
        try {
            role.setName("ROLE_" + securityRole.getName());
            role.setPseudonym(securityRole.getPseudonym());
            roleService.create(role);
            log.info("Роль {} добавлена в систему", securityRole.getPseudonym());
        } catch (IllegalArgumentException | ConstraintViolationException ex) {
            log.warn(ex.getMessage());
        }
    }

    private void generateDefaultRole(SystemSecurityRoles securityRole) {
        final Role role = new Role();
        try {
            role.setName("ROLE_" + securityRole.getName());
            role.setPseudonym(securityRole.getPseudonym());
            defaultRoleService.createDefault(role);
            log.info("Роль {} добавлена в систему", securityRole.getPseudonym());
        } catch (IllegalArgumentException | ConstraintViolationException ex) {
            log.warn(ex.getMessage());
        }
    }

    private void generateRootRole(SystemSecurityRoles securityRole) {
        final Role role = new Role();
        try {
            role.setName("ROLE_" + securityRole.getName());
            role.setPseudonym(securityRole.getPseudonym());
            defaultRoleService.createRoot(role);
            log.info("Роль {} добавлена в систему", securityRole.getPseudonym());
        } catch (IllegalArgumentException | ConstraintViolationException ex) {
            log.warn(ex.getMessage());
        }
    }
}
