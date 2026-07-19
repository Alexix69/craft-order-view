package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.model.dto.request.TipoMuebleRequestDTO;
import com.classic.craftorderview.services.TipoMuebleApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/tiposmueble")
public class TipoMuebleWebController {

    private final TipoMuebleApiService tipoMuebleService;

    public TipoMuebleWebController(TipoMuebleApiService tipoMuebleService) {
        this.tipoMuebleService = tipoMuebleService;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("tiposMueble",
                tipoMuebleService.listar());
        } catch (Exception e) {
            model.addAttribute("tiposMueble", List.of());
            model.addAttribute("errorConexion",
                "No se pudo conectar con el servidor. Intente más tarde.");
        }
        model.addAttribute("tituloPagina", "Tipos de Mueble");
        return "admin/tiposmueble/listar";
    }

    @PostMapping
    public String crear(@ModelAttribute TipoMuebleRequestDTO dto) {
        tipoMuebleService.crear(dto);
        return "redirect:/admin/tiposmueble";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id) {
        tipoMuebleService.desactivar(id);
        return "redirect:/admin/tiposmueble";
    }
}
