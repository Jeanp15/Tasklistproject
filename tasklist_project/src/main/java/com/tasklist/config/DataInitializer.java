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
            
            // 1. Usuario ADMINISTRADOR
            if (usuarioRepository.findByUsername("admin") == null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin")); // Contraseña: admin
                admin.setEmail("admin@tasklist.com");
                admin.setRol("ADMIN");
                admin.setPlanSolicitado("Empresarial"); // Plan por defecto para admin
                admin.setEnabled(true); // ACTIVADO
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: admin / admin");
            }

            // 2. Usuario ESTUDIANTE
            if (usuarioRepository.findByUsername("estudiante") == null) {
                Usuario student = new Usuario();
                student.setUsername("estudiante");
                student.setPassword(passwordEncoder.encode("1234")); // Contraseña: 1234
                student.setEmail("estudiante@tasklist.com");
                student.setRol("ESTUDIANTE");
                
                // CORRECCIÓN AQUÍ: Usamos 'student', no 'admin'
                student.setPlanSolicitado("Basico"); 
                
                student.setEnabled(true); // ACTIVADO
                usuarioRepository.save(student);
                System.out.println(">>> Usuario ESTUDIANTE creado: estudiante / 1234");
            }

            // 3. Usuario PROFESIONAL
            if (usuarioRepository.findByUsername("profesional") == null) {
                Usuario pro = new Usuario();
                pro.setUsername("profesional");
                pro.setPassword(passwordEncoder.encode("1234")); // Contraseña: 1234
                pro.setEmail("pro@tasklist.com");
                pro.setRol("PROFESIONAL");
                
                // CORRECCIÓN AQUÍ: Asignamos plan al profesional también
                pro.setPlanSolicitado("Profesional");
                
                pro.setEnabled(true); // ACTIVADO
                usuarioRepository.save(pro);
                System.out.println(">>> Usuario PROFESIONAL creado: profesional / 1234");
            }
        };
    }
}