package ru.codeinside.springsecuritytesttask.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.codeinside.springsecuritytesttask.oauth2.Scopes;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket apiCommon() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(basicAuth(), securitySchema()));
    }

    private OAuth securitySchema() {
        List<AuthorizationScope> authorizationScopeList = Stream.of(Scopes.DEFAULT_CLIENT)
                .map(s -> new AuthorizationScope(s, ""))
                .collect(Collectors.toList());
        ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant("/oauth/token");
        List<GrantType> grantTypes = Collections.singletonList(resourceOwnerPasswordCredentialsGrant);
        return new OAuth("OAuth", authorizationScopeList, grantTypes);
    }

    private BasicAuth basicAuth() {
        return new BasicAuth("Basic");
    }
}
