/**
 * craftorder-kanban.js
 * Lógica de drag and drop para el tablero Kanban.
 * Requiere Bootstrap 5 cargado antes de este script.
 */

const ETAPAS = [
    'ASIGNADO', 'CORTE', 'ENSAMBLADO',
    'ACABADOS', 'CONTROL_CALIDAD', 'LISTO_ENTREGA'
];

// Rol del usuario actual (inyectado por Thymeleaf en data-rol del contenedor)
let ROL_ACTUAL = null;
let USUARIO_ID = null;

// Datos del drag en curso
let _ordenId = null;
let _etapaOrigen = null;
let _etapaDestino = null;

/**
 * Inicializa el Kanban. Llamar en DOMContentLoaded.
 * @param {string} rol  - "ADMIN" o "ARTESANO"
 * @param {number} userId - id del usuario en sesión
 */
function initKanban(rol, userId) {
    ROL_ACTUAL = rol;
    USUARIO_ID = userId;

    // Tarjetas arrastrables
    document.querySelectorAll('.kanban-tarjeta[draggable="true"]')
        .forEach(tarjeta => {
            tarjeta.addEventListener('dragstart', onDragStart);
            tarjeta.addEventListener('dragend', onDragEnd);
        });

    // Columnas receptoras
    document.querySelectorAll('.kanban-columna')
        .forEach(columna => {
            columna.addEventListener('dragover', onDragOver);
            columna.addEventListener('dragleave', onDragLeave);
            columna.addEventListener('drop', onDrop);
        });

    // Los enlaces dentro de una tarjeta (nombre, ícono de detalle) no deben
    // iniciar un drag — permiten el click normal para navegar al detalle.
    document.querySelectorAll('.kanban-tarjeta a').forEach(enlace => {
        enlace.addEventListener('mousedown', function (e) {
            e.stopPropagation();
        });
        enlace.addEventListener('dragstart', function (e) {
            e.preventDefault();
            e.stopPropagation();
        });
    });
}

function onDragStart(e) {
    _ordenId = this.dataset.ordenId;
    _etapaOrigen = this.dataset.etapa;
    this.classList.add('opacity-50');
    e.dataTransfer.effectAllowed = 'move';
}

function onDragEnd(e) {
    this.classList.remove('opacity-50');
    document.querySelectorAll('.kanban-columna')
        .forEach(c => c.classList.remove('kanban-drop-target'));
}

function onDragOver(e) {
    e.preventDefault();
    this.classList.add('kanban-drop-target');
    e.dataTransfer.dropEffect = 'move';
}

function onDragLeave(e) {
    this.classList.remove('kanban-drop-target');
}

function onDrop(e) {
    e.preventDefault();
    this.classList.remove('kanban-drop-target');
    _etapaDestino = this.dataset.etapa;

    if (!_etapaDestino || _etapaDestino === _etapaOrigen) return;

    const indiceOrigen = ETAPAS.indexOf(_etapaOrigen);
    const indiceDestino = ETAPAS.indexOf(_etapaDestino);
    const esRetroceso = indiceDestino < indiceOrigen;

    // Bloquear si artesano intenta retroceder
    if (ROL_ACTUAL === 'ARTESANO' && esRetroceso) return;

    // Bloquear si artesano intenta mover a LISTO_ENTREGA
    if (ROL_ACTUAL === 'ARTESANO' &&
        _etapaDestino === 'LISTO_ENTREGA') return;

    // Retroceso del admin → modal con motivo obligatorio
    if (ROL_ACTUAL === 'ADMIN' && esRetroceso) {
        abrirModalRetroceso(_etapaOrigen, _etapaDestino);
        return;
    }

    // LISTO_ENTREGA → modal de confirmación simple
    if (_etapaDestino === 'LISTO_ENTREGA') {
        abrirModalListoEntrega();
        return;
    }

    // Avance normal → ejecutar directamente con spinner
    if (ROL_ACTUAL === 'ARTESANO') {
        // Solo puede avanzar exactamente una etapa
        if (indiceDestino !== indiceOrigen + 1) {
            // Salto no permitido — no hacer nada, la tarjeta vuelve sola
            return;
        }
        ejecutarCambioEstadoArtesano(_ordenId, _etapaDestino);
    } else {
        ejecutarCambioEstado(_ordenId, _etapaDestino, null);
    }
}

function abrirModalRetroceso(etapaOrigen, etapaDestino) {
    document.getElementById('modalRetroceso').dataset.etapaDestino =
        etapaDestino;
    document.getElementById('retroceso-desde').textContent =
        etapaOrigen.replace('_', ' ');
    document.getElementById('retroceso-hasta').textContent =
        etapaDestino.replace('_', ' ');
    document.getElementById('motivoRetroceso').value = '';
    new bootstrap.Modal(
        document.getElementById('modalRetroceso')).show();
}

function confirmarRetroceso() {
    const motivo = document.getElementById('motivoRetroceso').value.trim();
    if (!motivo) {
        document.getElementById('motivoRetroceso').classList.add('is-invalid');
        return;
    }
    const etapaDestino = document.getElementById('modalRetroceso')
        .dataset.etapaDestino;
    bootstrap.Modal.getInstance(
        document.getElementById('modalRetroceso')).hide();
    ejecutarCambioEstado(_ordenId, etapaDestino, motivo);
}

