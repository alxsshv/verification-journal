package com.alxsshv.journal.dto;


import com.alxsshv.security.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolDto {
    private long id;
    @NotNull(message = "протокол не может быть пустым")
    private String number;
    private String storageFileName;
    private String originalFilename;
    private String signedFileName;
    private String description;
    private String extension;
    private LocalDate verificationDate;
    private long journalId;
    private UserDto verificationEmployee;
    private boolean awaitingSigning;
    private boolean signed;
    private LocalDateTime uploadingDate;
    private LocalDateTime updatingDate;

}
