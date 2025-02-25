package com.alxsshv.security.controller;

import com.alxsshv.config.AppConstants;
import com.alxsshv.journal.utils.ServiceMessage;
import com.alxsshv.security.dto.*;
import com.alxsshv.security.model.User;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.UserService;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultRoleService defaultRoleService;
    @Autowired
    private ModelMapper mapper;

    @GetMapping(value = "/username")
    public UserNoPassDto getCurrentUserName(Principal principal) {
        final User user = (User) userService.loadUserByUsername(principal.getName());
        return mapper.map(user, UserNoPassDto.class);
    }

    @GetMapping("/pages")
    public Page<UserNoPassDto> getUsersPageableList(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR, required = false) String pageDir,
            @RequestParam(value = "search", defaultValue = "") String searchString) {
        final Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), "surname"));
        if (searchString.isEmpty() || "undefined".equals(searchString)) {
            return userService.findAll(pageable);
        }
        return userService.findBySearchString(searchString, pageable);
    }

    @GetMapping("pages/wait")
    public Page<UserNoPassDto> findWaitingCheckUsers(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNum,
                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                              @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR, required = false) String pageDir) {
        final Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), "surname"));
        return userService.findAllWaitingCheck(pageable);
    }

    @GetMapping("/search")
    public List<UserNoPassDto> searchUser(
            @RequestParam(value = "search") String searchString) {
        return userService.findBySearchString(searchString);
    }

    @GetMapping("/wait/count")
    public long findWaitingCheckUsersCount() {
        return userService.findWaitingCheckUsersCount();
    }

    @GetMapping
    public List<UserNoPassDto> getUserWithoutPageableList() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") @Min(value = 1, message = "Неверный формат id") long id) {
        final UserNoPassDto dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        userDto.setChecked(false);
        userDto.setEnabled(false);
        userService.create(userDto);
        final String okMessage = "Пользователь  " + userDto.getName() + " " + userDto.getSurname() + " успешно добавлен. " +
                "Вход в аккаунт будет доступен после проверки администратором. Повторите попытку входа через некоторое время";
        log.info(okMessage);
        return ResponseEntity.status(201).body(new ServiceMessage(okMessage));
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        userService.create(userDto);
        final String okMessage = "Пользователь " + userDto.getName() + " " + userDto.getSurname() + " успешно добавлен";
        log.info(okMessage);
        return ResponseEntity.status(201).body(new ServiceMessage(okMessage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto) {
        userService.update(userDto);
        final String okMessage = "Сведения о пользователе " + userDto.getName() + " " + userDto.getSurname() + " обновлены";
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") @Min(value = 1, message = "Неверный формат id") long id) {
        userService.delete(id);
        final String okMessage = "Запись о пользователе успешно удалена";
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }
}
