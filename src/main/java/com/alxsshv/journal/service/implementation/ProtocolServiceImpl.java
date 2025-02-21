package com.alxsshv.journal.service.implementation;

import com.alxsshv.config.PathsConfig;
import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.exceptions.ProtocolStorageException;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.journal.repository.ProtocolRepository;
import com.alxsshv.journal.service.interfaces.ProtocolService;
import com.alxsshv.journal.utils.PathResolver;
import com.alxsshv.security.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class ProtocolServiceImpl implements ProtocolService {
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private PathsConfig pathsConfig;
    @Autowired
    private ProtocolRepository protocolRepository;
    private ModelMapper mapper = new ModelMapper();

    @Transactional
    @Override
    public void upload(MultipartFile file, ProtocolDto protocolDto, Journal journal) throws IOException {
        final String filename = file.getOriginalFilename();
        final String extension = filename.substring(filename.lastIndexOf(".") + 1);
        final String storageFilename = UUID.randomUUID() + "." + filename;
        pathResolver.createFilePathIfNotExist(pathsConfig.getOriginProtocolsPath());
        final Protocol protocol = mapper.map(protocolDto, Protocol.class);
        protocol.setJournal(journal);
        protocol.setOriginalFilename(filename);
        protocol.setExtension(extension);
        protocol.setStorageFileName(storageFilename);
        protocol.setAwaitingSigning(true);
        protocol.setSigned(false);
        protocol.setVerificationDate(LocalDate.parse(protocolDto.getVerificationDate()));
        file.transferTo(new File(pathsConfig.getOriginProtocolsPath() + "/" + storageFilename));
        protocolRepository.save(protocol);
        log.info("Протокол поверки \"{}\" успешно загружен на сервер", filename);
    }

    @Override
    public Page<ProtocolDto> findProtocols(Pageable pageable) {
        return protocolRepository.findAll(pageable)
                .map(protocol -> mapper.map(protocol, ProtocolDto.class));
    }

    @Override
    public Page<ProtocolDto> findProtocolsByJournal(Journal journal, Pageable pageable) {
        return protocolRepository.findByJournal(journal, pageable)
                .map(protocol -> mapper.map(protocol, ProtocolDto.class));
    }

    @Override
    public Page<ProtocolDto> findProtocolByJournalAndSearchString(Journal journal, String searchString, Pageable pageable) {
        return protocolRepository.findByJournalAndNumberIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(journal, searchString, searchString, pageable)
                .map(protocol -> mapper.map(protocol, ProtocolDto.class));
    }

    @Override
    public Protocol getProtocolById(long id) {
        final Optional<Protocol> protocolOpt = protocolRepository.findById(id);
        if (protocolOpt.isEmpty()) {
            throw new EntityNotFoundException("Протокол поверки не найден");
        }
        return protocolOpt.get();
    }

    @Override
    public ResponseEntity<?> getProtocolFileById(long id) {
        final Protocol protocol = getProtocolById(id);
        try {
            return buildResponseEntity(protocol);
        } catch (IOException ex) {
            log.error("Запрашиваемый файл не найден или повержден");
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/file_not_found");
            return new ResponseEntity<>(null, headers, HttpStatus.FOUND);
        }
    }

    @Override
    public int findWaitingSignProtocolsCount(User user) {
        return protocolRepository.countBySignedAndVerificationEmployee(false, user);
    }

    @Override
    public List<ProtocolDto> findNotSigningProtocolsByUser(User user) {
        final List<Protocol> protocols = protocolRepository.findBySignedAndVerificationEmployee(false, user);
        return protocols.stream().map(protocol -> mapper.map(protocol, ProtocolDto.class)).toList();
    }

    @Override
    public List<ProtocolDto> findNotSigningProtocolsByUserAndSearchString(User user, String searchString) {
        final List<Protocol> protocols = protocolRepository
                .findNotSignedProtocolsByUserAndSearchSting(user.getId(), searchString);
        return protocols.stream().map(protocol -> mapper.map(protocol, ProtocolDto.class)).toList();
    }

    private ResponseEntity<?> buildResponseEntity(Protocol protocol) throws IOException {
        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(protocol.getOriginalFilename(), StandardCharsets.UTF_8).build();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(contentDisposition);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(Files.readAllBytes(Path
                        .of(resolveFilePath(protocol)))));
    }

    private String resolveFilePath(Protocol protocol) {
        if (protocol.isSigned()) {
            return pathsConfig.getSignedProtocolsPath() + "/" + protocol.getSignedFileName();
        }
        return pathsConfig.getOriginProtocolsPath() + "/" + protocol.getStorageFileName();
    }

    @Transactional
    @Override
    public void update(ProtocolDto protocolDto) {
        final Protocol protocol = getProtocolById(protocolDto.getId());
        final Protocol updateData = mapper.map(protocolDto, Protocol.class);
        protocol.updateFrom(updateData);
        protocolRepository.save(protocol);
    }

    @Override
    public void deleteById(long id) {
        final Protocol protocol = getProtocolById(id);
        try {
            Files.deleteIfExists(Path.of(pathsConfig.getOriginProtocolsPath() + "/" + protocol.getStorageFileName()));
            Files.deleteIfExists(Path.of(pathsConfig.getSignedProtocolsPath() + "/" + protocol.getSignedFileName()));
            protocolRepository.deleteById(id);
        } catch (IOException ex) {
            final String errorMessage = "Ошибка удаления файла.";
            log.info("{}:{}", errorMessage, ex.getMessage());
            throw new ProtocolStorageException(errorMessage);
        }
    }

    @Override
    public void deleteAll(Journal journal) {
        final List<Protocol> protocols = protocolRepository.findByJournal(journal);
        for (Protocol protocol : protocols) {
            deleteById(protocol.getId());
        }
    }
}
