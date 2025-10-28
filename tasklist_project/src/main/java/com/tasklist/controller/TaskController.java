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
        return "tasks";
    }

    // ===== Formulario para agregar tarea =====
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", Task.Priority.values());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("teams", teamRepository.findAll());
        return "add-task";
    }

    // ===== Guardar nueva tarea =====
    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute Task task,
                            BindingResult br,
                            Model model,
                            @RequestParam(value = "teamId", required = false) Long teamId,
                            @RequestParam(value = "dueDateStr", required = false) String dueDateStr) {

        // Convertir fecha de String a LocalDate
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            task.setDueDate(LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        if (br.hasErrors()) {
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "add-task";
        }

        if (teamId != null) {
            teamRepository.findById(teamId).ifPresent(task::setEquipo);
        } else {
            task.setEquipo(null);
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // ===== Formulario para editar tarea =====
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

            return "edit-task";
        }
        return "redirect:/tasks";
    }

    // ===== Guardar cambios de edición =====
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute Task task,
                             BindingResult br,
                             Model model,
                             @RequestParam(value = "teamId", required = false) Long teamId,
                             @RequestParam(value = "dueDateStr", required = false) String dueDateStr) {

        // Convertir fecha de String a LocalDate
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            task.setDueDate(LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            task.setDueDate(null);
        }

        if (br.hasErrors()) {
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "edit-task";
        }

        task.setId(id);

        if (teamId != null) {
            teamRepository.findById(teamId).ifPresent(task::setEquipo);
        } else {
            task.setEquipo(null);
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // ===== Eliminar tarea =====
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
}