package com.priyankdevurkar.device_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceApiDocs()  {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Device Service API")
                        .description("Device Service API for Smart Power Monitor")
                        .version("1.0.0"));
    }
}
