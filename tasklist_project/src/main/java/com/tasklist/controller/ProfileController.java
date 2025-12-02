package com.tasklist.controller;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // 1. Obtener quién está logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2. Buscar sus datos completos en la BD
        Usuario usuario = usuarioRepository.findByUsername(username);

        // 3. Enviarlos a la vista
        model.addAttribute("usuario", usuario);
        
        return "profile"; // Llama a profile.html
    }
}