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

    @Autowired
    private JwtAuthenticationFilter JwtAuthenticationFilter;

    @Autowired
    private UtilityService utilityService;

    private static final String[] PERMIT_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/h2/**",
            "/api/v1/user/**",
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
                        .requestMatchers(utilityService.getAntMathers(PERMIT_PATHS)).permitAll()
                        //.requestMatchers(new AntPathRequestMatcher("/h2/**"), new AntPathRequestMatcher("/api/v1/user/**")).permitAll() //Spring security 6.0.3 --> requestMatchers not working with /h2 path
                        .requestMatchers("/api/v1/management/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
/*                      .requestMatchers("/api/user").access(AuthorizationManagers.anyOf(AuthorityAuthorizationManager.hasRole("ADMIN"), AuthorityAuthorizationManager.hasRole("USER")))
                        .requestMatchers("/api/super").access(AuthorizationManagers.allOf(AuthorityAuthorizationManager.hasRole("ADMIN"), AuthorityAuthorizationManager.hasRole("SUPER_ADMIN")))
                        .requestMatchers("/api/admin").hasRole("ADMIN")*/
                        .anyRequest().denyAll())
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));
                http.addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                //.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
