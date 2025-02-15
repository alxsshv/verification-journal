package com.alxsshv.journal.service.implementation;

import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.repository.JournalRepository;
import com.alxsshv.journal.service.interfaces.JournalService;
import com.alxsshv.journal.service.validation.JournalNotExist;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Validated
@Slf4j
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Override
    public void create(@JournalNotExist @Valid JournalDto journalDto) {
        final Journal journal = mapper.map(journalDto, Journal.class);
        journalRepository.save(journal);
    }

    @Override
    public List<JournalDto> findAll() {
        final List<Journal> journals = journalRepository.findAll();
        return journals.stream().map(j -> mapper.map(j, JournalDto.class)).toList();
    }

    @Override
    public Page<JournalDto> findAll(Pageable pageable) {
        final Page<Journal> journals = journalRepository.findAll(pageable);
        return journals.map(journal -> mapper.map(journal, JournalDto.class));
    }

    @Override
    public JournalDto findById(@Min(value = 1, message = "Неверный формат id") long id) {
        return mapper.map(getById(id), JournalDto.class);
    }

    @Override
    public Journal getById(@Min(value = 1, message = "Неверный формат id") long id) {
        final Optional<Journal> journalOpt = journalRepository.findById(id);
        if (journalOpt.isEmpty()) {
            final String errorMessage = "Журнал поверки не найден";
            log.error("{} id={}", errorMessage, id);
            throw new EntityNotFoundException(errorMessage);
        }
        return journalOpt.get();
    }

    @Override
    public List<JournalDto> findBySearchString(String searchString) {
        final List<Journal> journals = journalRepository
                .findByNumberContainingOrTitleContainingOrDescriptionContaining(searchString,
                        searchString, searchString);
        return journals.stream().map(journal -> mapper.map(journal, JournalDto.class)).toList();
    }

    @Override
    public Page<JournalDto> findBySearchString(String searchString, Pageable pageable) {
        final Page<Journal> journals = journalRepository
                .findByNumberContainingOrTitleContainingOrDescriptionContaining(searchString,
                        searchString, searchString, pageable);
        return journals.map(journal -> mapper.map(journal, JournalDto.class));
    }

    @Override
    public void update(@Valid JournalDto journalDto) {
        final Journal journalFromDb = getById(journalDto.getId());
        final Journal journalData = mapper.map(journalDto, Journal.class);
        journalFromDb.updateFrom(journalData);
        journalRepository.save(journalFromDb);
    }

    @Override
    public void deleteById(@Min(value = 1, message = "Неверный формат id") long id) {
        journalRepository.deleteById(id);
    }
}
