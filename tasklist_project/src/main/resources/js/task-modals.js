// src/main/resources/js/task-modals.js

document.addEventListener('DOMContentLoaded', function() {
    const newTaskModalElement = document.getElementById('newTaskModal');
    const editTaskModalElement = document.getElementById('editTaskModal');
    const editTaskModalBody = document.getElementById('editTaskModalBody');


    // =======================================================
    // 1. LÓGICA PARA REABRIR EL MODAL 'NUEVA TAREA' EN ERROR
    // =======================================================
    if (newTaskModalElement) {
        const shouldShowNewTask = newTaskModalElement.getAttribute('data-show-on-error') === 'true';

        if (shouldShowNewTask) {
            const newTaskModal = new bootstrap.Modal(newTaskModalElement);
            newTaskModal.show();
            newTaskModalElement.querySelector('form').classList.add('was-validated');
        }
    }


    // =======================================================
    // 2. LÓGICA PARA CARGAR EL MODAL 'EDITAR TAREA' POR AJAX
    // =======================================================
    if (editTaskModalElement) {
        editTaskModalElement.addEventListener('show.bs.modal', async function (event) {

            // Botón que disparó el modal y contiene el ID
            const button = event.relatedTarget;
            const taskId = button.getAttribute('data-task-id');
            const url = `/tasks/edit/${taskId}`; // URL CORRECTA
            
            // Muestra spinner
            editTaskModalBody.innerHTML = `
                <div class="text-center p-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                </div>
            `;

            try {
                const response = await fetch(url);
                
                if (response.ok) {
                    const fragmentHtml = await response.text();
                    editTaskModalBody.innerHTML = fragmentHtml; 
                    
                    // Re-inicializar la validación para el formulario cargado
                    const form = editTaskModalBody.querySelector('form');
                    if (form) {
                        form.addEventListener('submit', function(e) {
                            if (!this.checkValidity()) {
                                e.preventDefault();
                                e.stopPropagation();
                            }
                            this.classList.add('was-validated');
                        }, false);
                    }
                } else {
                    editTaskModalBody.innerHTML = '<div class="alert alert-danger">Error al cargar el formulario de edición: ' + response.statusText + '</div>';
                }
            } catch (error) {
                console.error('Error de red al cargar el formulario:', error);
                editTaskModalBody.innerHTML = '<div class="alert alert-danger">Error de conexión. Intente de nuevo.</div>';
            }
        });
    }

    // =======================================================
    // 3. VALIDACIÓN MANUAL DE FORMULARIOS (GLOBAL)
    // =======================================================
    // Asegura que todos los formularios de los modales tengan la validación
    const allForms = document.querySelectorAll('.modal form');
    allForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            this.classList.add('was-validated');
        }, false);
    });

});