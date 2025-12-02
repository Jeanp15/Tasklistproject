package com.tasklist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Importante
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter; // Inyectamos nuestro filtro

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para permitir peticiones POST desde Postman (para la API)
            // Ojo: En producción real se configuraría diferente, pero para este proyecto académico está bien.
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests((requests) -> requests
                // Permitir login web y login API
                .requestMatchers("/", "/index", "/register", "/login", "/api/auth/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Todas las demás requieren autenticación
                .anyRequest().authenticated()
            )
            // Configuración Híbrida:
            // Usamos SessionCreationPolicy.IF_REQUIRED (por defecto) para que Thymeleaf siga funcionando con sesiones.
            // El JWT Filter se encargará de las peticiones sin sesión (API).
            
            .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        // AÑADIR EL FILTRO JWT ANTES DEL FILTRO DE USUARIO/PASSWORD
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}