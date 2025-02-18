package com.alxsshv.security.controller;

import com.alxsshv.security.service.interfaces.LoggerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LoggerController {
    @Autowired
    private LoggerService loggerService;

    @GetMapping
    public ResponseEntity<?> getLogFile() throws IOException {
        return loggerService.getApplicationLog();
    }
}
