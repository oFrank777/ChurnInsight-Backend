package com.alura.churninsight.Security;

import com.alura.churninsight.domain.Usuario.DatosAutenticacion;
import com.alura.churninsight.domain.Usuario.DatosTokenJWT;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer customer() {
        return openApi -> {
            if (openApi.getExtensions() != null) {
                openApi.getExtensions().remove("x-java-record-property-order- DatosHistorialPrediccion");
                openApi.getExtensions().remove("x-java-record-property-order-DatosHistorialPrediccion");
            }
        };
    }
}
