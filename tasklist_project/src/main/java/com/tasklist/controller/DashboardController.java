package com.tasklist.controller;

import com.tasklist.model.Task;
import com.tasklist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Añadido para inyectar datos a la vista
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private TaskRepository taskRepository; // Inyectar TaskRepository

    @GetMapping("/dashboard")
    public String dashboard(Model model) { // Recibe el objeto Model
        List<Task> allTasks = taskRepository.findAll();
        
        // 1. Cálculo de Contadores
        
        // Cuentas por estado (usando el enum Status de Task.java)
        long pendingCount = allTasks.stream()
            .filter(t -> t.getStatus() == Task.Status.Pendiente)
            .count();

        long inProgressCount = allTasks.stream()
            .filter(t -> t.getStatus() == Task.Status.En_Progreso)
            .count();

        long completedCount = allTasks.stream()
            .filter(t -> t.getStatus() == Task.Status.Completada)
            .count();
        
        // Cuenta de Tareas Vencidas (usando el método isVencida del modelo Task)
        long expiredCount = allTasks.stream()
            .filter(Task::isVencida)
            .count();
        
        // 2. Tareas Recientes (5 tareas no completadas, ordenadas por fecha de vencimiento)
        List<Task> recentTasks = allTasks.stream()
            .filter(t -> t.getStatus() != Task.Status.Completada)
            // Ordenar por fecha de vencimiento, dejando las nulas al final
            .sorted(Comparator.comparing(Task::getDueDate, 
                    Comparator.nullsLast(LocalDate::compareTo)))
            .limit(5) // Limitar a las 5 primeras
            .collect(Collectors.toList());

        // 3. Agregar los datos al modelo para Thymeleaf
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("expiredCount", expiredCount);
        model.addAttribute("recentTasks", recentTasks);

        return "dashboard"; // Retorna templates/dashboard.html
    }
}