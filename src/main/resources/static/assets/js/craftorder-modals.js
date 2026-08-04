/**
 * craftorder-modals.js
 * Funciones reutilizables para modales Bootstrap y galería de imágenes.
 * Requiere Bootstrap 5 cargado antes de este script.
 */

// ── Modal de confirmación reutilizable ──────────────────────────────────────

/**
 * Abre el modal de confirmación reutilizable (#modalConfirmar).
 * @param {string} titulo  - Título del modal
 * @param {string} cuerpo  - Mensaje de confirmación
 * @param {string} accion  - URL a la que apunta el formulario al confirmar
 */
function abrirModalConfirmar(titulo, cuerpo, accion) {
    document.getElementById('modalConfirmarTitulo').textContent = titulo;
    document.getElementById('modalConfirmarCuerpo').textContent = cuerpo;
    document.getElementById('formConfirmar').action = accion;
    new bootstrap.Modal(document.getElementById('modalConfirmar')).show();
}

// ── Modal de acabado ────────────────────────────────────────────────────────

/**
 * Abre el modal de edición de porcentaje de acabado (#modalAcabado).
 * @param {string} tipo       - Tipo de acabado (LACA, PINTURA, NATURAL)
 * @param {string} porcentaje - Porcentaje actual a precargar
 */
function abrirModalAcabado(tipo, porcentaje) {
    document.getElementById('acabadoTipo').value = tipo;
    document.getElementById('acabadoPorcentaje').value = porcentaje;
    document.getElementById('formAcabado').action =
        '/admin/materiales/acabados/' + tipo + '/porcentaje';
    new bootstrap.Modal(document.getElementById('modalAcabado')).show();
}

// ── Galería de imágenes ─────────────────────────────────────────────────────

/** URL de la miniatura actualmente en preview, usada por seleccionarImagenGaleria */
let _urlPreviewActual = null;
/** Instancia Bootstrap del modal de preview actualmente abierto */
let _modalPreview = null;

/**
 * Abre el modal de preview ampliado (#modalPreviewImagen) para una URL de galería.
 * @param {string} url - URL de la imagen a previsualizar
 */
function abrirPreviewImagen(url) {
    _urlPreviewActual = url;
    document.getElementById('imgPreviewGrande').src = url;
    _modalPreview = new bootstrap.Modal(
        document.getElementById('modalPreviewImagen')
    );
    _modalPreview.show();
}

/**
 * Selecciona la imagen actualmente en preview y la aplica al formulario.
 * Limpia el input file si había uno cargado.
 */
function seleccionarImagenGaleria() {
    if (!_urlPreviewActual) return;
    document.getElementById('fotoUrl').value = _urlPreviewActual;
    document.getElementById('previewImagen').src = _urlPreviewActual;
    document.getElementById('previewContenedor').style.display = 'block';
    document.getElementById('imagenFile').value = '';
    if (_modalPreview) _modalPreview.hide();
}

/**
 * Muestra una vista previa local de una imagen seleccionada con input file.
 * No sube el archivo — solo muestra el preview con FileReader.
 * @param {HTMLInputElement} input - El input file que disparó el evento
 */
function previewImagenPropia(input) {
    if (!input.files || !input.files[0]) return;
    const reader = new FileReader();
    reader.onload = function (e) {
        document.getElementById('previewImagen').src = e.target.result;
        document.getElementById('previewContenedor').style.display = 'block';
        document.getElementById('fotoUrl').value = '';
    };
    reader.readAsDataURL(input.files[0]);
}

/**
 * Limpia la imagen seleccionada (tanto de galería como de input file).
 */
function limpiarImagen() {
    document.getElementById('fotoUrl').value = '';
    document.getElementById('imagenFile').value = '';
    document.getElementById('previewImagen').src = '';
    document.getElementById('previewContenedor').style.display = 'none';
}
