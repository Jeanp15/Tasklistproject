package com.tasklist.controller;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.tasklist.model.Task;
import com.tasklist.model.Team;
import com.tasklist.model.Usuario;
import com.tasklist.repository.TaskRepository;
import com.tasklist.repository.TeamRepository;
import com.tasklist.repository.UsuarioRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método auxiliar para obtener el usuario actual logueado
    private Usuario getUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByUsername(auth.getName());
    }

    @GetMapping
    public String listTasks(Model model) {
        // CORRECCIÓN: Buscar solo las tareas del usuario logueado
        Usuario usuario = getUsuarioLogueado();
        List<Task> misTareas = taskRepository.findByUsuario(usuario);
        
        model.addAttribute("tasks", misTareas);
        
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

    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute("newTask") Task task, 
                            BindingResult br,
                            @RequestParam(value = "teamId", required = false) Long teamId,
                            Model model) {
        
        Usuario usuario = getUsuarioLogueado(); // Obtener usuario actual

        if (br.hasErrors()) {
            model.addAttribute("showNewTaskModal", true);
            // Al recargar por error, mostrar solo las tareas del usuario
            model.addAttribute("tasks", taskRepository.findByUsuario(usuario)); 
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findByActiveTrue());
            return "tasks"; 
        }

        if (teamId != null) {
            Optional<Team> teamOpt = teamRepository.findById(teamId);
            teamOpt.ifPresent(task::setEquipo);
        }

        // ASIGNAR EL DUEÑO DE LA TAREA
        task.setUsuario(usuario);

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        Usuario usuario = getUsuarioLogueado();

        // Seguridad extra: Verificar que la tarea sea del usuario logueado
        if (taskOpt.isPresent() && taskOpt.get().getUsuario().getId().equals(usuario.getId())) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            model.addAttribute("priorities", Task.Priority.values());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("teams", teamRepository.findByActiveTrue()); 

            String dueDateStr = "";
            if (task.getDueDate() != null) {
                dueDateStr = task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            model.addAttribute("dueDateStr", dueDateStr);

            return "edit-task :: taskEditForm";
        }
        return "redirect:/tasks"; // Si no es su tarea, lo devolvemos
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, 
                       @Valid @ModelAttribute Task task, 
                       BindingResult br,
                       @RequestParam(value = "teamId", required = false) Long teamId, 
                       Model model) { 

        if (br.hasErrors()) {
            return "redirect:/tasks";
        }
        
        // Asegurar que no cambie de dueño al editar
        Optional<Task> dbTask = taskRepository.findById(id);
        Usuario usuario = getUsuarioLogueado();

        if(dbTask.isPresent() && dbTask.get().getUsuario().getId().equals(usuario.getId())) {
            task.setId(id);
            task.setUsuario(usuario); // Mantener dueño
            
            if (teamId != null && teamId != 0) { 
                Optional<Team> teamOpt = teamRepository.findById(teamId);
                teamOpt.ifPresent(task::setEquipo);
            } else {
                task.setEquipo(null); 
            }
            taskRepository.save(task);
        }

        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}") 
    public String deleteTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        Usuario usuario = getUsuarioLogueado();

        // Solo eliminar si es dueño
        if (taskOpt.isPresent() && taskOpt.get().getUsuario().getId().equals(usuario.getId())) {
            Task task = taskOpt.get();
            task.setStatus(Task.Status.Completada); 
            taskRepository.save(task);
        }
        return "redirect:/tasks";
    }
}