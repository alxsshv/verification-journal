package com.alxsshv.security.service.implementation;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.model.Role;
import com.alxsshv.security.repository.RoleRepository;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.RoleService;
import com.alxsshv.security.service.validation.RoleNotExist;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Validated
@Service
public class RoleServiceImpl implements RoleService, DefaultRoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper mapper;

    @Override
    public void create(@RoleNotExist @Valid Role role) {
        role.setDefaultRole(false);
        role.setRootRole(false);
        roleRepository.save(role);
    }

    @Override
    public Role getRoleById(long id) {
        final Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            throw new EntityNotFoundException("Роль не найдена");
        }
        return roleOpt.get();
    }

    @Override
    public Role getRoleByName(String name) {
        final Optional<Role> roleOpt = roleRepository.findByName("ROLE_" + name);
        if (roleOpt.isEmpty()) {
            throw new EntityNotFoundException("Роль не найдена");
        }
        return roleOpt.get();
    }

    @Override
    public RoleDto findById(long id) {
        return mapper.map(getRoleById(id), RoleDto.class);
    }

    @Override
    public RoleDto findByName(String name) {
        final Optional<Role> roleOpt = roleRepository.findByName(name);
        if (roleOpt.isEmpty()) {
            final String errorMessage = "Роль не найдена";
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        return mapper.map(roleOpt.get(), RoleDto.class);
    }

    @Override
    public Page<RoleDto> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(role -> mapper.map(role, RoleDto.class));
    }

    @Override
    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(role -> mapper.map(role, RoleDto.class)).toList();
    }

    @Override
    public void delete(long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public void createDefault(@RoleNotExist Role role) {
        final Optional<Role> rootRoleOpt = roleRepository.findByRootRole(true);
        final Optional<Role> defaultRoleOpt = roleRepository.findByDefaultRole(true);
        if (defaultRoleOpt.isPresent()) {
            throw new IllegalArgumentException("Роль по умолчанию уже существует");
        }
        if (rootRoleOpt.isPresent() && rootRoleOpt.get().equals(role)) {
            throw new IllegalArgumentException("Роль с полными правами не может быть установлена" +
                    " в качестве роли с полными правами");
        }
        role.setRootRole(false);
        role.setDefaultRole(true);
        roleRepository.save(role);
    }

    @Override
    public void createRoot(@RoleNotExist Role role) throws ConstraintViolationException {
        final Optional<Role> rootRoleOpt = roleRepository.findByRootRole(true);
        final Optional<Role> defaultRoleOpt = roleRepository.findByDefaultRole(true);
        if (rootRoleOpt.isPresent()) {
            throw new IllegalArgumentException("Роль с полными правами уже существует");
        }
        if (defaultRoleOpt.isPresent() && defaultRoleOpt.get().equals(role)) {
            throw new IllegalArgumentException("Роль по умолчанию не может быть установлена " +
                    "в качестве роли с полными правами");
        }
        role.setRootRole(true);
        role.setDefaultRole(false);
        roleRepository.save(role);
    }

    @Override
    public Role getDefaultRole() {
        final Optional<Role> roleOpt = roleRepository.findByDefaultRole(true);
        if (roleOpt.isEmpty()) {
            final String errorMessage = "Роль по умолчанию не найдена";
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        return roleOpt.get();
    }

    @Override
    public Role getRootRole() {
        final Optional<Role> roleOpt = roleRepository.findByRootRole(true);
        if (roleOpt.isEmpty()) {
            final String errorMessage = "Роль полным доступом не найдена";
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        return roleOpt.get();
    }
}
