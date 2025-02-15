package com.alxsshv.security.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class RoleDto {
    private long id;
    private String pseudonym;
}
