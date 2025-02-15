package com.alxsshv.security.controller;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.service.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleDto> getRoleWithoutPageableList() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable("id") long id){
        final RoleDto dto = roleService.findById(id);
        return ResponseEntity.ok(dto);
    }

}
