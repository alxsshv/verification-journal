package com.alxsshv.security.service.validation;

import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.model.User;
import com.alxsshv.security.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@AllArgsConstructor
public class UserNotExistValidator implements ConstraintValidator<UserNotExist, UserDto> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        final Optional<User> userOpt = userRepository.findByUsername(userDto.getUsername());
        return userOpt.isEmpty();
    }
}
