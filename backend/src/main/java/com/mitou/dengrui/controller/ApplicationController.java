package com.mitou.dengrui.controller;

import com.mitou.dengrui.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

//    @PostMapping("/quit")
    public void quitApplication() {
        applicationService.closeApplication();
    }

}
