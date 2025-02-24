package com.alxsshv.journal.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JournalDto {
    private long id;
    @NotEmpty(message = "Номер журнала не может быть пустым")
    private String number;
    private String title;
    private String description;
}
