package com.alxsshv.journal.dto;

import com.alxsshv.security.dto.UserNoPassDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolDto {
    private long id;
    @NotEmpty(message = "протокол не может быть пустым")
    private String number;
    private String storageFileName;
    private String originalFilename;
    private String signedFileName;
    private String description;
    private String extension;
    private String verificationDate;
    private long journalId;
    private UserNoPassDto verificationEmployee;
    private boolean awaitingSigning;
    private boolean signed;
    private String miModification;
    private String miSerialNum;
    private String uploadingDate;
    private String updatingDate;

}
