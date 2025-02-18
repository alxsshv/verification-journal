package com.alxsshv.security.service.interfaces;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface LoggerService {

    ResponseEntity<?> getApplicationLog() throws IOException;
}
