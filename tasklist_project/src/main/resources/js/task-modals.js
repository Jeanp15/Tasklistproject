document.addEventListener('DOMContentLoaded', function() {
  const editTaskModal = document.getElementById('editTaskModal');
  const editTaskModalBody = document.getElementById('editTaskModalBody');
  
  // Listener para cargar el formulario de edición de Tarea por AJAX
  if (editTaskModal) {
    editTaskModal.addEventListener('show.bs.modal', async function (event) {
      // Botón que disparó el modal y contiene el ID
      const button = event.relatedTarget;
      const taskId = button.getAttribute('data-task-id');
      const url = `/tasks/edit/${taskId}`; 
      
      // Muestra spinner
      editTaskModalBody.innerHTML = `
        <div class="text-center p-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
        </div>
      `;

      try {
          // Cargar el fragmento del formulario desde el controlador
          const response = await fetch(url);
          
          if (response.ok) {
              const fragmentHtml = await response.text();
              
              // Carga el contenido en el modal
              editTaskModalBody.innerHTML = fragmentHtml; 
          } else {
              editTaskModalBody.innerHTML = '<div class="alert alert-danger">Error al cargar el formulario de edición.</div>';
          }
      } catch (error) {
          console.error('Error de red al cargar el formulario:', error);
          editTaskModalBody.innerHTML = '<div class="alert alert-danger">Error de conexión. Intente de nuevo.</div>';
      }
    });
  }
});