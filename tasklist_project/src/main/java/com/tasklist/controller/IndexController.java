package com.tasklist.controller;

// 1. IMPORTA LO NUEVO
import com.tasklist.model.MensajeContacto;
import com.tasklist.repository.MensajeContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Importar
import org.springframework.web.bind.annotation.RequestParam; // Importar

@Controller
public class IndexController {

    // 2. INYECTA EL NUEVO REPOSITORIO
    @Autowired
    private MensajeContactoRepository mensajeRepository;

    @GetMapping({"/", "/index"})
    public String index() {
        return "index"; // mostrará templates/index.html
    }

    // 3. AÑADE ESTE NUEVO MÉTODO
    @PostMapping("/submit-contact")
    public String handleContactForm(
            @RequestParam("name") String nombre,
            @RequestParam("email") String email,
            @RequestParam("message") String mensaje) {
        
        // Crea el nuevo objeto
        MensajeContacto nuevoMensaje = new MensajeContacto();
        nuevoMensaje.setNombre(nombre);
        nuevoMensaje.setEmail(email);
        nuevoMensaje.setMensaje(mensaje);
        
        // Guárdalo en la BD
        mensajeRepository.save(nuevoMensaje);
        
        // Redirige al usuario de vuelta a la página principal
        // Opcional: puedes añadir "?success=true" para mostrar un mensaje
        return "redirect:/index#contact"; 
    }
}