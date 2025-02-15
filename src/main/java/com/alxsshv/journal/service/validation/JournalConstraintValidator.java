package com.alxsshv.journal.service.validation;

import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.repository.JournalRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@AllArgsConstructor
public class JournalConstraintValidator implements ConstraintValidator<JournalNotExist, JournalDto> {
    @Autowired
    private JournalRepository repository;

    @Override
    public boolean isValid(JournalDto journalDto, ConstraintValidatorContext context) {
        final Optional<Journal> journalOpt = repository.findByNumber(journalDto.getNumber());
        return journalOpt.isEmpty();
    }
}
