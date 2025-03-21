package com.alxsshv.journal.service.implementation;

import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.dto.ProtocolFileInfo;
import com.alxsshv.journal.dto.mapper.ProtocolFileInfoToProtocolDtoMapper;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.journal.service.interfaces.JournalService;
import com.alxsshv.journal.service.interfaces.ProtocolService;
import com.alxsshv.journal.service.interfaces.ProtocolServiceFacade;
import com.alxsshv.security.dto.UserNoPassDto;
import com.alxsshv.security.model.User;
import com.alxsshv.security.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ProtocolServiceFacadeImpl implements ProtocolServiceFacade {
    @Autowired
    private final ProtocolService protocolService;
    @Autowired
    private final JournalService journalService;
    @Autowired
    private final UserService userService;

    @Override
    public void upload(MultipartFile file, ProtocolFileInfo protocolFileInfo) throws IOException {
        final Journal journal = journalService.getById(protocolFileInfo.getJournalId());
        final UserNoPassDto UserNoPassDto = userService.findById(protocolFileInfo.getVerificationEmployeeId());
        final ProtocolDto protocolDto = ProtocolFileInfoToProtocolDtoMapper
                .mapToProtocolDto(protocolFileInfo, UserNoPassDto);
        protocolService.upload(file, protocolDto, journal);
    }

    @Override
    public Page<ProtocolDto> findProtocolsByJournal(long journalId, Pageable pageable) {
        final Journal journal = journalService.getById(journalId);
        return protocolService.findProtocolsByJournal(journal, pageable);
    }

    @Override
    public Page<ProtocolDto> findJournalProtocolsBySearchString(long journalId, String searchString, Pageable pageable) {
        final Journal journal = journalService.getById(journalId);
        return protocolService.findProtocolByJournalAndSearchString(journal, searchString, pageable);
    }

    @Override
    public Page<ProtocolDto> findProtocols(Pageable pageable) {
        return protocolService.findProtocols(pageable);
    }

    @Override
    public int findWaitingSignProtocolsCount(User user) {
        return protocolService.findWaitingSignProtocolsCount(user);
    }

    @Override
    public List<ProtocolDto> findNotSigningProtocolsByUser(User user) {
        return protocolService.findNotSigningProtocolsByUser(user);
    }

    @Override
    public List<ProtocolDto> findNotSigningProtocolsByUserAndSearchString(User user, String searchString) {
        return protocolService.findNotSigningProtocolsByUserAndSearchString(user, searchString);
    }

    @Override
    public Protocol getProtocolById(long id) {
        return protocolService.getProtocolById(id);
    }

    @Override
    public ResponseEntity<?> getProtocolFile(long id) {
        return protocolService.getProtocolFileById(id);
    }

    @Override
    public void delete(long id) {
        protocolService.deleteById(id);
    }

    @Override
    public void deleteAll(long journalId) {
        final Journal journal = journalService.getById(journalId);
        protocolService.deleteAll(journal);
    }

}
