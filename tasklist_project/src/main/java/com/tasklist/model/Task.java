package com.tasklist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

@Entity
@Table(name = "Tareas")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String title;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String description;

    @FutureOrPresent(message = "La fecha de vencimiento debe ser hoy o futura")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Pendiente;

    @ManyToOne
    @JoinColumn(name = "equipo_id")
    @JsonBackReference
    private Team equipo;

    // --- NUEVO: RELACIÓN CON USUARIO (DUEÑO DE LA TAREA) ---
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Obligatorio tener dueño
    @JsonBackReference // Evita bucles en JSON
    private Usuario usuario;

    // Enums...
    public enum Priority { Baja, Media, Alta }
    public enum Status { Pendiente, En_Progreso, Completada }

    // --- Getters y Setters ---
    // (Mantén los anteriores y agrega estos para el usuario)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Team getEquipo() { return equipo; }
    public void setEquipo(Team equipo) { this.equipo = equipo; }

    // NUEVOS GETTER Y SETTER
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getResumen() {
        return String.format("[%s] %s - %s", status, title, priority);
    }

    public boolean isVencida() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != Status.Completada;
    }
}