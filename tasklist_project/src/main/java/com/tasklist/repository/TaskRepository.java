package com.tasklist.repository;

import com.tasklist.model.Task;
import com.tasklist.model.Usuario; // Importar Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Método antiguo (puedes dejarlo o borrarlo si no se usa)
    List<Task> findByStatusIsNot(Task.Status status);

    // --- NUEVO MÉTODO: Buscar por Usuario ---
    List<Task> findByUsuario(Usuario usuario);
}