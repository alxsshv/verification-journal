package com.alxsshv.security.repository;

import com.alxsshv.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);
    List<User> findBySurname(String surname);
    List<User> findBySurnameContainingOrNameContaining(String surname, String name);
    Page<User> findBySurnameContainingOrUsernameContaining(String surname, String username, Pageable pageable);
    Page<User> findByChecked(boolean checked, Pageable pageable);
    long countByChecked(boolean checked);
}
