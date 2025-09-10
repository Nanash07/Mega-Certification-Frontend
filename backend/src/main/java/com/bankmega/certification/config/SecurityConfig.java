package com.bankmega.certification.config;

import com.bankmega.certification.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // ⬇️ penting: aktifkan CORS di security
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ⬇️ preflight harus lolos
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .requestMatchers("/api/auth/**").permitAll()

                // USERS
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("SUPERADMIN","PIC") // atau authenticated()
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/users/**").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("SUPERADMIN")

                // ROLES
                .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAnyRole("SUPERADMIN","PIC")
                .requestMatchers(HttpMethod.POST, "/api/roles").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/roles/**").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/roles/**").hasRole("SUPERADMIN")

                .requestMatchers(HttpMethod.GET, "/api/certifications/**").hasAnyRole("SUPERADMIN","PIC")
                .requestMatchers(HttpMethod.POST, "/api/certifications").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/certifications/**").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/certifications/**").hasRole("SUPERADMIN")
                
                // PIC Certification (punya lo)
                .requestMatchers(HttpMethod.POST,   "/api/pic-scopes").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/pic-scopes/**").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pic-scopes/**").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/pic-scopes/user/**").hasAnyRole("SUPERADMIN","PIC")
                .requestMatchers(HttpMethod.GET,    "/api/pic-scopes").hasRole("SUPERADMIN")


                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ⬇️ CORS untuk origin Vite (5173)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
         c.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "https://2e9bdf373ecb.ngrok-free.app",   // BE via ngrok
            "https://mega-certification-frontend.vercel.app" // FE di Vercel
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        c.addAllowedHeader("*"); // biar semua header lolos
        c.setExposedHeaders(List.of("Location"));
        c.setAllowCredentials(true); // kalau lo mau cookie/JWT di header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
