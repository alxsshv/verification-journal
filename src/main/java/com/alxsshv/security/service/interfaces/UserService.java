package com.alxsshv.security.service.interfaces;

import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.model.User;
import com.alxsshv.security.service.validation.UserNotExist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {
    void create(@UserNotExist @Valid UserDto userDto);
    UserDto findById(long id);
    User getUserById(long id);
    Page<UserDto> findBySearchString(@NotBlank(message = "Поле для поиска не может быть пустым") String searchString, Pageable pageable);
    List<UserDto> findBySearchString(@NotBlank(message = "Поле для поиска не может быть пустым") String searchString);
    Page<UserDto> findAll(Pageable pageable);
    List<UserDto> findAll();
    Page<UserDto> findAllWaitingCheck(Pageable pageable);
    long findWaitingCheckUsersCount();
    void update(@Valid UserDto userDto);
    void delete(@Min(value = 2, message = "Недопустимый формат id") long id);

}
