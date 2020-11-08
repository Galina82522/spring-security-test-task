package ru.codeinside.springsecuritytesttask.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/configuration/security", "/swagger-ui.html", "/webjars/**",
                        "/csrf", "/")
                .permitAll()
                .antMatchers("/oauth/token**", "/oauth/check_token**").permitAll()
                .antMatchers("/user/registration")
                .permitAll()
                .anyRequest().authenticated();
    }
}
