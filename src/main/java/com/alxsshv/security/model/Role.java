package com.alxsshv.security.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Наименование роли не может быть пустым")
    private String name;
    @NotEmpty(message = "Псевдоним роли не может быть пустым")
    private String pseudonym;
    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private Set<User> users;
    private boolean defaultRole;
    private boolean rootRole;

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final Role role = (Role) object;
        return id == role.id && Objects.equals(name, role.name) && Objects.equals(pseudonym, role.pseudonym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pseudonym);
    }
}
