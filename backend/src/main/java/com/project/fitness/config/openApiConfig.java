package com.project.fitness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class openApiConfig {

    @Bean
    public OpenAPI customApi(){
        return new OpenAPI().info(new Info()
                .title("Fitness tracking API")
                .version("v1.0")
                .description("Production garde api documentaion")
                .contact(new Contact().name("Rishabh Jain").email("abc@gmail.com"))
                .license(new License().name("Apache 2.0"))
        );
    }
}
