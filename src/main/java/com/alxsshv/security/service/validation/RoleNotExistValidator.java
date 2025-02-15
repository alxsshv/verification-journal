package com.alxsshv.security.service.validation;

import com.alxsshv.security.model.Role;
import com.alxsshv.security.repository.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@AllArgsConstructor
public class RoleNotExistValidator implements ConstraintValidator<RoleNotExist, Role> {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        Optional<Role> roleOpt = roleRepository.findByName(role.getPseudonym());
        return  roleOpt.isEmpty();
    }
}
