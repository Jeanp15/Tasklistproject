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
            
            // 1. Usuario ADMINISTRADOR (El que tiene el control total)
            if (usuarioRepository.findByUsername("admin") == null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña: admin123
                admin.setEmail("admin@tasklist.com");
                admin.setRol("ADMIN");
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: admin / admin123");
            }

            // 2. Usuario ESTUDIANTE (Para la demo de funcionalidades académicas)
            if (usuarioRepository.findByUsername("estudiante") == null) {
                Usuario student = new Usuario();
                student.setUsername("estudiante");
                student.setPassword(passwordEncoder.encode("1234")); // Contraseña: 1234
                student.setEmail("estudiante@tasklist.com");
                student.setRol("ESTUDIANTE");
                usuarioRepository.save(student);
                System.out.println(">>> Usuario ESTUDIANTE creado: estudiante / 1234");
            }

            // 3. Usuario PROFESIONAL (Para la demo de freelancers)
            if (usuarioRepository.findByUsername("profesional") == null) {
                Usuario pro = new Usuario();
                pro.setUsername("profesional");
                pro.setPassword(passwordEncoder.encode("1234")); // Contraseña: 1234
                pro.setEmail("pro@tasklist.com");
                pro.setRol("PROFESIONAL");
                usuarioRepository.save(pro);
                System.out.println(">>> Usuario PROFESIONAL creado: profesional / 1234");
            }
            
            // NOTA: Los "GRUPOS" no son usuarios, son la entidad 'Team' (Equipos).
            // Estos usuarios (Estudiante/Profesional) podrán crear o unirse a Equipos en el Dashboard.
        };
    }
}