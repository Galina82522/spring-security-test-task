package ru.codeinside.springsecuritytesttask.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class WebAuthorizationConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/configuration/security", "/swagger-ui.html", "/webjars/**",
                        "/csrf", "/")
                .permitAll()
                .antMatchers("/oauth/token**", "/oauth/check_token**")
                .permitAll()
                .antMatchers("/user/registration")
                .permitAll()
                .anyRequest().authenticated();
    }
}
