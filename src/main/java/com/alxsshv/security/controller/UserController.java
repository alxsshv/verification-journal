package com.alxsshv.security.controller;

import com.alxsshv.config.AppConstants;
import com.alxsshv.journal.utils.ServiceMessage;
import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.model.User;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

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
    public UserDto getCurrentUserName(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        return mapper.map(user, UserDto.class);
    }

    @GetMapping("/pages")
    public Page<UserDto> getUsersPageableList(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR, required = false) String pageDir,
            @RequestParam(value = "search", defaultValue = "") String searchString){
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), "surname"));
        if(searchString.isEmpty() || searchString.equals("undefined")) {
            return userService.findAll(pageable);
        }
        return userService.findBySearchString(searchString,pageable);
    }

    @GetMapping("pages/wait")
    public Page<UserDto>findWaitingCheckUsers(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNum,
                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                              @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR, required = false) String pageDir){
        Pageable pageable = PageRequest.of(pageNum,pageSize,Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()),"surname"));
        return userService.findAllWaitingCheck(pageable);
    }

    @GetMapping("/search")
    public List<UserDto> searchUser(
            @RequestParam(value = "search") String searchString){
        return userService.findBySearchString(searchString);
    }

    @GetMapping("/wait/count")
    public long findWaitingCheckUsersCount(){
        return userService.findWaitingCheckUsersCount();
    }

    @GetMapping
    public List<UserDto> getUserWithoutPageableList() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id){
        UserDto dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        userDto.setChecked(false);
        userDto.setEnabled(false);
        userService.create(userDto);
        String okMessage = "Пользователь  " + userDto.getName() + " " + userDto.getSurname() + " успешно добавлен. " +
                "Вход в аккаунт будет доступен после проверки администратором. Повторите попытку входа через некоторое время";
        log.info(okMessage);
        return ResponseEntity.status(201).body(new ServiceMessage(okMessage));
    }

    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto){
        userService.create(userDto);
        String okMessage = "Пользователь " + userDto.getName() + " "
                + userDto.getSurname() + " успешно добавлен";
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto){
        userService.update(userDto);
        String okMessage = "Сведения о пользователе " + userDto.getName() + " "
                + userDto.getSurname() + " обновлены";
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id){
        userService.delete(id);
        String okMessage ="Запись о пользователе успешно удалена";
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }
}
