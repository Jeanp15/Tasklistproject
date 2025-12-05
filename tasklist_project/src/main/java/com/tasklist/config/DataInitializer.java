package com.tasklist.config;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            
            // 1. Usuario ADMINISTRADOR (Este sí lo dejamos para poder gestionar el sistema)
            if (usuarioRepository.findByUsername("admin") == null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin")); // Pass: admin
                admin.setEmail("admin@tasklist.com");
                admin.setRol("ADMIN");
                admin.setPlanSolicitado("Empresarial");
                admin.setEnabled(true); // El admin nace activado
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: admin / admin");
            }

        };
    }
}