// src/main/java/com/tasklist/repository/TeamRepository.java
package com.tasklist.repository;

import com.tasklist.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // Método para obtener solo equipos activos
    List<Team> findByActiveTrue();
}