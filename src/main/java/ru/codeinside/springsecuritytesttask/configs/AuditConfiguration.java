package ru.codeinside.springsecuritytesttask.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.codeinside.springsecuritytesttask.models.UserDetailsImpl;

import java.util.Objects;
import java.util.Optional;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(authentication) || Objects.isNull(authentication.getPrincipal())) {
                return Optional.empty();
            }
            final Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                return Optional.of(((UserDetailsImpl) principal).getUsername());
            } else if (principal instanceof String) {
                return Optional.of((String) principal);
            } else {
                return Optional.empty();
            }
        };
    }
}