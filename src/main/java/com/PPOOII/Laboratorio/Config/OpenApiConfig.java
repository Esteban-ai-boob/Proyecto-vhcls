package com.PPOOII.Laboratorio.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI laboratorioOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Laboratorio 4 API")
                .version("3.0")
                .description("API para gestion de vehiculos, documentos, conductores y rutas"))
            .schemaRequirement(
                "bearerAuth",
                new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
            .schemaRequirement(
                "apiKeyAuth",
                new SecurityScheme()
                    .name("APIKey")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
            )
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("apiKeyAuth"));
    }
}
