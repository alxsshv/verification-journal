package com.alxsshv.journal.controller;

import com.alxsshv.config.AppConstants;
import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.dto.ProtocolFileInfo;
import com.alxsshv.journal.service.interfaces.DigitalSignatureService;
import com.alxsshv.journal.service.interfaces.ProtocolServiceFacade;
import com.alxsshv.journal.utils.ServiceMessage;
import com.alxsshv.security.model.User;
import com.alxsshv.security.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/journals/protocols")
@Slf4j
@AllArgsConstructor
public class ProtocolController {
    @Autowired
    private ProtocolServiceFacade protocolServiceFacade;
    @Autowired
    private DigitalSignatureService digitalSignatureService;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @PostMapping("/form")
    public ResponseEntity<?> addProtocol(
            @RequestParam(value = "files") MultipartFile file,
            @RequestParam(value = "protocolInfo") String fileInfo) {
        final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        final ProtocolFileInfo protocolFileInfo = mapper.readValue(fileInfo, ProtocolFileInfo.class);
        protocolServiceFacade.upload(file, protocolFileInfo);
        final String successMessage = "Протокол поверки  №" + protocolFileInfo.getNumber() + " добавлен";
        log.info(successMessage);
        return ResponseEntity.status(201).body(new ServiceMessage(successMessage));
    }

    @GetMapping("/pages")
    public Page<ProtocolDto> getAllProtocolsForJournal(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "dir", defaultValue = AppConstants.DEFAULT_PAGE_SORT_DIR) String pageDir,
            @RequestParam(value = "search") String searchString,
            @RequestParam(value = "journal") long journalId) {
        final Sort sort = Sort.by(Sort.Direction.valueOf(pageDir.toUpperCase()), "number");
        final Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        if (searchString.isEmpty() || "undefined".equals(searchString)) {
            return protocolServiceFacade.findProtocolsByJournal(journalId, pageable);
        }
        return protocolServiceFacade.findJournalProtocolsBySearchString(journalId, searchString, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProtocolFile(@PathVariable("id") long id) {
        return protocolServiceFacade.getProtocolFile(id);
    }

    @GetMapping("/signing/{id}")
    @SneakyThrows
    public ResponseEntity<?> signingProtocolFile(@PathVariable("id") long id, Principal principal) {
        final User currentUser = (User) userService.loadUserByUsername(principal.getName());
        digitalSignatureService.setUserStamp(id, currentUser);
        final String okMessage = "Протокол подписан пользователем " + currentUser.getSurname() + " " + currentUser.getName();
        log.info(okMessage);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }

    @GetMapping("/signing/wait/count")
    public int getWaitingSignProtocolsCount(Principal principal) {
        final User user = (User) userService.loadUserByUsername(principal.getName());
        return protocolServiceFacade.findWaitingSignProtocolsCount(user);
    }

    @GetMapping("/signing/wait")
    public List<ProtocolDto> getWaitingSigingProtocol(
            @RequestParam(value = "search", defaultValue = "") String searchString, Principal principal) {
        final User user = (User) userService.loadUserByUsername(principal.getName());
        if (searchString.isEmpty() || "undefined".equals(searchString)) {
            return protocolServiceFacade.findNotSigningProtocolsByUser(user);
        }
        return protocolServiceFacade.findNotSigningProtocolsByUserAndSearchString(user, searchString);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProtocolById(@PathVariable("id") long id) {
        protocolServiceFacade.delete(id);
        final String okMessage = "Протокол удален";
        log.info("{} id= {}", okMessage, id);
        return ResponseEntity.ok(new ServiceMessage(okMessage));
    }
}
