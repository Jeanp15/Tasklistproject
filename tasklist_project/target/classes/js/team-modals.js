document.addEventListener('DOMContentLoaded', function() {
  const editTeamModal = document.getElementById('editTeamModal');
  const editTeamModalBody = document.getElementById('editTeamModalBody');
  
  // Listener para cargar el formulario de edición por AJAX
  if (editTeamModal) {
    editTeamModal.addEventListener('show.bs.modal', async function (event) {
      // Botón que disparó el modal y contiene el ID
      const button = event.relatedTarget;
      const teamId = button.getAttribute('data-team-id');
      const url = `/teams/edit/${teamId}`; 
      
      // Muestra spinner
      editTeamModalBody.innerHTML = `
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
              editTeamModalBody.innerHTML = fragmentHtml; 

              // Re-inicializar la validación para el formulario cargado
              const form = editTeamModalBody.querySelector('form');
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
              // Reportar error de HTTP
              editTeamModalBody.innerHTML = '<div class="alert alert-danger">Error al cargar el formulario de edición: ' + response.statusText + '</div>';
          }
      } catch (error) {
          console.error('Error de red al cargar el formulario:', error);
          editTeamModalBody.innerHTML = '<div class="alert alert-danger">Error de conexión. Intente de nuevo.</div>';
      }
    });
  }
});