function abrirModalListoEntrega() {
    new bootstrap.Modal(
        document.getElementById('modalListoEntrega')).show();
}

function confirmarListoEntrega() {
    bootstrap.Modal.getInstance(
        document.getElementById('modalListoEntrega')).hide();
    ejecutarCambioEstado(_ordenId, 'LISTO_ENTREGA', null);
}

async function ejecutarCambioEstado(ordenId, estadoNuevo, motivo) {
    mostrarSpinner();
    try {
        const response = await fetch(
            `/admin/kanban/${ordenId}/estado`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                estadoNuevo,
                motivo,
                realizadoPor: USUARIO_ID
            })
        });
        if (response.ok) {
            window.location.reload();
        } else {
            const error = await response.json();
            ocultarSpinner();
            mostrarToastError(error.mensaje || 'Error al mover la tarjeta');
        }
    } catch (err) {
        ocultarSpinner();
        mostrarToastError('Error de conexión. Intenta de nuevo.');
    }
}

// Versión para el artesano (misma lógica, diferente endpoint)
async function ejecutarCambioEstadoArtesano(ordenId, estadoNuevo) {
    mostrarSpinner();
    try {
        const response = await fetch(
            `/artesano/kanban/${ordenId}/estado`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                estadoNuevo,
                realizadoPor: USUARIO_ID
            })
        });
        if (response.ok) {
            window.location.reload();
        } else {
            const error = await response.json();
            ocultarSpinner();
            mostrarToastError(error.mensaje || 'Error al avanzar la etapa');
        }
    } catch (err) {
        ocultarSpinner();
        mostrarToastError('Error de conexión. Intenta de nuevo.');
    }
}

function mostrarSpinner() {
    document.getElementById('kanbanSpinner').classList.remove('d-none');
}

function ocultarSpinner() {
    document.getElementById('kanbanSpinner').classList.add('d-none');
}

function mostrarToastError(mensaje) {
    const toast = document.getElementById('kanbanToastError');
    document.getElementById('kanbanToastMensaje').textContent = mensaje;
    new bootstrap.Toast(toast, { delay: 4000 }).show();
}

// ── Buscador client-side ────────────────────────────────────────────────────

/**
 * Filtra las tarjetas del Kanban por texto y período.
 * Opera sobre el DOM ya renderizado — sin peticiones al back.
 */
function filtrarKanban() {
    const textoBusqueda = (document.getElementById('kanbanBusqueda')?.value || '')
        .toLowerCase().trim();
    const periodo = document.getElementById('kanbanPeriodo')?.value || 'todos';

    const ahora = new Date();
    let fechaLimite = null;

    if (periodo === 'hoy') {
        fechaLimite = new Date(ahora.getFullYear(),
            ahora.getMonth(),
            ahora.getDate());
    } else if (periodo !== 'todos') {
        const dias = parseInt(periodo);
        fechaLimite = new Date(ahora.getTime() - dias * 24 * 60 * 60 * 1000);
    }

    let totalVisibles = 0;

    document.querySelectorAll('.kanban-tarjeta').forEach(tarjeta => {
        const textoTarjeta = (tarjeta.dataset.busqueda || '').toLowerCase();
        const fechaStr = tarjeta.dataset.fecha || '';
        const fechaTarjeta = fechaStr ? new Date(fechaStr) : null;

        const pasaTexto = !textoBusqueda ||
            textoTarjeta.includes(textoBusqueda);

        const pasaPeriodo = !fechaLimite || !fechaTarjeta ||
            fechaTarjeta >= fechaLimite;

        const visible = pasaTexto && pasaPeriodo;
        tarjeta.style.display = visible ? '' : 'none';
        if (visible) totalVisibles++;
    });

    // Actualizar contador
    const contador = document.getElementById('kanbanContador');
    if (contador) {
        const tienesFiltro = textoBusqueda || periodo !== 'todos';
        contador.textContent = tienesFiltro
            ? `${totalVisibles} orden(es) encontrada(s)`
            : '';
    }
}

function limpiarFiltroKanban() {
    const input = document.getElementById('kanbanBusqueda');
    const select = document.getElementById('kanbanPeriodo');
    if (input) input.value = '';
    if (select) select.value = 'todos';
    filtrarKanban();
}

// Sincronizar scroll horizontal entre cabeceras y cuerpo del Kanban
document.addEventListener('DOMContentLoaded', function () {
    const headers = document.getElementById('kanbanHeaders');
    const body = document.getElementById('kanban');

    if (headers && body) {
        body.addEventListener('scroll', function () {
            headers.scrollLeft = body.scrollLeft;
        });
    }
});

function igualarAlturaColumnas() {
    const columnas = document.querySelectorAll('.kanban-columna');
    columnas.forEach(c => c.style.minHeight = '');
    let maxAltura = 0;
    columnas.forEach(c => {
        if (c.scrollHeight > maxAltura) maxAltura = c.scrollHeight;
    });
    columnas.forEach(c => c.style.minHeight = maxAltura + 'px');
}

document.addEventListener('DOMContentLoaded', function () {
    igualarAlturaColumnas();
});