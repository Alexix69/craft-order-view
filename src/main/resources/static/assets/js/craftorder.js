document.addEventListener('DOMContentLoaded', function () {

    var toggle = document.querySelector('.js-sidebar-toggle');
    var sidebar = document.getElementById('sidebar');
    if (toggle && sidebar) {
        toggle.addEventListener('click', function () {
            sidebar.classList.toggle('d-none');
        });
    }

    document.addEventListener('hidden.bs.modal', function () {
        document.body.classList.remove('modal-open');
        document.body.style.removeProperty('overflow');
        document.body.style.removeProperty('padding-right');
        document.querySelectorAll('.modal-backdrop').forEach(function (b) {
            b.remove();
        });
    });

});

async function buscarPorToken(e) {
    e.preventDefault();
    const token = document.getElementById('inputToken').value.trim();
    if (!token) return;

    try {
        const response = await fetch(
            `/admin/buscar-token?token=${encodeURIComponent(token)}`);

        if (response.ok) {
            const data = await response.json();
            if (data.tieneOrden) {
                window.location.href = `/admin/kanban/${data.ordenId}`;
            } else {
                window.location.href =
                    `/admin/cotizaciones/${data.cotizacionId}`;
            }
        } else {
            mostrarToastBuscador('No se encontró ningún pedido con ese token.');
        }
    } catch (err) {
        mostrarToastBuscador('Error al buscar. Intenta de nuevo.');
    }
}

function mostrarToastBuscador(mensaje) {
    const toast = document.getElementById('navToastError');
    if (toast) {
        document.getElementById('navToastMensaje').textContent = mensaje;
        new bootstrap.Toast(toast, { delay: 4000 }).show();
    } else {
        alert(mensaje);
    }
}
