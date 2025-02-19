package com.alxsshv.security.service.implementation;

import com.alxsshv.security.service.interfaces.LoggerService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class LoggerServiceImpl implements LoggerService {

    @Value("${logging.file.name}")
    private String logFileName;

    @Override
    public ResponseEntity<?> getApplicationLog() throws IOException {
        try {
            final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename("logs.txt", StandardCharsets.UTF_8).build();
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(Files.readAllBytes(Path.of(logFileName))));
        } catch (IOException ex) {
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/file_not_found");
            return new ResponseEntity<>(null, headers, HttpStatus.FOUND);
        }
    }
}
