package com.classic.craftorderview.controller;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import com.classic.craftorderview.services.UsuarioApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginWebController {

    private final UsuarioApiService usuarioService;

    public LoginWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if ("ADMIN".equals(rol)) {
            return "redirect:/admin/kanban";
        }
        if ("ARTESANO".equals(rol)) {
            return "redirect:/artesano/tablero";
        }

        model.addAttribute("error", null);
        return Vistas.LOGIN;
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute LoginRequestDTO dto,
                                HttpSession session,
                                Model model) {
        try {
            UsuarioResponseDTO usuario = usuarioService.autenticar(dto);
            session.setAttribute(ModelAtributos.SESSION_USUARIO_ID, usuario.getId());
            session.setAttribute(ModelAtributos.SESSION_USUARIO_NOMBRE, usuario.getNombre());
            session.setAttribute(ModelAtributos.SESSION_USUARIO_ROL, usuario.getRol());

            if (Boolean.TRUE.equals(usuario.getPrimerLogin())) {
                session.setAttribute(ModelAtributos.SESSION_PRIMER_LOGIN, true);
                return "redirect:/cambiar-contrasena";
            }

            if ("ADMIN".equals(usuario.getRol())) {
                return "redirect:/admin/kanban";
            } else if ("ARTESANO".equals(usuario.getRol())) {
                return "redirect:/artesano/tablero";
            } else {
                model.addAttribute("error",
                    "Rol no reconocido. Contacte al administrador.");
                return Vistas.LOGIN;
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return Vistas.LOGIN;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
