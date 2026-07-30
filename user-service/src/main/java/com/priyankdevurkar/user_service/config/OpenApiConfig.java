package com.priyankdevurkar.user_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceApiDocs()  {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("User Service API")
                        .description("User Service API for Smart Power Monitor")
                        .version("1.0.0"));
    }
}
