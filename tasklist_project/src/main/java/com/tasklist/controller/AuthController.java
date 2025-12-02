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
    public String registerUser(@ModelAttribute Usuario usuario, 
                               @RequestParam(required = false) String planSolicitado,
                               Model model) {
        
        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // 1. Guardar el Plan Solicitado
        usuario.setPlanSolicitado(planSolicitado != null ? planSolicitado : "Basico");
        
        // 2. Dejar usuario INACTIVO (Pendiente de aprobación)
        usuario.setEnabled(false); 

        // 3. Asignar Rol (Si no eligió, por defecto Estudiante)
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE"); 
        }

        usuarioRepository.save(usuario);
        
        // Redirigir con mensaje de "Pendiente"
        return "redirect:/login?pending=true";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}