package com.sola.reviewyme.resume_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication

public class ResumeAIApplication {

    public static void main(String[] args) {SpringApplication.run(ResumeAIApplication.class,args);}
    }

//scanBasePackages = {"com.Sola.user_service.service.UserService"},
//exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})