package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardWebController {

    @GetMapping
    public String mostrar(Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Dashboard");

        String mensajeExito = (String) session.getAttribute(ModelAtributos.MENSAJE_EXITO);
        if (mensajeExito != null) {
            model.addAttribute(ModelAtributos.MENSAJE_EXITO, mensajeExito);
            session.removeAttribute(ModelAtributos.MENSAJE_EXITO);
        }

        return Vistas.ADMIN_DASHBOARD;
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
