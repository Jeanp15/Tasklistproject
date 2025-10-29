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
        
        if (!model.containsAttribute("newTask")) {
             model.addAttribute("newTask", new Task());
        }
       
        if (!model.containsAttribute("showNewTaskModal")) {
            model.addAttribute("showNewTaskModal", false);
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
            model.addAttribute("showNewTaskModal", true);
            model.addAttribute("tasks", taskRepository.findAll()); 
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findByActiveTrue());
            return "tasks"; 
        }

        if (teamId != null) {
            Optional<Team> teamOpt = teamRepository.findById(teamId);
            teamOpt.ifPresent(task::setEquipo);
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // ===== Formulario para editar tarea (CARGA AJAX) =====
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            // Se asegura que esta lista se carga para la etiqueta <select> en el fragmento.
            model.addAttribute("teams", teamRepository.findByActiveTrue()); 

            String dueDateStr = "";
            if (task.getDueDate() != null) {
                dueDateStr = task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            model.addAttribute("dueDateStr", dueDateStr);

            // Retorna el fragmento de Thymeleaf.
            return "edit-task :: taskEditForm";
        }
        return "redirect:/tasks";
    }

    // ===== GUARDAR CAMBIOS DE TAREA (FINAL) =====
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, 
                       @Valid @ModelAttribute Task task, 
                       BindingResult br,
                       @RequestParam(value = "teamId", required = false) Long teamId, 
                       Model model) { 

        if (br.hasErrors()) {
            return "redirect:/tasks";
        }

        task.setId(id);
        
        if (teamId != null && teamId != 0) { 
            Optional<Team> teamOpt = teamRepository.findById(teamId);
            teamOpt.ifPresent(task::setEquipo);
        } else {
            task.setEquipo(null); 
        }

        taskRepository.save(task);

        return "redirect:/tasks";
    }

    // ===== Eliminar tarea (Lógica) =====
    @PostMapping("/delete/{id}") 
    public String deleteTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.Status.Completada); 
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }
}