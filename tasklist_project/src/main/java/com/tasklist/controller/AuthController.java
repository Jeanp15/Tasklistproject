package com.tasklist.controller;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, Model model) {
        // 1. Validar si el usuario ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }

        // 2. ENCRIPTAR CONTRASEÑA (Obligatorio)
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // 3. VALIDAR EL ROL
        // Como agregaste el <select> en el HTML, el objeto 'usuario' ya trae el rol elegido (ESTUDIANTE o PROFESIONAL).
        // Solo por seguridad, verificamos si viene vacío para asignar uno por defecto.
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE"); // Rol por defecto si algo falla
        }

        // Ya no forzamos "USER", ahora guardamos lo que el usuario eligió (o el defecto)
        usuarioRepository.save(usuario);
        
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}