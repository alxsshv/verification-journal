package com.alxsshv.journal.exceptions;

import com.alxsshv.journal.utils.ServiceMessage;
import com.alxsshv.security.exception.UserOperationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchProtocolStorageException(ProtocolStorageException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(500).body(new ServiceMessage(ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchIoException(IOException ex) {
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

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchConstrainViolationExceptionException(ConstraintViolationException ex) {
        log.error(ex.getMessage());
        final String errorMessage = ex.getMessage().split(":")[1];
        return ResponseEntity.status(400).body(new ServiceMessage(errorMessage));
    }

    @ExceptionHandler
    public ResponseEntity<ServiceMessage> catchUserOperationException(UserOperationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(new ServiceMessage(ex.getMessage()));
    }
}
