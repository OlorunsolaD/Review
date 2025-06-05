package com.reviewyme.userservice.existinsetup.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi (){
        return GroupedOpenApi.builder()
                .group("User-service")
                .pathsToMatch("/users/**")
                .build();
    }

    @Bean
    public OpenAPI CustomizedOpenApi (){
        return new OpenAPI()
                .info(new Info()
                        .title("User service API Documentation")
                        .description("API Documentation for User service")
                        .version("2.0"))

                // Defined JWT Security Schemes
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("oauth2"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))

                        // Defined oauth2 Security Schemes
                                .addSecuritySchemes("oauth2", new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("https://auth-server.com/oauth/authorize")
                                                        .tokenUrl("https://auth-server.com/oauth/token")
                                                        .scopes(new Scopes()
                                                                .addString("read","Grants read access")
                                                                .addString("write", "Grants write access"))))));



    }
}