package com.alxsshv.journal.service.interfaces;

import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.security.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProtocolService {

    void upload(MultipartFile file, @Valid ProtocolDto protocolDto, Journal journal) throws IOException;

    Page<ProtocolDto> findProtocols(Pageable pageable);

    Page<ProtocolDto> findProtocolsByJournal(Journal journal, Pageable pageable);

    Page<ProtocolDto> findProtocolByJournalAndSearchString(Journal journal, String searchString, Pageable pageable);

    Protocol getProtocolById(@Min(value = 1, message = "Недопустимое значение id") long id);

    ResponseEntity<?> getProtocolFileById(@Min(value = 1, message = "Недопустимое значение id") long id);

    int findWaitingSignProtocolsCount(User user);

    List<ProtocolDto> findNotSigningProtocolsByUser(User user);

    List<ProtocolDto> findNotSigningProtocolsByUserAndSearchString(User user, String searchString);

    void update(@Valid ProtocolDto protocolDto);

    void deleteById(@Min(value = 1, message = "Недопустимое значение id") long id);

    void deleteAll(Journal journal);
}
