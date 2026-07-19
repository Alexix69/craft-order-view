document.addEventListener('DOMContentLoaded', function () {

  // 1. SIDEBAR TOGGLE
  var toggle = document.querySelector('.js-sidebar-toggle');
  var sidebar = document.getElementById('sidebar');
  if (toggle && sidebar) {
    toggle.addEventListener('click', function () {
      sidebar.classList.toggle('d-none');
    });
  }

  // 2. MODALES BOOTSTRAP — limpieza de backdrop residual
  document.addEventListener('hidden.bs.modal', function () {
    document.body.classList.remove('modal-open');
    document.body.style.removeProperty('overflow');
    document.body.style.removeProperty('padding-right');
    document.querySelectorAll('.modal-backdrop').forEach(function (b) {
      b.remove();
    });
  });

});
