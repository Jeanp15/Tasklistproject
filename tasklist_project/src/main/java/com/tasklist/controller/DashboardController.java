package com.tasklist.controller;

import com.tasklist.model.Task;
import com.tasklist.model.Usuario;
import com.tasklist.repository.TaskRepository;
import com.tasklist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Obtener usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName());

        // 2. Obtener SOLO las tareas de este usuario
        List<Task> allTasks = taskRepository.findByUsuario(usuario);
        
        // 3. Calcular Contadores (Estadísticas)
        long pendingCount = allTasks.stream().filter(t -> t.getStatus() == Task.Status.Pendiente).count();
        long inProgressCount = allTasks.stream().filter(t -> t.getStatus() == Task.Status.En_Progreso).count();
        long completedCount = allTasks.stream().filter(t -> t.getStatus() == Task.Status.Completada).count();
        long expiredCount = allTasks.stream().filter(Task::isVencida).count();
        
        // 4. Tareas Recientes
        List<Task> recentTasks = allTasks.stream()
            .filter(t -> t.getStatus() != Task.Status.Completada)
            .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(LocalDate::compareTo)))
            .limit(4)
            .collect(Collectors.toList());

        // 5. Notificaciones
        List<Task> notifications = allTasks.stream()
            .filter(t -> t.isVencida() || t.getStatus() == Task.Status.Pendiente)
            .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(LocalDate::compareTo)))
            .limit(5)
            .collect(Collectors.toList());

        // Enviar datos a la vista
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("expiredCount", expiredCount);
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", notifications.size());

        return "dashboard";
    }
}