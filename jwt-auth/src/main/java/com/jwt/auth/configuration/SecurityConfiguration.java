package com.jwt.auth.configuration;

import com.jwt.auth.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private JwtAuthenticationFilter JwtAuthenticationFilter;
    private UtilityService utilityService;

    @Autowired
    public SecurityConfiguration(com.jwt.auth.configuration.JwtAuthenticationFilter jwtAuthenticationFilter, UtilityService utilityService) {
        JwtAuthenticationFilter = jwtAuthenticationFilter;
        this.utilityService = utilityService;
    }

    private static final String[] PERMIT_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/h2/**",
            "/api/v1/user/**",
            "/actuator/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        //Sequence of Matchers is important; it requires checking privilege before role.
                        .requestMatchers(GET, "/api/v1/management/**").hasAuthority("READ")
                        .requestMatchers(POST, "/api/v1/management/**").hasAuthority("WRITE")
                        .requestMatchers(DELETE, "/api/v1/management/**").hasAuthority("DELETE")
                        .requestMatchers("/actuator/health").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/actuator/beans").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/actuator/env").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers(utilityService.getAntMathers(PERMIT_PATHS)).permitAll()
                        .anyRequest().denyAll())
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));
        http.addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
