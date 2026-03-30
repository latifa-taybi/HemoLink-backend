package com.example.hemolinkbackend.config;

import com.example.hemolinkbackend.security.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/centres-collecte/**", "/api/hopitaux/**").permitAll()
                        .requestMatchers("/api/centres-collecte/**", "/api/hopitaux/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/commandes-sang/**").hasRole("PERSONNEL_HOPITAL")
                        .requestMatchers(HttpMethod.PATCH, "/api/commandes-sang/**").hasRole("TECHNICIEN_LABO")
                        .requestMatchers(HttpMethod.GET, "/api/commandes-sang/**").hasAnyRole("PERSONNEL_HOPITAL", "TECHNICIEN_LABO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/dons/**").hasRole("PERSONNEL_CENTRE")
                        .requestMatchers(HttpMethod.GET, "/api/dons/**").hasAnyRole("PERSONNEL_CENTRE", "TECHNICIEN_LABO", "ADMIN")
                        .requestMatchers("/api/tests-labo/**").hasRole("TECHNICIEN_LABO")
                        .requestMatchers("/api/poches-sang/**").hasAnyRole("TECHNICIEN_LABO", "ADMIN")
                        .requestMatchers("/api/statistiques/**").hasAnyRole("TECHNICIEN_LABO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/rendez-vous/**").hasAnyRole("DONNEUR", "PERSONNEL_CENTRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendez-vous/**").hasRole("PERSONNEL_CENTRE")
                        .requestMatchers(HttpMethod.GET, "/api/rendez-vous/**").hasAnyRole("DONNEUR", "PERSONNEL_CENTRE", "TECHNICIEN_LABO", "ADMIN")
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

