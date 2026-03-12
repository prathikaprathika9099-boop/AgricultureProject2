package com.example.AgriProject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    @Value("${app.backend.url)")
    private String backendUrl;

    public String getBackendUrl(){
        return backendUrl;
    }
}
