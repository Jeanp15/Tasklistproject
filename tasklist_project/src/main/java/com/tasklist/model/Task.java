package com.tasklist.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

/**
 * Clase que representa una Tarea dentro del sistema TaskList.
 * Incluye atributos básicos como título, descripción, fecha de vencimiento,
 * prioridad, estado y relación con un equipo.
 */
@Entity
@Table(name = "Tareas")
public class Task {

    // ----- IDENTIFICADOR -----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- TÍTULO -----
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String title;

    // ----- DESCRIPCIÓN -----
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String description;

    // ----- FECHA DE VENCIMIENTO -----
    @FutureOrPresent(message = "La fecha de vencimiento debe ser hoy o futura")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Compatible con JSON
    @Column(name = "due_date")
    private LocalDate dueDate;

    // ----- PRIORIDAD -----
    @Enumerated(EnumType.STRING)
    private Priority priority;

    // ----- ESTADO -----
    @Enumerated(EnumType.STRING)
    private Status status = Status.Pendiente;

    // ----- RELACIÓN CON EQUIPO -----
    @ManyToOne
    @JoinColumn(name = "equipo_id")
    @JsonBackReference // Evita ciclos infinitos al serializar a JSON
    private Team equipo;

    // ----- ENUMERACIONES -----
    public enum Priority { Baja, Media, Alta }
    public enum Status { Pendiente, En_Progreso, Completada }

    // ----- GETTERS Y SETTERS -----
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

    // ----- MÉTODOS ADICIONALES (opcional) -----
    /**
     * Retorna un resumen corto de la tarea, útil para logs o vistas.
     */
    public String getResumen() {
        return String.format("[%s] %s - %s", status, title, priority);
    }

    /**
     * Indica si la tarea está vencida comparando la fecha actual con dueDate.
     */
    public boolean isVencida() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != Status.Completada;
    }
}
