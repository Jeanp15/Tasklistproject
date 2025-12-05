package com.tasklist.controller;

import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; // <--- Importante
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // <--- Importante
import org.springframework.web.bind.annotation.RequestParam; // <--- Importante
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <--- Importante

@Controller
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // <--- Inyectamos el encriptador

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName());
        model.addAttribute("usuario", usuario);
        return "profile";
    }

    // --- NUEVO MÉTODO PARA CAMBIAR CONTRASEÑA ---
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        // 1. Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName());

        // 2. Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/profile";
        }

        // 3. Verificar que la nueva contraseña y la confirmación coincidan
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden.");
            return "redirect:/profile";
        }

        // 4. Guardar la nueva contraseña encriptada
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        return "redirect:/profile";
    }
}