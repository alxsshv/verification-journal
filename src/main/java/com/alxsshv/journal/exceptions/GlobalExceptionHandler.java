package com.alxsshv.journal.exceptions;

import com.alxsshv.journal.utils.ServiceMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchProtocolStorageException(ProtocolStorageException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(500).body(new ServiceMessage(ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchIOException(IOException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(500).body(new ServiceMessage("Ошибка доступа к хранилищу файлов"));
    }

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(new ServiceMessage(ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchOperationNotSupportedException(OperationNotSupportedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(new ServiceMessage(ex.getMessage()));
    }
}
