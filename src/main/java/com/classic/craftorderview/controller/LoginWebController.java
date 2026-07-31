package com.classic.craftorderview.controller;

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
        String rol = (String) session.getAttribute("usuarioRol");
        if ("ADMIN".equals(rol)) {
            return "redirect:/admin/dashboard";
        }
        if ("ARTESANO".equals(rol)) {
            return "redirect:/artesano/tablero";
        }

        model.addAttribute("error", null);
        return "plantilla/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute LoginRequestDTO dto,
                                HttpSession session,
                                Model model) {
        try {
            UsuarioResponseDTO usuario = usuarioService.autenticar(dto);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol());

            if (Boolean.TRUE.equals(usuario.getPrimerLogin())) {
                session.setAttribute("primerLogin", true);
                return "redirect:/cambiar-contrasena";
            }

            if ("ADMIN".equals(usuario.getRol())) {
                return "redirect:/admin/dashboard";
            } else if ("ARTESANO".equals(usuario.getRol())) {
                return "redirect:/artesano/tablero";
            } else {
                model.addAttribute("error",
                    "Rol no reconocido. Contacte al administrador.");
                return "plantilla/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "plantilla/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
