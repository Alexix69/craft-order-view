package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.CambiarContrasenaRequestDTO;
import com.classic.craftorderview.services.UsuarioApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/cambiar-contrasena")
public class AdminCambiarContrasenaWebController {

    private final UsuarioApiService usuarioService;

    public AdminCambiarContrasenaWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrar(Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Cambiar contraseña");
        return Vistas.ADMIN_CAMBIAR_CONTRASENA;
    }

    @PostMapping
    public String procesar(@ModelAttribute CambiarContrasenaRequestDTO dto,
                            Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            Long usuarioId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
            usuarioService.cambiarContrasena(usuarioId, dto);
            session.setAttribute(ModelAtributos.MENSAJE_EXITO, "Contraseña actualizada correctamente");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Cambiar contraseña");
            model.addAttribute("error", e.getMessage());
            return Vistas.ADMIN_CAMBIAR_CONTRASENA;
        }
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/tablero"
                    : "redirect:/admin/dashboard";
        }
        return null;
    }
}
