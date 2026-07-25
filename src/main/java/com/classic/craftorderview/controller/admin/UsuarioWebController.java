package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.model.dto.request.UsuarioRequestDto;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDto;
import com.classic.craftorderview.services.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UsuarioWebController {

    private final IUsuarioService usuarioService;

    public UsuarioWebController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tituloPagina", "Artesanos");
        List<UsuarioResponseDto> artesanos = usuarioService.listar().stream()
                .filter(u -> "ARTESANO".equals(u.getRol()))
                .toList();
        model.addAttribute("lista", artesanos);
        UsuarioRequestDto nuevo = new UsuarioRequestDto();
        nuevo.setRol("ARTESANO");
        model.addAttribute("artesano", nuevo);
        return "/admin/listar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UsuarioRequestDto artesano) {
        usuarioService.crear(artesano);
        return "redirect:/admin";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return "redirect:/admin";
    }
}
