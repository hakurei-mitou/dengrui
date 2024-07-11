package com.mitou.dengrui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationContext context;

    public void closeApplication() {
        SpringApplication.exit(context);
    }
}
