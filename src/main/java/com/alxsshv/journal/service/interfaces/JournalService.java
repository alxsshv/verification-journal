package com.alxsshv.journal.service.interfaces;

import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.service.validation.JournalNotExist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JournalService {

    void create(@JournalNotExist @Valid JournalDto journalDto);

    List<JournalDto> findAll();

    Page<JournalDto> findAll(Pageable pageable);

    JournalDto findById(@Min(value = 1, message = "Неверный формат id") long id);

    Journal getById(@Min(value = 1, message = "Неверный формат id") long id);

    List<JournalDto> findBySearchString(@NotNull(message = "Поисковый запрос не может быть пустым") String searchString);

    Page<JournalDto> findBySearchString(@NotNull String searchString, Pageable pageable);

    void update(@Valid JournalDto journalDto);

    void deleteById(@Min(value = 1, message = "Неверный формат id") long id);

}
