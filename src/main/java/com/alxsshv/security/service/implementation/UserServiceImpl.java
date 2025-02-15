package com.alxsshv.security.service.implementation;


import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.model.Role;
import com.alxsshv.security.model.User;
import com.alxsshv.security.repository.UserRepository;
import com.alxsshv.security.service.interfaces.RoleService;
import com.alxsshv.security.service.interfaces.UserService;
import com.alxsshv.security.service.validation.UserNotExist;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Service
@Validated
public class UserServiceImpl implements UserService, UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()){
            throw new UsernameNotFoundException("Пользователь с таким логином не найден");
        }
        return userOpt.get();
    }


    @Override
    public void create(@UserNotExist @Valid UserDto userDto) {
        final User user = mapper.map(userDto, User.class);
        final Set<Role> userRoles = getUserRoles(userDto);
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setRoles(userRoles);
        userRepository.save(user);
    }

    private Set<Role> getUserRoles(UserDto userDto){
        Set<Role> userRoles = new HashSet<>();
        for (RoleDto roleDto : userDto.getRoles()) {
            final Role role = roleService.getRoleById(roleDto.getId());
            userRoles.add(role);
        }
        return userRoles;
    }

    @Override
    public UserDto findById(long id) {
        final User user = getUserById(id);
        return mapper.map(user, UserDto.class);
    }

    @Override
    public User getUserById(long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден");
        }
        return userOpt.get();
    }

    @Override
    public Page<UserDto> findBySearchString(
            @NotBlank(message = "Поле для поиска не может быть пустым") String searchString, Pageable pageable) {
        return userRepository.findBySurnameContainingOrUsernameContaining(searchString, searchString, pageable)
                .map(user -> mapper.map(user, UserDto.class));
    }

    @Override
    public List<UserDto> findBySearchString(@NotBlank(message = "Поле для поиска не может быть пустым") String searchString) {
        return userRepository.findBySurnameContainingOrNameContaining(searchString,searchString).stream()
                .map(user -> mapper.map(user, UserDto.class)).toList();
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> mapper.map(user, UserDto.class));
    }

    @Override
    public Page<UserDto> findAllWaitingCheck(Pageable pageable) {
        return userRepository.findByChecked(false, pageable).map(user -> mapper.map(user, UserDto.class));
    }

    @Override
    public long findWaitingCheckUsersCount(){
        return userRepository.countByChecked(false);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(user -> mapper.map(user, UserDto.class)).toList();
    }

    @Override
    public void update(@Valid UserDto userDto) {
        final User user = getUserById(userDto.getId());
        final User updatingUserData = mapper.map(userDto, User.class);
        user.updateFrom(updatingUserData);
        userRepository.save(user);
    }

    @Override
    public void delete(@Min(value = 2, message = "Недопустимый формат id") long id) {
        userRepository.deleteById(id);
    }
}
