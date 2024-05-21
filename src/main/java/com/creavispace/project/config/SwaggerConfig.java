package com.creavispace.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .info(new Info()
            .title("Creavi 프로젝트 API")
            .description("프로젝트를 공유하고, 프로젝트/스터디 인원을 모집하는 기능을 제공합니다.")
            .version("1.0.0"));
    }
}
