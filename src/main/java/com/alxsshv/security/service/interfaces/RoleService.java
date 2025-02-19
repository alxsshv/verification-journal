package com.alxsshv.security.service.interfaces;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.model.Role;
import com.alxsshv.security.service.validation.RoleNotExist;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

    void create(@RoleNotExist @Valid Role role);

    RoleDto findById(long id);

    RoleDto findByName(String name);

    Role getRoleById(long id);

    Role getRoleByName(String name);

    Page<RoleDto> findAll(Pageable pageable);

    List<RoleDto> findAll();

    void delete(long id);

}
