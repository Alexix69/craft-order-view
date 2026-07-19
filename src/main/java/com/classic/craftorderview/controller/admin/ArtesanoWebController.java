package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.model.dto.request.UsuarioRequestDTO;
import com.classic.craftorderview.services.UsuarioApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/artesanos")
public class ArtesanoWebController {

    private final UsuarioApiService usuarioService;

    public ArtesanoWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tituloPagina", "Usuarios");
        try {
            model.addAttribute("usuarios",
                usuarioService.listarPorRol("ARTESANO"));
        } catch (Exception e) {
            model.addAttribute("usuarios", List.of());
            model.addAttribute("errorConexion",
                "No se pudo conectar con el servidor. Intente más tarde.");
        }
        return "admin/artesanos/listar";
    }

    @PostMapping
    public String crear(@ModelAttribute UsuarioRequestDTO dto) {
        usuarioService.crear(dto);
        return "redirect:/admin/artesanos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return "redirect:/admin/artesanos";
    }
}
