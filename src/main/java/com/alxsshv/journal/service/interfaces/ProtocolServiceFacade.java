package com.alxsshv.journal.service.interfaces;

import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.dto.ProtocolFileInfo;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.security.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProtocolServiceFacade {

    void upload(MultipartFile file, ProtocolFileInfo protocolFileInfo) throws IOException;

    Page<ProtocolDto> findProtocolsByJournal(long journalId, Pageable pageable);

    Page<ProtocolDto> findJournalProtocolsBySearchString(long journalId, String searchString, Pageable pageable);

    Page<ProtocolDto> findProtocols(Pageable pageable);

    int findWaitingSignProtocolsCount(User user);

    List<ProtocolDto> findNotSigningProtocolsByUser(User user);

    List<ProtocolDto> findNotSigningProtocolsByUserAndSearchString(User user, String searchString);

    Protocol getProtocolById(long id);

    ResponseEntity<?> getProtocolFile(long id);

    void delete(long id) throws IOException;

    void deleteAll(long journalId) throws IOException;
}

