package com.tasklist.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tasklist.model.Task;
import com.tasklist.model.Team;
import com.tasklist.repository.TaskRepository;
import com.tasklist.repository.TeamRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    // ===== Listar todas las tareas =====
    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        
        // CORRECCIÓN: Solo añadir "newTask" si no viene de un error de validación para preservar los datos del formulario
        if (!model.containsAttribute("newTask")) {
             model.addAttribute("newTask", new Task());
        }
       
        model.addAttribute("priorities", Task.Priority.values());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("teams", teamRepository.findByActiveTrue()); 
        
        return "tasks";
    }

    // ===== MANEJAR EL ENVÍO DEL FORMULARIO DE NUEVA TAREA (FINAL) =====
    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute("newTask") Task task, 
                            BindingResult br,
                            @RequestParam(value = "teamId", required = false) Long teamId,
                            Model model) {
        
        if (br.hasErrors()) {
            // CORRECCIÓN: Setear el flag para que el JavaScript reabra el modal
            model.addAttribute("showNewTaskModal", true);
            
            // Recargamos los datos faltantes para que la vista se renderice
            model.addAttribute("tasks", taskRepository.findAll()); 
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findByActiveTrue());

            // Retornar la vista directamente para mostrar errores en el modal
            return "tasks"; 
        }

        // Asignar el equipo si se seleccionó uno
        if (teamId != null) {
            Optional<Team> teamOpt = teamRepository.findById(teamId);
            teamOpt.ifPresent(task::setEquipo);
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // ===== Formulario para editar tarea (Ahora como Fragmento Modal) =====
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findByActiveTrue());

            // Formatear fecha para el input
            String dueDateStr = "";
            if (task.getDueDate() != null) {
                dueDateStr = task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            model.addAttribute("dueDateStr", dueDateStr);

            // Retorna el fragmento de Thymeleaf, cargado por AJAX en el modal.
            return "edit-task :: taskEditForm";
        }
        return "redirect:/tasks";
    }

    // ===== Eliminar tarea (Eliminación Lógica: Cambia el Status a Completada) =====
    @PostMapping("/delete/{id}") 
    public String deleteTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            // Cambiamos el estado de la tarea a Completada
            task.setStatus(Task.Status.Completada); 
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }
}