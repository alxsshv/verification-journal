package com.alxsshv.security.repository;

import com.alxsshv.security.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findAll();
    Page<Role> findAll(Pageable pageable);
    Optional<Role> findByDefaultRole(boolean defaultRole);
    Optional<Role> findByRootRole(boolean rootRole);
}
