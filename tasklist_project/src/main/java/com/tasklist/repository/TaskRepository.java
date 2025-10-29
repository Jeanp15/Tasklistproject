package com.tasklist.repository;

import com.tasklist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Método para obtener tareas cuyo estado NO sea el especificado
    List<Task> findByStatusIsNot(Task.Status status);
}