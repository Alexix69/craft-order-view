function mostrarErrorBack(mensaje, campo) {
  if (campo) {
    var input = document.querySelector('[name="' + campo + '"]');
    if (input) {
      input.classList.add('is-invalid');
      var feedback = input.parentElement.querySelector('.invalid-feedback');
      if (feedback) {
        feedback.textContent = mensaje;
      }
    }
    return;
  }
  var errorGeneral = document.getElementById('errorGeneral');
  if (errorGeneral) {
    errorGeneral.textContent = mensaje;
    errorGeneral.classList.remove('d-none');
  }
}

function limpiarErrores(formId) {
  var form = document.getElementById(formId);
  if (!form) {
    return;
  }
  form.reset();
  form.querySelectorAll('.is-invalid, .is-valid').forEach(function (input) {
    input.classList.remove('is-invalid', 'is-valid');
  });
  form.querySelectorAll('.invalid-feedback').forEach(function (feedback) {
    feedback.style.display = '';
  });
  var errorGeneral = document.getElementById('errorGeneral');
  if (errorGeneral) {
    errorGeneral.textContent = '';
    errorGeneral.classList.add('d-none');
  }
}

function initPasswordToggle() {
  document.querySelectorAll('.btn-toggle-password').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var targetSelector = btn.getAttribute('data-target');
      var input = document.querySelector(targetSelector);
      if (!input) {
        return;
      }
      var icono = btn.querySelector('i');
      if (input.type === 'password') {
        input.type = 'text';
        if (icono) {
          icono.classList.remove('fa-eye');
          icono.classList.add('fa-eye-slash');
        }
      } else {
        input.type = 'password';
        if (icono) {
          icono.classList.remove('fa-eye-slash');
          icono.classList.add('fa-eye');
        }
      }
    });
  });
}

function initValidacionInline() {
  var selector = 'input[required], select[required], input[pattern], ' +
    'input[minlength], input[maxlength], input[type="email"]';

  document.querySelectorAll('form').forEach(function (form) {
    form.querySelectorAll(selector).forEach(function (input) {
      input.addEventListener('input', function () {
        if (input.value) {
          validarCampoInline(input);
        }
      });
      input.addEventListener('blur', function () {
        validarCampoInline(input);
      });
    });
  });
}

function validarCampoInline(input) {
  var feedback = input.parentElement.querySelector('.invalid-feedback');
  if (!feedback) {
    var contenedor = input.closest('.mb-3, .mb-1');
    if (contenedor) {
      feedback = contenedor.querySelector('.invalid-feedback');
    }
  }

  if (!input.validity.valid) {
    input.classList.add('is-invalid');
    input.classList.remove('is-valid');
    if (feedback) {
      var mensaje = input.dataset.mensajeError || input.validationMessage;
      feedback.textContent = mensaje;
      feedback.style.display = 'block';
    }
  } else {
    input.classList.add('is-valid');
    input.classList.remove('is-invalid');
    if (feedback) {
      feedback.style.display = 'none';
    }
  }
}
