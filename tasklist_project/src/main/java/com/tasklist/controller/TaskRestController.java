package com.tasklist.controller;

import com.tasklist.model.Task;
import com.tasklist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAllTasks() {
        // CORRECCIÓN: Devolver solo las tareas que NO estén en estado Completada
        return taskRepository.findByStatusIsNot(Task.Status.Completada);
    }
}