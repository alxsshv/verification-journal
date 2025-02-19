package com.alxsshv.security.service.interfaces;

import com.alxsshv.security.model.Role;
import com.alxsshv.security.service.validation.RoleNotExist;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;

@Service
public interface DefaultRoleService {

    void createDefault(@RoleNotExist Role role) throws ConstraintViolationException;

    void createRoot(@RoleNotExist Role role) throws ConstraintViolationException;

    Role getDefaultRole();

    Role getRootRole();
}
