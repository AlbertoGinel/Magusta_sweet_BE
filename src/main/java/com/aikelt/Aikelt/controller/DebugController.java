package com.aikelt.Aikelt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {
    @Value("${SPRING_DATASOURCE_URL}")
    private String datasourceUrl;

    @GetMapping("/datasource-url")
    public String getDatasourceUrl() {
        return "Database URL: " + datasourceUrl;
    }

    @GetMapping("/ping")
    public String ping() {
        return "PING";
    }
}