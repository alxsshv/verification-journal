package com.alxsshv.journal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JournalDto {
    private long id;
    @NotNull(message = "Номер журнала не может быть пустым")
    private String number;
    private String title;
    private String description;
}
