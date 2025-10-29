// src/main/java/com/tasklist/controller/TaskController.java
package com.tasklist.controller;

import java.time.LocalDate;
// ... (otros imports)
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

    // ===== Listar todas las tareas (se actualizó en la respuesta anterior) =====
    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        
        // Objetos necesarios para el modal de "Nueva Tarea" (ya se hizo en la respuesta anterior)
        model.addAttribute("newTask", new Task());
        model.addAttribute("priorities", Task.Priority.values());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("teams", teamRepository.findAll());
        
        return "tasks";
    }

    /* EL MÉTODO addForm() FUE ELIMINADO en la respuesta anterior */

    // ... (Método addSubmit)

    // ===== Formulario para editar tarea (Ahora como Fragmento Modal) =====
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findAll());

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
    @GetMapping("/delete/{id}")
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