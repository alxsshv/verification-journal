package com.alxsshv.security.service.interfaces;

import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.dto.UserNoPassDto;
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

    UserNoPassDto findById(long id);

    User getUserById(long id);

    Page<UserNoPassDto> findBySearchString(@NotBlank(message = "Поле для поиска не может быть пустым") String searchString, Pageable pageable);

    List<UserNoPassDto> findBySearchString(@NotBlank(message = "Поле для поиска не может быть пустым") String searchString);

    Page<UserNoPassDto> findAll(Pageable pageable);

    List<UserNoPassDto> findAll();

    Page<UserNoPassDto> findAllWaitingCheck(Pageable pageable);

    long findWaitingCheckUsersCount();

    void update(@Valid UserDto userDto);

    void delete(@Min(value = 2, message = "Недопустимый формат id") long id);

}
