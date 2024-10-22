package com.sparta.gathering.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API")
                        .version("0.1")
                        .description("API 명세서")
                        .contact(new Contact()
                                .name("Team Github Repository")
                                .url("https://github.com/eunhyeong99/final_project")
                        )
                );
    }

}
