package com.tasklist.controller;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin-users";
    }

    // --- MÉTODO NUEVO: APROBAR ---
    @PostMapping("/users/approve/{id}")
    public String approveUser(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setEnabled(true); // Activar cuenta
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}