package com.tasklist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Equipos")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
    private String descripcion;

    @Size(max = 255, message = "La lista de miembros no puede tener más de 255 caracteres")
    private String miembros;

    @Min(value = 0, message = "El progreso no puede ser menor que 0")
    @Max(value = 100, message = "El progreso no puede ser mayor que 100")
    private int progreso;

    private boolean active = true;

    @OneToMany(mappedBy = "equipo")
    @JsonIgnore
    private List<Task> tareas;

    // ===== GETTERS y SETTERS =====
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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Task> getTareas() { return tareas; }
    public void setTareas(List<Task> tareas) { this.tareas = tareas; }
}
