// src/main/java/com/tasklist/model/Team.java
package com.tasklist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Equipos")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String miembros;
    private int progreso;

    // ----- CAMPO NUEVO PARA ELIMINACIÓN LÓGICA -----
    private boolean active = true;

    @OneToMany(mappedBy = "equipo")
    @JsonIgnore // CORRECCIÓN CLAVE: Esto soluciona errores de serialización/JPA que afectan a la carga de /teams y /tasks/edit.
    private List<Task> tareas;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMiembros() { return miembros; }
    public void setMiembros(String miembros) { this.miembros = miembros; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    public List<Task> getTareas() { return tareas; }
    public void setTareas(List<Task> tareas) { this.tareas = tareas; }
    
    // ----- GETTER Y SETTER NUEVOS -----
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}