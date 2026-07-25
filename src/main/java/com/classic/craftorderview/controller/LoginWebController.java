package com.classic.craftorderview.controller;

import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDto;
import com.classic.craftorderview.services.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginWebController {

    private final IUsuarioService usuarioService;

    public LoginWebController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("error", null);
        return "plantilla/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute LoginRequestDTO dto,
                                HttpSession session,
                                Model model) {
        try {
            UsuarioResponseDto usuario = usuarioService.autenticar(dto);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol());

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
            model.addAttribute("error",
                "Credenciales incorrectas. Intente nuevamente.");
            return "plantilla/login";
        }
    }
}
