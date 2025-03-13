package com.alxsshv.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "paths")
public class PathsConfig {
    private String originProtocolsPath;
    private String signedProtocolsPath;
    private String fontPath;
}