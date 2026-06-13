package com.PPOOII.Laboratorio.Config;

import com.PPOOII.Laboratorio.Config.Model.Constans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JWTAuthorizationFilter jwtAuthorizationFilter;
    private final ApiKeyAuthorizationFilter apiKeyAuthorizationFilter;

    public WebSecurityConfig(
        JWTAuthorizationFilter jwtAuthorizationFilter,
        ApiKeyAuthorizationFilter apiKeyAuthorizationFilter
    ) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.apiKeyAuthorizationFilter = apiKeyAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(Constans.LOGIN_URL).permitAll()
                .requestMatchers(HttpMethod.GET, "/LaboratorioV1/coordenadas").permitAll()
                .requestMatchers("/api/excel/**").permitAll()
                .requestMatchers("/api/demo/**").permitAll()
                .requestMatchers("/LaboratorioV1/public/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(apiKeyAuthorizationFilter, JWTAuthorizationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "APIKey", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization", "APIKey"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
