package com.amikhaylov.mysimplereminder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mainmenu")
@Data
public class PropertiesConfig {
    private List<String> commands;
    private Map<String, String> descriptions;
}
