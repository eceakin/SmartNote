package com.eceakin.noteapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.eceakin.noteapp.application.manager", "com.eceakin.noteapp.application.service"})
public class ServiceConfiguration {
    // This configuration ensures proper scanning of manager components
    // and their automatic wiring with service interfaces
}