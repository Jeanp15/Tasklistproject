package com.tasklist.controller;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    // Guardar nuevo usuario
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, Model model) {
        // Validar si el nombre ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    // Mostrar formulario de inicio de sesión
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Procesar inicio de sesión
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        Model model) {

        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return "redirect:/dashboard"; // redirige al dashboard después de iniciar sesión
        }

        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }
}

