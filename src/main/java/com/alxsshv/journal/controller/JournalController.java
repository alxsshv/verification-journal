package com.alxsshv.journal.controller;

import com.alxsshv.config.AppConstants;
import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.service.interfaces.JournalService;
import com.alxsshv.journal.utils.ServiceMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journals")
@AllArgsConstructor
@Slf4j
public class JournalController {
    @Autowired
    private JournalService journalService;

    @PostMapping
    public ResponseEntity<?> createJournal(@RequestBody JournalDto journalDto) {
        journalService.create(journalDto);
        final String okMessage = "Создан новый журнал № " + journalDto.getNumber();
        return ResponseEntity.status(201).body(new ServiceMessage(okMessage));
    }

    @GetMapping
    public List<JournalDto> findAllJournals() {
        return journalService.findAll();
    }

    @GetMapping("/pages")
    public Page<JournalDto> findAllJournals(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String dir,
            @RequestParam(value = "search", defaultValue = "") String searchString) {
        final Sort.Direction direction = Sort.Direction.valueOf(dir.toUpperCase());
        final Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, "number"));
        if (searchString.isEmpty()) {
            return journalService.findAll(pageable);
        }
        return journalService.findBySearchString(searchString, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalDto> getJournalById(@PathVariable("id") long id) {
        final JournalDto journalDto = journalService.findById(id);
        return ResponseEntity.ok(journalDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceMessage> updateJournal(@PathVariable("id") long id,
                                                        @RequestBody JournalDto journalDto) {
        if (journalDto.getId() != id) {
            final String errorMessage = "Идентифкатор обновляемого журнала отличается от идентификатора в даных для обновления";
            log.error("{} id журнала={}, id для обновления={}", errorMessage, id, journalDto.getId());
            return ResponseEntity.status(400).body(new ServiceMessage(errorMessage));
        }
        journalService.update(journalDto);
        final String okMessage = "Данные о журнале изменены";
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }
}
