package com.tasklist.controller;

import com.tasklist.config.JwtUtil;
import com.tasklist.model.Usuario;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Endpoint para Login (Obtener Token)
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Usuario o contraseña incorrectos", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    // 2. Endpoint de Registro para API (Postman)
    // Crea usuarios y los ACTIVA automáticamente (enabled = true)
    @PostMapping("/register")
    public ResponseEntity<?> registerAPI(@RequestBody Usuario usuario) {
        
        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Error: El usuario ya existe");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // ACTIVAR AUTOMÁTICAMENTE para poder usarlo ya mismo
        usuario.setEnabled(true); 
        
        // Asignar Rol por defecto si no viene en el JSON
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ESTUDIANTE"); 
        }
        
        // Asignar Plan por defecto si no viene
        if (usuario.getPlanSolicitado() == null) {
            usuario.setPlanSolicitado("Basico");
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado y activado. Usa /api/auth/login para obtener tu token.");
    }
}

// DTOs auxiliares
class LoginRequest {
    private String username;
    private String password;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class LoginResponse {
    private String token;
    public LoginResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}