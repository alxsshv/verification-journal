package com.alxsshv.security.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private long id;
    private String pseudonym;

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", pseudonym='" + pseudonym + '\'' +
                '}';
    }
}
