package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.model.dto.request.AcabadoPorcentajeRequestDTO;
import com.classic.craftorderview.model.dto.request.MaterialRequestDTO;
import com.classic.craftorderview.services.MaterialApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/materiales")
public class MaterialWebController {

	private static final List<String> TIPOS_ACABADO_DEFAULT = Arrays.asList("LACA", "PINTURA", "NATURAL");

	private final MaterialApiService materialService;

	public MaterialWebController(MaterialApiService materialService) {
		this.materialService = materialService;
	}

	@GetMapping
	public String listar(Model model) {
		try {
			model.addAttribute("materiales", materialService.listar());
		} catch (Exception e) {
			model.addAttribute("materiales", List.of());
			model.addAttribute("errorConexion", "No se pudo conectar con el servidor. Intente más tarde.");
		}
		try {
			model.addAttribute("acabados", materialService.listarAcabados());
		} catch (Exception e) {
			model.addAttribute("acabados", List.of());
		}
		model.addAttribute("tiposAcabadoDefault", TIPOS_ACABADO_DEFAULT);
		model.addAttribute("tituloPagina", "Materiales");
		return "admin/materiales/listar";
	}

	@PostMapping
	public String crear(@ModelAttribute MaterialRequestDTO dto) {
		materialService.crear(dto);
		return "redirect:/admin/materiales";
	}

	@PostMapping("/{id}/desactivar")
	public String desactivar(@PathVariable Long id) {
		materialService.desactivar(id);
		return "redirect:/admin/materiales";
	}

	@PostMapping("/acabados/{tipo}/porcentaje")
	public String actualizarPorcentaje(@PathVariable String tipo, @ModelAttribute AcabadoPorcentajeRequestDTO dto) {
		materialService.actualizarPorcentajeAcabado(tipo, dto);
		return "redirect:/admin/materiales";
	}
}
