package com.sammy.codetest;

import com.sammy.codetest.config.CodeTestConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CodeTestConfigurationProperties.class)
public class CodeTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeTestApplication.class, args);
    }

}
